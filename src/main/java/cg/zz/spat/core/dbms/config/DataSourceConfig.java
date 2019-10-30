package cg.zz.spat.core.dbms.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源配置管理，在ConfigUtil中设置的ClusterConfig名称和对象
 * 
 * @author chengang
 *
 */
public class DataSourceConfig {

	/**
	 * Key为数据源名称，Value为ClusterConfig对象
	 */
	private Map<String, ClusterConfig> clusterConfigMap = new ConcurrentHashMap<>();

	/**
	 * 将ClusterConfig对象存放到集合中，如果两个ClusterConfig名称一样，后放入的将替换前面的
	 * @param clusterConfig - ClusterConfig
	 */
	public void addClusterConfig(ClusterConfig clusterConfig) {
		if (clusterConfig == null) {
			return;
		}
		this.clusterConfigMap.put(clusterConfig.getName(), clusterConfig);
	}

	/**
	 * 根据名称获得ClusterConfig对象
	 * @param datasourceName - 数据源名称
	 * @return ClusterConfig
	 */
	public ClusterConfig getDataSourceConfig(String datasourceName) {
		return this.clusterConfigMap.get(datasourceName);
	}

	/**
	 * 获得维护的所有的ClusterConfig映射
	 * @return Map<String, ClusterConfig>
	 */
	public Map<String, ClusterConfig> getDataSourceConfig() {
		return this.clusterConfigMap;
	}

}
