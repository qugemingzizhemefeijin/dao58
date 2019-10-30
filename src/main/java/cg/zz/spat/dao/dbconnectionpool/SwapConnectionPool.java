package cg.zz.spat.dao.dbconnectionpool;

import java.sql.Connection;

import cg.zz.spat.core.dbms.AbstractDataSource;
import cg.zz.spat.core.dbms.DbUtils;

/**
 * 
 * 支持多库的数据库连接池，但是感觉这个连接池没有实现呀？都是实时去拿连接的。。释放也是直接关闭连接。
 * 
 * @author chengang
 *
 */
public class SwapConnectionPool extends ConnectionPool {

	private AbstractDataSource datasource;

	public SwapConnectionPool(AbstractDataSource datasource) {
		this.datasource = datasource;
	}

	@Override
	public int GetAllCount() {
		return -1;
	}

	@Override
	public int GetFreeConnCount() {
		return -1;
	}

	@Override
	public synchronized Connection Get() throws Exception {
		return this.datasource.getConnection();
	}

	@Override
	public synchronized void Release(Connection connection) {
		DbUtils.closeConnection(connection);
	}

	@Override
	public Connection GetReadConnection() throws Exception {
		return this.datasource.GetReadConnection();
	}

}
