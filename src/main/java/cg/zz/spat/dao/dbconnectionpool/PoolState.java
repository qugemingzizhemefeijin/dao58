package cg.zz.spat.dao.dbconnectionpool;

/**
 * 
 * 连接池空闲状态计算类
 * 
 * @author chengang
 *
 */
public class PoolState {

	/**
	 * 上次空闲时间戳
	 */
	private long duration = 0L;
	
	/**
	 * 当前连接池中未工作的数据库连接数量
	 */
	private int noWorkCount = 0;

	/**
	 * 更新空闲连接数，如果上次比较忙，但是当前突然空闲了，则刷新一下时间戳，用来判断连接是否需要归还给连接池还是直接销毁
	 * @param noWorkCout - 空闲连接数
	 */
	public synchronized void setNoWorkCout(int noWorkCout) {
		//如果上次空闲连接小于等于2 并且 当前空闲连接>2 则刷新一下时间戳
		//大概意思就是 本来数据库连接很忙，突然就不忙了，就记录一下时间戳
		if (this.noWorkCount <= 2 && noWorkCout > 2) {
			this.duration = System.currentTimeMillis();
		}
		this.noWorkCount = noWorkCout;
	}

	/**
	 * 获得当前是否应该减少数据库连接池中的对象，如果>0连接对象不归还连接池，直接销毁，否则归还连接池。
	 * @param duration - DBConfig中维护的连接空闲时间
	 * @return int
	 */
	public synchronized int GetNoWorkCount(long duration) {
		int i = 0;
		//如果认为的连接空闲时间>配置
		if (System.currentTimeMillis() - this.duration > duration) {
			i = this.noWorkCount - 2;
		}
		return i;
	}

}
