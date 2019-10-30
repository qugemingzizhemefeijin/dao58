package cg.zz.spat.dao.dbconnectionpool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 数据库连接池
 * 
 * @author chengang
 *
 */
public class ConnectionPool {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
	
	/**
	 * 可用连接池，这里使用的是栈结构，先入后出。可能会造成最先放入的连接会长期闲置。
	 */
	private Stack<Connection> AvailableConn = new Stack<>();
	
	/**
	 * 所有的连接池，包括被借出去。
	 */
	private List<Connection> Pool = new ArrayList<>();
	
	/**
	 * 数据库配置对象
	 */
	private DBConfig _Config;
	
	/**
	 * 连接池空闲状态维护对象
	 */
	private PoolState state = new PoolState();

	public ConnectionPool() {
		
	}

	public ConnectionPool(DBConfig config) {
		try {
			this._Config = config;
			//加载驱动程序
			LoadDrivers();
			//初始化
			Init();
			//注册关机事件
			RegisterExcetEven();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	/**
	 * 获得连接池所有连接的数量
	 * @return int
	 */
	public int GetAllCount() {
		return this.Pool.size();
	}

	/**
	 * 获得连接池空闲连接的数量
	 * @return int
	 */
	public int GetFreeConnCount() {
		return this.AvailableConn.size();
	}

	/**
	 * 从连接池中获取一个连接
	 * @return
	 * @throws Exception
	 */
	public synchronized Connection Get() throws Exception {
		//获取可用连接数
		int freeCount = this.AvailableConn.size();
		//刷新一下空间戳
		this.state.setNoWorkCout(freeCount);
		//空闲连接大于的话，则直接从可用集合中获取
		if (freeCount > 0) {
			Connection conn = (Connection) this.AvailableConn.pop();
			if (conn == null) {
				//如果对象为空则从Pool中也移除
				this.Pool.remove(conn);
			}
			logger.debug("Connection get " + conn + " connection size is " + GetAllCount() + " FreeConnCount is " + GetFreeConnCount());
			return conn;
		}
		//如果空闲连接没有了，则判断当前维持的连接是否小于最大连接数，小于的话，创建新的连接
		if (GetAllCount() < this._Config.getMaxPoolSize()) {
			Connection conn = CreateConn();
			logger.debug(" Connection get " + conn + " connection size is " + GetAllCount() + " FreeConnCount is " + GetFreeConnCount());
			return conn;
		}
		//否则抛出异常
		throw new Exception("db connection pool is full");
	}

	/**
	 * 获取一个只读的连接，内部实际拿到的不是只读的。。
	 * @return Connection
	 * @throws Exception
	 */
	public Connection GetReadConnection() throws Exception {
		return Get();
	}

	/**
	 * 将连接归还给连接池
	 * @param conn - Connection
	 */
	public synchronized void Release(Connection conn) {
		if (conn != null) {
			try {
				//如果连接池是自动伸缩，并且连接都比较空闲，则直接销毁
				if (this._Config.isAutoShrink() && this.state.GetNoWorkCount(this._Config.getIdleTimeout() * 1000) > 0) {
					//销毁并刷新一下空闲状态戳
					Destroy(conn);
					this.state.setNoWorkCout(GetFreeConnCount());
				} else if (!conn.isClosed()) {
					//如果连接没有被关闭，则归还到连接池中
					this.AvailableConn.push(conn);
				} else {
					//直接销毁
					Destroy(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		logger.debug("Release method connection size is " + GetAllCount() + " FreeConnCount is " + GetFreeConnCount());
	}

	/**
	 * 销毁数据库连接，并从连接池中移除
	 * @param conn - Connection
	 * @throws SQLException
	 */
	private synchronized void Destroy(Connection conn) throws SQLException {
		if (conn != null) {
			try {
				conn.close();
				this.Pool.remove(conn);
				logger.debug(" close one connection!!!" + conn + " connection size is " + GetAllCount() + " FreeConnCount is " + GetFreeConnCount());
			} catch (Throwable th) {
				throw th;
			}
		}
		return;
	}

	/**
	 * 创建数据库连接
	 * @return Connection
	 * @throws Exception
	 */
	private synchronized Connection CreateConn() throws Exception {
		Connection conn = null;
		try {
			//判断其是否有用户名
			if (this._Config.getUserName() == null) {
				conn = DriverManager.getConnection(this._Config.getConnetionUrl());
			} else {
				conn = DriverManager.getConnection(this._Config.getConnetionUrl(), this._Config.getUserName(), this._Config.getPassWord());
			}
		} catch (SQLException e) {
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			//创建成功了后，将其加入到总集合中，至于放不放到可用里面，得看当前可用的是否超过MaxPoolSize
			if (conn != null && !conn.isClosed()) {
				logger.debug(" this conn is create " + conn + " connection size is " + GetAllCount() + " FreeConnCount is " + GetFreeConnCount());
				this.Pool.add(conn);
			}
		}
		return conn;
	}

	/**
	 * 加载数据库驱动
	 */
	private void LoadDrivers() {
		try {
			DriverManager.registerDriver((Driver) Class.forName(this._Config.getDriversClass()).newInstance());
			//驱动程序连接数据库的超时秒数
			DriverManager.setLoginTimeout(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化连接池对象，创建MinPoolSize个数据库连接
	 * @throws Exception
	 */
	private void Init() throws Exception {
		for (int i = 0; i < this._Config.getMinPoolSize(); i++) {
			//创建数据库连接
			Connection conn = CreateConn();
			if (conn != null) {
				//加入到可用连接栈集合中
				this.AvailableConn.push(conn);
			}
		}
	}

	/**
	 * 注册服务器关机事件，关闭的时候，将连接池中所有的连接对象都释放
	 */
	private void RegisterExcetEven() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("application exiting,begin remove all connections.");
			for (Connection conn : ConnectionPool.this.Pool) {
				if (conn != null) {
					try {
						if (!conn.isClosed()) {
							conn.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}));
	}

}
