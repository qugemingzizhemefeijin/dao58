package cg.zz.spat.dao.basedao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.dao.statementcreater.IStatementCreater;
import cg.zz.spat.dao.util.Common;
import cg.zz.spat.dao.util.PropertiesHelper;

/**
 * 
 * DAO抽象类，主要用于创建DAO连接池等等
 * 
 * @author chengang
 *
 */
public abstract class DAOBase implements IDAO {
	
	protected static final Logger logger = LoggerFactory.getLogger(DAOBase.class);

	/**
	 * 生成Statement的类，包括Bean实体的SQL转换，参数的入参等
	 */
	protected IStatementCreater psCreater;
	
	/**
	 * 连接池
	 */
	protected ConnectionHelper connHelper;
	
	/**
	 * 默认数据库读取操作超时2秒
	 */
	protected int qurryTimeOut = 2;
	
	/**
	 * 默认数据库写入操作超时5秒
	 */
	protected int insertUpdateTimeOut = 5;
	
	/**
	 * 数据库配置文件路径
	 */
	private static final String DB_CONFIG_PATH = "db.properties";

	/**
	 * 根据数据库默认配置创建DAOHelper对象
	 * @return DAOHelper
	 * @throws Exception
	 */
	public static DAOHelper createIntrance() throws Exception {
		return createDAO(DB_CONFIG_PATH);
	}

	/**
	 * 根据指定的数据库配置创建DAOHelper对象
	 * @param configPath - 配置文件相对地址
	 * @return DAOHelper
	 * @throws Exception
	 */
	public static DAOHelper createIntrance(String configPath) throws Exception {
		return createDAO(configPath);
	}

	/**
	 * 根据指定的数据库配置创建DAOHelper对象
	 * @param configPath - 配置文件相对地址
	 * @return DAOHelper
	 * @throws Exception
	 */
	private static DAOHelper createDAO(String configPath) throws Exception {
		//数据库连接池对象
		ConnectionHelper ch = new ConnectionHelper(configPath);
		//配置文件属性维护对象
		PropertiesHelper ph = new PropertiesHelper(configPath);

		//数据库操作对象，将数据库连接池传递过去
		DAOHelper dao = new DAOHelper(ch);

		//创建普通SQL处理者
		DAOBase sqlDAO = null;
		String sqlCreaterClass = ph.getString("SqlCreaterClass");
		if (sqlCreaterClass != null && !sqlCreaterClass.equalsIgnoreCase("")) {
			//这里的代表估计是为了兼容老版本而写死的，感觉好突兀，一脸懵逼
			if (sqlCreaterClass.equalsIgnoreCase("cg.zz.spat.dao.sqlcreate.SqlServerSQLCreater")) {
				sqlCreaterClass = "cg.zz.spat.dao.statementcreater.SqlServerPSCreater";
			}
			if (sqlCreaterClass.equalsIgnoreCase("cg.zz.spat.dao.sqlcreate.MySqlSQLCreater")) {
				sqlCreaterClass = "cg.zz.spat.dao.statementcreater.MysqlPSCreater";
			}
			logger.info("init SqlCreaterClass:" + sqlCreaterClass);
			
			//构造Statement创建者
			IStatementCreater creater = (IStatementCreater) Class.forName(sqlCreaterClass).newInstance();
			sqlDAO = new DAOHandler(creater);
			sqlDAO.connHelper = ch;
			sqlDAO.qurryTimeOut = ph.getInt("QurryTimeOut");
			sqlDAO.insertUpdateTimeOut = ph.getInt("InsertUpdateTimeOut");
		}
		
		//创建存储过程处理者
		DAOBase procDAO = null;
		String procCreaterClass = ph.getString("ProcCreaterClass");
		if (procCreaterClass != null && !procCreaterClass.equalsIgnoreCase("")) {
			//这里的代表估计是为了兼容老版本而写死的，感觉好突兀，一脸懵逼
			if (procCreaterClass.equalsIgnoreCase("cg.zz.spat.dao.sqlcreate.BJ58AutoCreateProcParaCreater")) {
				procCreaterClass = "cg.zz.spat.dao.statementcreater.BJ58ProcCSCreater";
			}
			logger.info("init ProcCreaterClass:" + procCreaterClass);
			
			//构造Statement创建者
			IStatementCreater creater = (IStatementCreater) Class.forName(procCreaterClass).newInstance();
			procDAO = new DAOHandler(creater);
			procDAO.connHelper = ch;
			procDAO.qurryTimeOut = ph.getInt("QurryTimeOut");
			procDAO.insertUpdateTimeOut = ph.getInt("InsertUpdateTimeOut");
		}
		
		//将创建出来的具体SQL的执行器赋值给DAOHelper中的对应的属性字段
		dao.sql = sqlDAO;
		dao.proc = procDAO;

		logger.info("create DAOHelper success!");
		return dao;
	}

	/**
	 * 将ResultSet中的数据构造为指定的类型集合
	 * @param resultSet - ResultSet
	 * @param clazz - Class<T>
	 * @return List<T>
	 * @throws Exception
	 */
	protected <T> List<T> populateData(ResultSet resultSet, Class<T> clazz) throws Exception {
		List<T> dataList = new ArrayList<>();
		//获取所有数据库字段
		List<Field> fieldList = Common.getAllFields(clazz);

		//获取ResultSet元数据信息，这里应该将字段名称存储到Set集合中，如果用list的话，在下面使用contains的复杂度是o(n)。
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsCount = rsmd.getColumnCount();
		List<String> columnNameList = new ArrayList<>();
		for (int i = 0; i < columnsCount; i++) {
			columnNameList.add(rsmd.getColumnLabel(i + 1).toLowerCase());
		}
		while (resultSet.next()) {
			T bean = clazz.newInstance();
			for (Field f : fieldList) {
				//获取字段对应的数据库名称
				String columnName = Common.getDBCloumnName(clazz, f).toLowerCase();
				//如果查询出来的字段在映射实体中，则获取其value值
				if (columnNameList.contains(columnName)) {
					Object columnValueObj = null;
					Class<?> filedCls = f.getType();
					if (filedCls == Integer.TYPE || filedCls == Integer.class) {
						columnValueObj = Integer.valueOf(resultSet.getInt(columnName));
					} else if (filedCls == String.class) {
						columnValueObj = resultSet.getString(columnName);
					} else if (filedCls == Boolean.TYPE || filedCls == Boolean.class) {
						columnValueObj = Boolean.valueOf(resultSet.getBoolean(columnName));
					} else if (filedCls == Byte.TYPE || filedCls == Byte.class) {
						columnValueObj = Byte.valueOf(resultSet.getByte(columnName));
					} else if (filedCls == Short.TYPE || filedCls == Short.class) {
						columnValueObj = Short.valueOf(resultSet.getShort(columnName));
					} else if (filedCls == Long.TYPE || filedCls == Long.class) {
						columnValueObj = Long.valueOf(resultSet.getLong(columnName));
					} else if (filedCls == Float.TYPE || filedCls == Float.class) {
						columnValueObj = Float.valueOf(resultSet.getFloat(columnName));
					} else if (filedCls == Double.TYPE || filedCls == Double.class) {
						columnValueObj = Double.valueOf(resultSet.getDouble(columnName));
					} else if (filedCls == BigDecimal.class) {
						columnValueObj = resultSet.getBigDecimal(columnName);
					} else {
						columnValueObj = resultSet.getObject(columnName);
					}
					//反射调用set方法
					if (columnValueObj != null) {
						Method setterMethod = Common.getSetterMethod(clazz, f);
						setterMethod.invoke(bean, new Object[] { columnValueObj });
					}
				}
			}
			dataList.add(bean);
		}
		return dataList;
	}

}
