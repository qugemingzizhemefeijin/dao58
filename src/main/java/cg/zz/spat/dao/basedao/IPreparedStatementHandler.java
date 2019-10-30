package cg.zz.spat.dao.basedao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * PreparedStatement执行器
 * 
 * @author chengang
 *
 */
public interface IPreparedStatementHandler {

	/**
	 * 通过DAOHelper.execWithPara方法，执行一些SQL操作的接口
	 * @param ps - PreparedStatement
	 * @return Object 返回结果或其他对象等等
	 * @throws SQLException
	 */
	public Object exec(PreparedStatement ps) throws SQLException;

}
