package cg.zz.spat.dao.basedao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAOHelper.execQuery方法的回调执行接口
 * @author chengang
 *
 */
public interface IRowCallbackHandler {

	/**
	 * 通过DAOHelper.execQuery获取结果集并调用此方法
	 * @param rs - ResultSet
	 * @return Object
	 * @throws SQLException
	 */
	public Object exec(ResultSet rs) throws SQLException;

}
