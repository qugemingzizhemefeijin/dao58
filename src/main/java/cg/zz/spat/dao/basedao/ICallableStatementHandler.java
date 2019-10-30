package cg.zz.spat.dao.basedao;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * DAOHelper.execProc方法参数中执行存储过程后执行的回调逻辑
 * @author chengang
 *
 */
public interface ICallableStatementHandler {

	/**
	 * 通过DAOHelper.execProc方法执行存储过程的回调
	 * @param cs - CallableStatement
	 * @return Object 返回值
	 * @throws SQLException
	 */
	public Object exec(CallableStatement cs) throws SQLException;

}
