package cg.zz.spat.dao.statementcreater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import cg.zz.spat.dao.util.OutSQL;

/**
 * 
 * PreparedStatement创建接口
 * @author chengang
 *
 */
public interface IStatementCreater {

	/**
	 * 创建删除的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param id - ID
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public <I> PreparedStatement createDelete(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception;

	/**
	 * 创建自定义删除的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param condition - 删除条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createDeleteByCustom(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception;

	/**
	 * 创建自定义删除的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param map - 删除条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createDeleteByCustom(Class<?> clazz, Connection conn, Map<String, Object> map, OutSQL sql) throws Exception;

	/**
	 * 创建通过ID数组删除的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param ids - ID数组
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public <I> PreparedStatement createDeleteByIDS(Class<?> clazz, Connection conn, I[] ids, OutSQL sql) throws Exception;

	/**
	 * 创建自定义查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param columns - 获取的字段列表，多个逗号分隔
	 * @param condition - 查询条件
	 * @param orderBy - 排序条件，多个逗号分隔
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetByCustom(Class<?> clazz, Connection conn, String columns, String condition, String orderBy, OutSQL sql) throws Exception;

	/**
	 * 创建自定义查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param columns - 获取的字段列表，多个逗号分隔
	 * @param map - 查询条件
	 * @param orderBy - 排序条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetByCustom(Class<?> clazz, Connection conn, String columns, Map<String, Object> map, String orderBy, OutSQL sql) throws Exception;

	/**
	 * 创建通过ID数组查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param ids - ID数组
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public <I> PreparedStatement createGetByIDS(Class<?> clazz, Connection conn, I[] ids, OutSQL sql) throws Exception;

	/**
	 * 创建分页查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param condition - 查询条件
	 * @param columns - 查询的字段
	 * @param page - 当前页
	 * @param pageSize - 每页数量
	 * @param orderBy - 排序
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetByPage(Class<?> clazz, Connection conn, String condition, String columns, int page, int pageSize, String orderBy, OutSQL sql) throws Exception;

	/**
	 * 
	 * 创建分页查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param map - 查询条件
	 * @param columns - 查询的字段
	 * @param page - 当前页
	 * @param pageSize - 每页数量
	 * @param orderBy - 排序
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetByPage(Class<?> clazz, Connection conn, Map<String, Object> map, String columns, int page, int pageSize, String orderBy, OutSQL sql) throws Exception;

	/**
	 * 创建Count查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param condition - 查询条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetCount(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception;

	/**
	 * 创建Count查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param map - 查询条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createGetCount(Class<?> clazz, Connection conn, Map<String, Object> map, OutSQL sql) throws Exception;

	/**
	 * 创建通过ID查询的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param id - ID
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public <I> PreparedStatement createGetEntity(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception;

	/**
	 * 创建Insert的PreparedStatement
	 * @param bean - 数据库映射实体
	 * @param conn - Connection
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createInsert(Object bean, Connection conn, OutSQL sql) throws Exception;

	/**
	 * 创建Update的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param updateStatement - 修改部分的语句
	 * @param condition - 修改的条件
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createUpdateByCustom(Class<?> clazz, Connection conn, String updateStatement, String condition, OutSQL sql) throws Exception;

	/**
	 * 创建Update的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param map - 查询条件
	 * @param map2 - 修改的字段
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createUpdateByCustom(Class<?> clazz, Connection conn, Map<String, Object> map, Map<String, Object> map2, OutSQL sql) throws Exception;

	/**
	 * 创建通过ID执行Update的PreparedStatement
	 * @param clazz - Class
	 * @param conn - Connection
	 * @param updateStatement - 修改部分的语句
	 * @param id - ID
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public <I> PreparedStatement createUpdateByID(Class<?> clazz, Connection conn, String updateStatement, I id, OutSQL sql) throws Exception;

	/**
	 * 创建通过Bean实体执行Update的PreparedStatement
	 * @param bean - 数据库映射实体
	 * @param conn - Connection
	 * @param sql - OutSQL
	 * @return PreparedStatement
	 * @throws Exception
	 */
	public PreparedStatement createUpdateEntity(Object bean, Connection conn, OutSQL sql) throws Exception;

}
