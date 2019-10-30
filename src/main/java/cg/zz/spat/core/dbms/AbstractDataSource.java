package cg.zz.spat.core.dbms;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * 自定义的数据库源对象
 * 
 * @author chengang
 *
 */
public abstract class AbstractDataSource implements DataSource {

	public Connection GetReadConnection() throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw new SQLException("Not Implemented");
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new SQLException("Not Implemented");
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new SQLException("Not Implemented");
	}

}
