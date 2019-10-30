package cg.zz.spat.dao.basedao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.core.dbms.DbUtils;
import cg.zz.spat.dao.dbconnectionpool.ConnectionPool;
import cg.zz.spat.dao.statementcreater.IStatementCreater;
import cg.zz.spat.dao.statementcreater.MysqlPSCreater;
import cg.zz.spat.dao.statementcreater.SqlServerPSCreater;

/**
 * 
 * SQL执行工具类
 * 
 * @author chengang
 *
 */
public class DAOHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DAOHelper.class);
	
	/**
	 * 数据库连接池
	 */
	private ConnectionHelper connHelper;
	
	/**
	 * 存储过程处理者
	 */
	public IDAO proc = null;
	
	/**
	 * 普通SQL处理者
	 */
	public IDAO sql = null;

	/**
	 * DAOHelper
	 * @param connHelper - 数据库连接池
	 */
	public DAOHelper(ConnectionHelper connHelper) {
		this.connHelper = connHelper;
	}

	/**
	 * 应该是不建议直接返回连接池对象
	 * @return ConnectionPool
	 */
	@Deprecated
	public ConnectionPool getConnPool() {
		return this.connHelper.getConnPool();
	}

	/**
	 * 返回连接池对象
	 * @return ConnectionHelper
	 */
	public ConnectionHelper getConnHelper() {
		return this.connHelper;
	}

	/**
	 * 执行SQL处理，并将通过IPreparedStatementHandler接口处理增删改查逻辑
	 * @param sql - SQL语句
	 * @param handler - 执行增删改查逻辑
	 * @return Object
	 * @throws Exception
	 */
	public Object execInsert(String sql, IPreparedStatementHandler handler) throws Exception {
		return execWithPara(sql, handler);
	}

	/**
	 * 执行SQL处理，并将通过IPreparedStatementHandler接口处理增删改查逻辑
	 * @param sql - SQL语句
	 * @param handler - 执行增删改查逻辑
	 * @return Object
	 * @throws Exception
	 */
	public Object execWithPara(String sql, IPreparedStatementHandler handler) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.connHelper.get();
			ps = conn.prepareStatement(sql);
			return handler.exec(ps);
		} catch (Exception e) {
			LOGGER.error("execQuery error sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	/**
	 * 以只读方式执行SQL查询，并将使用IRowCallbackHandler处理ResultSet结果集
	 * @param sql - SQL语句
	 * @param handler - 执行结果集处理的接口
	 * @return Object
	 * @throws Exception
	 */
	public Object execQuery(String sql, IRowCallbackHandler handler) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = this.connHelper.getReadConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			return handler.exec(rs);
		} catch (Exception e) {
			LOGGER.error("execQuery error sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(stmt);
			this.connHelper.release(conn);
		}
	}

	/**
	 * 执行存储过程
	 * @param sql - 存储过程SQL
	 * @param handler - 执行存储过程参数赋值，输出参数配置等
	 * @return Object
	 * @throws Exception
	 */
	public Object execProc(String sql, ICallableStatementHandler handler) throws Exception {
		Connection conn = null;
		CallableStatement cs = null;
		try {
			conn = this.connHelper.get();
			cs = conn.prepareCall(sql);
			return handler.exec(cs);
		} catch (SQLException e) {
			LOGGER.error("execCustomProc error " + sql);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeStatement(cs);
			this.connHelper.release(conn);
		}
	}

	/**
	 * 以事务形式执行SQL，返回Exception对象，如果对象为空则代表执行没有出现异常。具体的SQL操作逻辑需要实现ITransactionHandler接口
	 * @param handler - ITransactionHandler
	 * @return Exception
	 * @throws Exception
	 */
	@Deprecated
	public Exception execTransaction(ITransactionHandler handler) throws Exception {
		Connection conn = null;
		Exception exception = null;
		try {
			IStatementCreater sqlServerCreater = new SqlServerPSCreater();
			IStatementCreater mysqlCreater = new MysqlPSCreater();
			conn = this.connHelper.get();
			conn.setAutoCommit(false);
			
			//执行SQL
			try {
				handler.exec(conn, sqlServerCreater, mysqlCreater);
			} catch (Exception ex) {
				exception = ex;
			}
			
			//提交事务
			try {
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return exception;
		} catch (SQLException e2) {
			LOGGER.error("execCustomProc error " + this.sql);
			throw e2;
		} catch (Throwable th) {
			if(conn != null) {
				//提交事务
				try {
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			throw th;
		} finally {
			//归还连接
			this.connHelper.release(conn);
		}
	}

	/**
	 * 以事务形式执行SQL，如果没有异常则将提交事务，否则回滚。具体的SQL操作逻辑需要实现ITransaction接口
	 * @param tran - ITransaction
	 * @throws Exception
	 */
	public void execTransaction(ITransaction tran) throws Exception {
		beginTransaction();
		try {
			tran.exec();
			commitTransaction();
		} catch (Exception ex) {
			rollbackTransaction();
			throw ex;
		} catch (Throwable th) {
			throw th;
		} finally {
			endTransaction();
		}
	}

	/**
	 * 开启事务，并将事务等级设置为 读已提交
	 * @throws Exception
	 */
	public void beginTransaction() throws Exception {
		beginTransaction(Connection.TRANSACTION_READ_COMMITTED);
	}

	/**
	 * 开启事务，并将事务等级设置为level
	 * @param level - 事务等级，查看Connection.TRANSACTION_READ_COMMITTED等
	 * @throws Exception
	 */
	public void beginTransaction(int level) throws Exception {
		Connection conn = this.connHelper.get();
		if (conn != null) {
			try {
				//将连接设置为非自动提交
				conn.setAutoCommit(false);
				//设置事务等级
				conn.setTransactionIsolation(level);
				this.connHelper.lockConn(conn);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage() , ex);
			}
		} else {
			throw new Exception("conn is null when beginTransaction");
		}
	}

	/**
	 * 事务提交
	 * @throws Exception
	 */
	public void commitTransaction() throws Exception {
		Connection conn = this.connHelper.get();
		if (conn != null) {
			conn.commit();
			return;
		}
		throw new Exception("conn is null when commitTransaction");
	}

	/**
	 * 事务回滚
	 * @throws Exception
	 */
	public void rollbackTransaction() throws Exception {
		Connection conn = this.connHelper.get();
		if (conn != null) {
			conn.rollback();
			return;
		}
		throw new Exception("conn is null when rollbackTransaction");
	}

	/**
	 * 事务完成后，将连接的状态重置
	 * @throws Exception
	 */
	public void endTransaction() throws Exception {
		Connection conn = this.connHelper.get();
		if (conn != null) {
			try {
				//设置自动提交
				conn.setAutoCommit(true);
				//
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			} finally {
				//将当前ThreadLocal中保存的连接清理掉
				this.connHelper.unLockConn();
				//归还连接
				this.connHelper.release(conn);
			}
		} else {
			throw new Exception("conn is null when endTransaction");
		}
	}

}
