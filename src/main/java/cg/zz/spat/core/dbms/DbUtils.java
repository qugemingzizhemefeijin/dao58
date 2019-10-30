package cg.zz.spat.core.dbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * DB工具类
 * 
 * @author chengang
 *
 */
public class DbUtils {

	private static Logger logger = LoggerFactory.getLogger(DbUtils.class);

	/**
	 * 关闭连接
	 * @param connection - Connection
	 */
	public static void closeConnection(Connection connection) {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException ex) {
			logger.info("Could not close JDBC Connection", ex);
		} catch (Throwable ex) {
			logger.info("Unexpected exception on closing JDBC Connection", ex);
		}
	}

	/**
	 * 关闭Statement
	 * @param stmt - Statement
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt == null) {
			return;
		}
		try {
			stmt.close();
		} catch (SQLException ex) {
			logger.info("Could not close JDBC Statement", ex);
		} catch (Throwable ex) {
			logger.info("Unexpected exception on closing JDBC Statement", ex);
		}
	}

	/**
	 * 关闭ResultSet
	 * @param rs - ResultSet
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs == null) {
			return;
		}
		try {
			rs.close();
		} catch (SQLException ex) {
			logger.info("Could not close JDBC ResultSet", ex);
		} catch (Throwable ex) {
			logger.info("Unexpected exception on closing JDBC ResultSet", ex);
		}
	}

	/**
	 * 关闭ResultSet,Statement,Connection
	 * @param rs - ResultSet
	 * @param stmt - Statement
	 * @param connection - Connection
	 */
	public static void close(ResultSet rs, Statement stmt, Connection connection) {
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(connection);
	}

	private DbUtils() {

	}

}
