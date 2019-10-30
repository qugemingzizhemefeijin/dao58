package cg.zz.spat.dao.basedao;

import java.io.File;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.dao.dbconnectionpool.ConnectionPool;
import cg.zz.spat.dao.dbconnectionpool.ConnectionPoolFactory;
import cg.zz.spat.dao.dbconnectionpool.DBConfig;
import cg.zz.spat.dao.util.PropertiesHelper;

/**
 * 
 * 数据库连接工具类
 * 
 * @author chengang
 *
 */
public class ConnectionHelper {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionHelper.class);
	
	/**
	 * 连接池对象
	 */
	private ConnectionPool connPool;
	
	/**
	 * 用于维护当前线程获得的连接对象
	 */
	private final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

	public ConnectionHelper(String configPath) throws Exception {
		logger.info("creating DAOHelper configPath:" + configPath);
		//加载配置文件到PropertiesHelper对象中
		PropertiesHelper ph = new PropertiesHelper(configPath);
		logger.info("init ConnectionPool...");
		//获取多库配置，默认情况下是不会用到多数据源
		String swapDataSource = ph.getString("swapDataSource");
		if (swapDataSource != null) {
			//支持多数据库的连接池
			this.connPool = getDataSource(configPath, swapDataSource);
			if (this.connPool == null) {
				throw new Exception("conn pool is null: " + configPath);
			}
		} else {
			//普通单库的连接池
			this.connPool = createConnPool(ph);
		}
		logger.info("init ConnectionPool success connection count:" + this.connPool.GetAllCount());
		//如果连接池中连接对象为0则发出警告
		if (this.connPool.GetAllCount() == 0) {
			logger.warn("success create 0 connection, please check config!!!");
		}
	}

	/**
	 * 读取swap.config配置创建支持多数据库的连接池
	 * @param configPath - 单库的配置文件路径
	 * @param swapDataSource - 其平行目录下的swap配置文件路径
	 * @return ConnectionPool
	 * @throws Exception
	 */
	private ConnectionPool getDataSource(String configPath, String swapDataSource) throws Exception {
		return ConnectionPoolFactory.createPool(new File(configPath).getParent() + "/" + swapDataSource);
	}

	/**
	 * 从连接池中获取连接
	 * @return Connection
	 * @throws Exception
	 */
	public Connection get() throws Exception {
		Connection conn = this.threadLocal.get();
		if (conn == null) {
			return this.connPool.Get();
		}
		return conn;
	}

	/**
	 * 从连接池中获取只读连接
	 * @return Connection
	 * @throws Exception
	 */
	public Connection getReadConnection() throws Exception {
		Connection conn = (Connection) this.threadLocal.get();
		if (conn == null) {
			return this.connPool.GetReadConnection();
		}
		return conn;
	}

	/**
	 * 将连接归还连接池
	 * @param conn - Connection
	 */
	public void release(Connection conn) {
		Connection tconn = this.threadLocal.get();
		//当threadLocal中维护的Connection跟要释放的不是一个对象，则允许释放
		if (tconn == null || tconn.hashCode() != conn.hashCode()) {
			logger.debug("this conn is release " + conn);
			this.connPool.Release(conn);
		}
	}

	/**
	 * 将当前的连接放到ThreadLocal中
	 * @param conn - Connection
	 */
	public void lockConn(Connection conn) {
		this.threadLocal.set(conn);
	}

	/**
	 * 将当前ThreadLocal中的连接清空，注意：此处没有释放连接。
	 */
	public void unLockConn() {
		this.threadLocal.set(null);
	}

	/**
	 * 获得整个连接池对象
	 * @return ConnectionPool
	 */
	public ConnectionPool getConnPool() {
		return this.connPool;
	}

	/**
	 * 根据配置生成连接池对象
	 * @param ph - PropertiesHelper
	 * @return ConnectionPool
	 * @throws Exception
	 */
	private ConnectionPool createConnPool(PropertiesHelper ph) throws Exception {
		logger.debug("ConnectionPool ConnetionURL:" + ph.getString("ConnetionURL"));
		logger.debug("ConnectionPool DriversClass:" + ph.getString("DriversClass"));
		logger.debug("ConnectionPool UserName:***");
		logger.debug("ConnectionPool PassWord:***");
		logger.debug("ConnectionPool MinPoolSize:" + ph.getInt("MinPoolSize"));
		logger.debug("ConnectionPool MaxPoolSize:" + ph.getInt("MaxPoolSize"));
		logger.debug("ConnectionPool IdleTimeout:" + ph.getInt("IdleTimeout"));
		logger.debug("ConnectionPool AutoShrink:" + ph.getBoolean("AutoShrink"));
		
		DBConfig config = new DBConfig();
		config.setConnetionUrl(ph.getString("ConnetionURL"));
		config.setDriversClass(ph.getString("DriversClass"));
		config.setUserName(ph.getString("UserName"));
		config.setPassWord(ph.getString("PassWord"));
		config.setMinPoolSize(ph.getInt("MinPoolSize"));
		config.setMaxPoolSize(ph.getInt("MaxPoolSize"));
		config.setIdleTimeout(ph.getInt("IdleTimeout"));
		config.setAutoShrink(ph.getBoolean("AutoShrink"));
		
		return new ConnectionPool(config);
	}

}
