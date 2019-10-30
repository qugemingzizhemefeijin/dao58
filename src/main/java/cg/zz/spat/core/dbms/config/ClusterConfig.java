package cg.zz.spat.core.dbms.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Cluster配置
 * 
 * @author chengang
 *
 */
public class ClusterConfig {

	private List<DbConfig> dbConfigList = new ArrayList<>();

	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * dataSource数据源类
	 */
	private String dataSource = "com.bj58.spat.core.dbms.ClusterDataSource";

	public String getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 添加数据库配置到dbConfigList集合中
	 * @param dbConfig - DbConfig
	 */
	public void addDbConfig(DbConfig dbConfig) {
		if (dbConfig == null) {
			return;
		}
		this.dbConfigList.add(dbConfig);
	}

	/**
	 * 获得DbConfigList集合
	 * @return List<DbConfig>
	 */
	public List<DbConfig> getDbConfigList() {
		return this.dbConfigList;
	}

}
