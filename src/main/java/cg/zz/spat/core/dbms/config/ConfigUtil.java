package cg.zz.spat.core.dbms.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.core.dbms.AbstractDataSource;
import cg.zz.spat.core.utils.MessageAlertFactory;

public class ConfigUtil {

	private static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

	/**
	 * 读取配置文件，创建多数据源对象
	 * @param filePath - 配置文件地址
	 * @return DataSourceConfig
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DataSourceConfig getDataSourceConfig(String filePath) throws SQLException {
		File file = new File(filePath);
		//???这个是告警用的？
		MessageAlertFactory.setConfig(file.getParent());
		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		ClusterConfig clusterConfig = new ClusterConfig();
		try {
			List<HashMap<String, Object>> obj = (List)Yaml.loadType(new File(filePath), ArrayList.class);
			for (HashMap<String, Object> config : obj) {
				DbConfig dbConfig = assemblyDbConfig(config);
				String dataSource = String.valueOf(config.get("DataSource"));
				//这里判断得一塌糊涂，实际不在配置文件中配置DataSource，则不会被执行。
				if (!dataSource.equals("null")) {
					try {
						//判断AbstractDataSource是否是dataSource配置的类的超类或接口
						if (AbstractDataSource.class.isAssignableFrom(Class.forName(dataSource))) {
							clusterConfig.setDataSource(dataSource);
							if (dataSource.equals("com.bj58.spat.core.dbms.IndieDataSource")) {
								if (!dbConfig.getDriversClass().startsWith("com.microsoft.sqlserver")) {
									throw new SQLException("当前使用的数据库连接池仅仅支持 SQL Server 数据库");
								}
								Object mobj = config.get("managerDbConfig");
								if (mobj != null) {
									DbConfig managerDbConfig = null;
									Iterator it = ((List) mobj).iterator();
									if (it.hasNext()) {
										managerDbConfig = assemblyDbConfig((HashMap) it.next());
									}
									if (managerDbConfig != null) {
										String conurl = dbConfig.getConnetionURL();
										String mconurl = managerDbConfig.getConnetionURL();
										String conhost = conurl.replaceAll("DatabaseName=[\\s\\S]+", "");
										String mconhost = mconurl.replaceAll("DatabaseName=[\\s\\S]+", "");
										if (conurl.equals(mconurl) || !conhost.equals(mconhost)) {
											logger.warn("配置'P_WakeUP'存储过程所在数据库不合法,当主数据库发生异常时将无法自动切换到从库.");
										} else {
											dbConfig.setManagerDbConfig(managerDbConfig);
										}
									}
								}
								if (dbConfig.getManagerDbConfig() == null) {
									logger.warn("未配置'P_WakeUP'存储过程所在数据库,当主数据库发生异常时将无法自动切换到从库.");
								}
							}
						}
					} catch (Exception e) {
					}
				}
				clusterConfig.addDbConfig(dbConfig);
				clusterConfig.setName("_DEFAULT");
			}
			dataSourceConfig.addClusterConfig(clusterConfig);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		return dataSourceConfig;
	}

	/**
	 * 通过读取到的配置信息组装DbConfig对象
	 * @param config - HashMap<String, Object>
	 * @return DbConfig
	 */
	private static DbConfig assemblyDbConfig(HashMap<String, Object> config) {
		DbConfig dbConfig = new DbConfig();
		//连接地址
		dbConfig.setConnetionURL((String) config.get("connectionURL"));
		//驱动
		dbConfig.setDriversClass((String) config.get("driversClass"));
		//用户名
		dbConfig.setUsername(String.valueOf(config.get("username")));
		//密码
		dbConfig.setPassword(String.valueOf(config.get("password")));
		//空闲超时时间
		dbConfig.setIdleTimeout(Integer.parseInt(String.valueOf(config.get("idleTimeout"))));
		//插入和修改超时时间
		dbConfig.setInsertUpdateTimeout((long) Integer.parseInt(String.valueOf(config.get("insertUpdateTimeout"))));
		//连接池最大值
		dbConfig.setMaxPoolSize(Integer.parseInt(String.valueOf(config.get("maxPoolSize"))));
		//连接至最小值
		dbConfig.setMinPoolSize(Integer.parseInt(String.valueOf(config.get("minPoolSize"))));
		//查询超时时间
		dbConfig.setQueryTimeout((long) Integer.parseInt(String.valueOf(config.get("queryTimeout"))));
		boolean readonlyValue = false;
		String readonly = String.valueOf(config.get("readonly"));
		if ("true".equals(readonly)) {
			readonlyValue = true;
		}
		//是否是只读
		dbConfig.setReadonly(readonlyValue);
		//???
		if (config.get("releaseInterval") != null) {
			dbConfig.setReleaseInterval(Long.parseLong(String.valueOf(config.get("releaseInterval"))));
		}
		//???
		if (config.get("releaseStrategyValve") != null) {
			dbConfig.setReleaseStrategyValve(Integer.parseInt(String.valueOf(config.get("releaseStrategyValve"))));
		}
		
		return dbConfig;
	}

	/**
	 * 通过配置列表组装DataSourceConfig
	 * @param obj - List<HashMap<String, Object>>
	 * @return DataSourceConfig
	 */
	public static DataSourceConfig getDataSourceConfig(List<HashMap<String, Object>> obj) {
		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		ClusterConfig clusterConfig = new ClusterConfig();
		for (HashMap<String, Object> config : obj) {
			DbConfig dbConfig = new DbConfig();
			//数据库连接URL
			dbConfig.setConnetionURL(String.valueOf(config.get("connectionURL")));
			//数据库驱动
			dbConfig.setDriversClass(String.valueOf(config.get("driversClass")));
			//用户名
			dbConfig.setUsername(String.valueOf(config.get("username")));
			//密码
			dbConfig.setPassword(String.valueOf(config.get("password")));
			//空闲超时时间
			dbConfig.setIdleTimeout(Integer.parseInt(String.valueOf(config.get("idleTimeout"))));
			//插入和修改超时时间
			dbConfig.setInsertUpdateTimeout((long) Integer.parseInt(String.valueOf(config.get("insertUpdateTimeout"))));
			//连接池最大数量 
			dbConfig.setMaxPoolSize(Integer.parseInt(String.valueOf(config.get("maxPoolSize"))));
			//连接池最小数量
			dbConfig.setMinPoolSize(Integer.parseInt(String.valueOf(config.get("minPoolSize"))));
			//查询超时时间
			dbConfig.setQueryTimeout(Long.parseLong(String.valueOf(config.get("queryTimeout"))));
			
			boolean readonlyValue = false;
			String readonly = String.valueOf(config.get("readonly"));
			if ("true".equals(readonly)) {
				readonlyValue = true;
			}
			//是否是只读
			dbConfig.setReadonly(readonlyValue);
			if (config.get("releaseInterval") != null) {
				dbConfig.setReleaseInterval(Long.parseLong(String.valueOf(config.get("releaseInterval"))));
			}
			if (config.get("releaseStrategyValve") != null) {
				dbConfig.setReleaseStrategyValve(Integer.parseInt(String.valueOf(config.get("releaseStrategyValve"))));
			}
			//将其加入到多数据源配置对象中
			clusterConfig.addDbConfig(dbConfig);
			clusterConfig.setName("_DEFAULT");
		}
		//加入到多数据源对象中
		dataSourceConfig.addClusterConfig(clusterConfig);
		return dataSourceConfig;
	}

	public static ClusterConfig getClusterConfig(String filePath) {
		return null;
	}

	public static DbConfig getDbConfig(String filePath) {
		return null;
	}

}
