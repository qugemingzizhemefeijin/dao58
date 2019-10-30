package cg.zz.spat.core.dbms.config;

/**
 * 
 * 数据库配置对象
 * 
 * @author chengang
 *
 */
public class DbConfig {

	/**
	 * 数据库连接
	 */
	private String connetionURL;
	
	/**
	 * 数据库驱动
	 */
	private String driversClass;
	
	/**
	 * 连接空闲超时时间
	 */
	private int idleTimeout;
	
	/**
	 * 插入更新超时时间
	 */
	private long insertUpdateTimeout;
	
	/**
	 * ？？？什么用？？
	 */
	private DbConfig managerDbConfig;
	
	/**
	 * 连接池最大数量
	 */
	private int maxPoolSize;
	
	/**
	 * 连接池最小数量
	 */
	private int minPoolSize;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 查询超时时间
	 */
	private long queryTimeout;
	
	/**
	 * 是否只读
	 */
	private boolean readonly = false;
	
	/**
	 * ???
	 */
	private long releaseInterval;
	
	/**
	 * ???
	 */
	private int releaseStrategyValve;

	public String getConnetionURL() {
		return connetionURL;
	}

	public void setConnetionURL(String connetionURL) {
		this.connetionURL = connetionURL;
	}

	public String getDriversClass() {
		return driversClass;
	}

	public void setDriversClass(String driversClass) {
		this.driversClass = driversClass;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public long getInsertUpdateTimeout() {
		return insertUpdateTimeout;
	}

	public void setInsertUpdateTimeout(long insertUpdateTimeout) {
		this.insertUpdateTimeout = insertUpdateTimeout;
	}

	public DbConfig getManagerDbConfig() {
		return managerDbConfig;
	}

	public void setManagerDbConfig(DbConfig managerDbConfig) {
		this.managerDbConfig = managerDbConfig;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly1) {
		readonly = readonly1;
	}

	public long getReleaseInterval() {
		return releaseInterval;
	}

	public void setReleaseInterval(long releaseInterval) {
		this.releaseInterval = releaseInterval;
	}

	public int getReleaseStrategyValve() {
		return releaseStrategyValve;
	}

	public void setReleaseStrategyValve(int releaseStrategyValve) {
		this.releaseStrategyValve = releaseStrategyValve;
	}

}
