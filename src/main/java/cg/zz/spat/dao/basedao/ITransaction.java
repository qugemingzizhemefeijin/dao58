package cg.zz.spat.dao.basedao;

/**
 * DAOHelper.execTransaction方法参数中传入的回调逻辑接口
 * @author chenganag
 *
 */
public interface ITransaction {

	/**
	 * DAOHelper.execTransaction方法事务开启后到事务提交前，执行的一些逻辑。。这里有个问题，就是执行SQL操作的时候，可能不太好操作，需要从静态变量中获取DAO对象
	 * @throws Exception
	 */
	public void exec() throws Exception;

}
