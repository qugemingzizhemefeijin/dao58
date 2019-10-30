package cg.zz.spat.dao.basedao;

import java.sql.Connection;

import cg.zz.spat.dao.statementcreater.IStatementCreater;

/**
 * DAOHelper.execTransaction方法参数中在执行事务提交前的一些逻辑操作
 * @author chengang
 *
 */
public interface ITransactionHandler {

	/**
	 * DAOHelper.execTransaction方法在开启事务后，事务提交前执行的SQL逻辑
	 * @param conn - Connection
	 * @param sqlServerCreater - IStatementCreater
	 * @param mysqlCreater - IStatementCreater
	 * @return Object 返回值
	 * @throws Exception
	 */
	public Object exec(Connection conn, IStatementCreater sqlServerCreater, IStatementCreater mysqlCreater) throws Exception;

}
