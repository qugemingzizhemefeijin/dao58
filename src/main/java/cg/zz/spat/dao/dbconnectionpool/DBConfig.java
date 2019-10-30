package cg.zz.spat.dao.dbconnectionpool;

/**
 * 
 * 数据库配置
 * 
 * @author chengang
 *
 */
public class DBConfig {
	
	/**
	 * 数据库驱动
	 */
	private String driversClass;
	
	/**
	 * 连接URL
	 */
	private String connetionUrl;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 密码
	 */
	private String passWord;
	
	/**
	 * 连接池最大活跃连接
	 */
	private int maxPoolSize;
	
	/**
	 * 连接池最小活跃连接
	 */
	private int minPoolSize;
	
	/**
	 * 连接空闲时间，秒
	 */
	private int idleTimeout;
	
	/**
	 * 连接超时时间
	 */
	private long timeout;
	
	/**
	 * 连接池是否可自动收缩
	 */
	private boolean autoShrink;

    public String getDriversClass() {
        return this.driversClass;
    }

    public void setDriversClass(String driversClass) {
        this.driversClass = driversClass;
    }

    public String getConnetionUrl() {
        return this.connetionUrl;
    }

    public void setConnetionUrl(String connetionUrl) {
        this.connetionUrl = connetionUrl;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMinPoolSize() {
        return this.minPoolSize;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getIdleTimeout() {
        return this.idleTimeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

	public boolean isAutoShrink() {
		return autoShrink;
	}

	public void setAutoShrink(boolean autoShrink) {
		this.autoShrink = autoShrink;
	}

}
