package cg.zz.spat.dao.dbconnectionpool;

import javax.sql.DataSource;

import cg.zz.spat.core.dbms.AbstractDataSource;
import cg.zz.spat.core.dbms.DataSourceFactory;
import cg.zz.spat.core.dbms.config.ConfigUtil;
import cg.zz.spat.core.dbms.config.DataSourceConfig;

/**
 * 
 * 支持多数据库的连接池创建工厂
 * 
 * @author chengang
 *
 */
public final class ConnectionPoolFactory {

	/**
	 * 根据数据库配置创建连接池
	 * @param configPath - 数据库配置
	 * @return
	 * @throws Exception
	 */
	public static synchronized ConnectionPool createPool(String configPath) throws Exception {
		//从配置文件中
		DataSourceConfig dataSourceConfig = ConfigUtil.getDataSourceConfig(configPath);
		DataSourceFactory.setConfig(dataSourceConfig);
		DataSource datasource = DataSourceFactory.getDataSource("_DEFAULT");

		AbstractDataSource aDataSource = (AbstractDataSource) datasource;

		SwapConnectionPool dbConnectionPool = new SwapConnectionPool(aDataSource);

		return dbConnectionPool;
	}
	
	private ConnectionPoolFactory() {
		
	}

}
