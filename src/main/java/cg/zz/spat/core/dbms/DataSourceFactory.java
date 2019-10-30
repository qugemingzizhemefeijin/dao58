package cg.zz.spat.core.dbms;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import cg.zz.spat.core.dbms.config.ClusterConfig;
import cg.zz.spat.core.dbms.config.DataSourceConfig;

/**
 * 
 * 数据源工厂
 * 
 * @author chengang
 *
 */
public class DataSourceFactory {

	private static Map<String, AbstractDataSource> clouds = new HashMap<>();

	/**
	 * 根据DataSource名称获得缓存的数据源
	 * @param dataSourceName - String
	 * @return DataSource
	 * @throws Exception
	 */
	public static DataSource getDataSource(String dataSourceName) throws Exception {
		return getAbstractDataSource(dataSourceName);
	}

	/**
	 * 根据DataSource名称获得缓存的数据源
	 * @param dataSourceName - String
	 * @return DataSource
	 * @throws Exception
	 */
	public static AbstractDataSource getAbstractDataSource(String dataSourceName) throws Exception {
		AbstractDataSource dataSource = clouds.get(dataSourceName);
		if (dataSource != null) {
			return dataSource;
		}
		throw new Exception("there is no dataSourceConfig for " + dataSourceName);
	}

	/**
	 * 设置数据源配置并生成DataSource对象缓存起来。数据源缓存也将会被重置。
	 * @param config - DataSourceConfig
	 * @throws Exception
	 */
	public static synchronized void setConfig(DataSourceConfig config) throws Exception {
		if (clouds.size() != 0) {
			clouds.clear();
		}
		Map<String, ClusterConfig> clusters = config.getDataSourceConfig();
		//迭代所有的数据源，循环反射调用构造方法，构造AbstractDataSource对象
		for (String key : clusters.keySet()) {
			String clazzName = clusters.get(key).getDataSource();
			Object obj = Class.forName(clazzName).getConstructor(new Class[] { ClusterConfig.class }).newInstance(new Object[] { clusters.get(key) });
			if (obj instanceof AbstractDataSource) {
				clouds.put(key, (AbstractDataSource) obj);
			}
		}
	}

}
