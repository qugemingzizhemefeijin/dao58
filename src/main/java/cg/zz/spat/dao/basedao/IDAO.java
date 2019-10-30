package cg.zz.spat.dao.basedao;

import java.util.List;
import java.util.Map;

/**
 * 
 * DAO操作接口
 * 
 * @author chengang
 *
 */
public interface IDAO {

	/**
	 * 插入数据
	 * @param bean - T
	 * @return Object 返回自增ID值
	 * @throws Exception
	 */
	public <T> Object insert(T bean) throws Exception;

	/**
	 * 插入数据
	 * @param bean - T
	 * @param timeOut - 超时时间，秒
	 * @return Object 返回自增ID值
	 * @throws Exception
	 */
	public <T> Object insert(T bean, int timeOut) throws Exception;

	/**
	 * 根据ID删除数据
	 * @param clazz - Class
	 * @param id - I
	 * @throws Exception
	 */
	public <I> void deleteByID(Class<?> clazz, I id) throws Exception;

	/**
	 * 根据ID删除数据
	 * @param clazz - Class
	 * @param id - I
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public <I> void deleteByID(Class<?> clazz, I id, int timeOut) throws Exception;

	/**
	 * 根据ID批量删除数据
	 * @param clazz - Class
	 * @param ids - I[]
	 * @throws Exception
	 */
	public <I> void deleteByIDS(Class<?> clazz, I[] ids) throws Exception;

	/**
	 * 根据ID批量删除数据
	 * @param clazz - Class
	 * @param ids - I[]
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public <I> void deleteByIDS(Class<?> clazz, I[] ids, int timeOut) throws Exception;

	/**
	 * 根据条件删除数据
	 * @param clazz - Class
	 * @param condition - String
	 * @throws Exception
	 */
	public void deleteByCustom(Class<?> clazz, String condition) throws Exception;

	/**
	 * 根据条件删除数据
	 * @param clazz - Class
	 * @param condition - String
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public void deleteByCustom(Class<?> clazz, String condition, int timeOut) throws Exception;

	/**
	 * 根据条件删除数据
	 * @param clazz - Class
	 * @param condition - Map<String, Object>
	 * @throws Exception
	 */
	public void deleteByCustom(Class<?> clazz, Map<String, Object> condition) throws Exception;

	/**
	 * 根据条件删除数据
	 * @param clazz - Class
	 * @param condition - Map<String, Object>
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public void deleteByCustom(Class<?> clazz, Map<String, Object> condition, int timeOut) throws Exception;

	/**
	 * 根据Bean实体更新数据
	 * @param bean - Object
	 * @throws Exception
	 */
	public void updateEntity(Object bean) throws Exception;

	/**
	 * 根据Bean实体更新数据
	 * @param bean - Object
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public void updateEntity(Object bean, int timeOut) throws Exception;

	/**
	 * 根据ID更新指定字段
	 * @param clazz - Class
	 * @param updateStatement - 更新的语句
	 * @param id - I
	 * @throws Exception
	 */
	public <I> void updateByID(Class<?> clazz, String updateStatement, I id) throws Exception;

	/**
	 * 根据ID更新指定字段
	 * @param clazz - Class
	 * @param updateStatement - 更新的语句
	 * @param id - I
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public <I> void updateByID(Class<?> clazz, String updateStatement, I id, int timeOut) throws Exception;

	/**
	 * 根据指定条件更新指定的字段
	 * @param clazz - Class
	 * @param updateStatement - 更新的语句
	 * @param condition - 条件
	 * @throws Exception
	 */
	public void updateByCustom(Class<?> clazz, String updateStatement, String condition) throws Exception;

	/**
	 * 根据指定条件更新指定的字段
	 * @param clazz - Class
	 * @param updateStatement - 更新的语句
	 * @param condition - 条件
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public void updateByCustom(Class<?> clazz, String updateStatement, String condition, int timeOut) throws Exception;

	/**
	 * 根据指定条件更新指定的字段
	 * @param clazz - Class
	 * @param kv - Map<String, Object>
	 * @param condition - Map<String, Object>
	 * @throws Exception
	 */
	public void updateByCustom(Class<?> clazz, Map<String, Object> kv, Map<String, Object> condition) throws Exception;

	/**
	 * 根据指定条件更新指定的字段
	 * @param clazz - Class
	 * @param kv - Map<String, Object>
	 * @param condition - Map<String, Object>
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	public void updateByCustom(Class<?> clazz, Map<String, Object> kv, Map<String, Object> condition, int timeOut) throws Exception;

	/**
	 * 根据ID查询数据
	 * @param clazz - Class
	 * @param id - I
	 * @return T
	 * @throws Exception
	 */
	public <T, I> T get(Class<T> clazz, I id) throws Exception;

	/**
	 * 根据ID查询数据
	 * @param clazz - Class
	 * @param id - I
	 * @param timeOut - 超时时间，秒
	 * @return T
	 * @throws Exception
	 */
	public <T, I> T get(Class<T> clazz, I id, int timeOut) throws Exception;

	/**
	 * 根据ID批量查询数据
	 * @param clazz - Class
	 * @param ids - I[]
	 * @return List<T>
	 * @throws Exception
	 */
	public <T, I> List<T> getListByIDS(Class<T> clazz, I[] ids) throws Exception;

	/**
	 * 根据ID批量查询数据
	 * @param clazz - Class
	 * @param ids - I[]
	 * @param timeOut - 超时时间，秒
	 * @return List<T>
	 * @throws Exception
	 */
	public <T, I> List<T> getListByIDS(Class<T> clazz, I[] ids, int timeOut) throws Exception;

	/**
	 * 根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param columns - 字段，多个逗号分隔
	 * @param condition - 条件
	 * @param orderBy - 排序
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, String condition, String orderBy) throws Exception;

	/**
	 * 根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param columns - 字段，多个逗号分隔
	 * @param condition - 条件
	 * @param orderBy - 排序
	 * @param timeOut - 超时时间，秒
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, String condition, String orderBy, int timeOut) throws Exception;

	/**
	 * 根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param columns - 字段，多个逗号分隔
	 * @param condition - 条件
	 * @param orderBy - 排序
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, Map<String, Object> condition, String orderBy) throws Exception;

	/**
	 * 根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param columns - 字段，多个逗号分隔
	 * @param condition - 条件
	 * @param orderBy - 排序
	 * @param timeOut - 超时时间，秒
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, Map<String, Object> condition, String orderBy, int timeOut) throws Exception;

	/**
	 * 带分页根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param columns - 查询字段
	 * @param page - 当前页
	 * @param pageSize - 页数
	 * @param orderBy - 排序
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByPage(Class<T> clazz, String condition, String columns, int page, int pageSize, String orderBy) throws Exception;

	/**
	 * 带分页根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param columns - 查询字段
	 * @param page - 当前页
	 * @param pageSize - 页数
	 * @param orderBy - 排序
	 * @param timeOut - 超时时间，秒
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByPage(Class<T> clazz, String condition, String columns, int page, int pageSize, String orderBy, int timeOut) throws Exception;

	/**
	 * 带分页根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param columns - 查询字段
	 * @param page - 当前页
	 * @param pageSize - 页数
	 * @param orderBy - 排序
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByPage(Class<T> clazz, Map<String, Object> condition, String columns, int page, int pageSize, String orderBy) throws Exception;
	
	/**
	 * 带分页根据条件查询指定字段的数据
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param columns - 查询字段
	 * @param page - 当前页
	 * @param pageSize - 页数
	 * @param orderBy - 排序
	 * @param timeOut - 超时时间，秒
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListByPage(Class<T> clazz, Map<String, Object> condition, String columns, int page, int pageSize, String orderBy, int timeOut) throws Exception;

	/**
	 * 根据条件查询数量
	 * @param clazz - Class
	 * @param condition - 条件
	 * @return int
	 * @throws Exception
	 */
	public int getCount(Class<?> clazz, String condition) throws Exception;

	/**
	 * 根据条件查询数量
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param timeOut - 超时时间，秒
	 * @return int
	 * @throws Exception
	 */
	public int getCount(Class<?> clazz, String condition, int timeOut) throws Exception;

	/**
	 * 根据条件查询数量
	 * @param clazz - Class
	 * @param condition - 条件
	 * @return int
	 * @throws Exception
	 */
	public int getCount(Class<?> clazz, Map<String, Object> condition) throws Exception;

	/**
	 * 根据条件查询数量
	 * @param clazz - Class
	 * @param condition - 条件
	 * @param timeOut - 超时时间，秒
	 * @return int
	 * @throws Exception
	 */
	public int getCount(Class<?> clazz, Map<String, Object> condition, int timeOut) throws Exception;

	/**
	 * 直接通过SQL查询数据
	 * @param clazz - Class
	 * @param sql - String
	 * @param param - 参数
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListBySQL(Class<T> clazz, String sql, Object... param) throws Exception;

	/**
	 * 直接通过SQL查询数据
	 * @param clazz - Class
	 * @param sql - String
	 * @param timeOut - 超时时间，秒
	 * @param param - 参数
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> getListBySQL(Class<T> clazz, String sql, int timeOut, Object... param) throws Exception;

	/**
	 * 直接执行SQL
	 * @param sql - String
	 * @param param - 参数
	 * @return int
	 * @throws Exception
	 */
	public int execBySQL(String sql, Object... param) throws Exception;

	/**
	 * 直接执行SQL
	 * @param sql - String
	 * @param timeOut - 超时时间，秒
	 * @param param - 参数
	 * @return int
	 * @throws Exception
	 */
	public int execBySQL(String sql, int timeOut, Object... param) throws Exception;

	/**
	 * 直接执行SQL获取数量
	 * @param sql - String
	 * @param param - 参数
	 * @return int
	 * @throws Exception
	 */
	public int getCountBySQL(String sql, Object... param) throws Exception;

	/**
	 * 直接执行SQL获取数量
	 * @param sql - String
	 * @param timeOut - 超时时间，秒
	 * @param param - 参数
	 * @return int
	 * @throws Exception
	 */
	public int getCountBySQL(String sql, int timeOut, Object... param) throws Exception;

	/**
	 * 自定义查询SQL
	 * @param sql - String
	 * @param columnCount - 列数（按理说应该不需要，可以直接获取到列数）
	 * @return List<Object[]>
	 * @throws Exception
	 */
	@Deprecated
	public List<Object[]> customSql(String sql, int columnCount) throws Exception;

	/**
	 * 自定义查询SQL
	 * @param sql - String
	 * @param columnCount - 列数（按理说应该不需要，可以直接获取到列数）
	 * @param timeOut - 超时时间，秒
	 * @return List<Object[]>
	 * @throws Exception
	 */
	@Deprecated
	public List<Object[]> customSql(String sql, int columnCount, int timeOut) throws Exception;

	/**
	 * 自定义执行SQL，不需要返回值
	 * @param sql - String
	 * @throws Exception
	 */
	@Deprecated
	public void customSqlNoReturn(String sql) throws Exception;

	/**
	 * 自定义执行SQL，不需要返回值
	 * @param sql - String
	 * @param timeOut - 超时时间，秒
	 * @throws Exception
	 */
	@Deprecated
	public void customSqlNoReturn(String sql, int timeOut) throws Exception;

}
