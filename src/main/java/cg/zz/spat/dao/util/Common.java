package cg.zz.spat.dao.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cg.zz.spat.dao.annotation.Column;
import cg.zz.spat.dao.annotation.ProcedureName;

/**
 * 
 * 维护数据库实体解析后的ClassInfo工具类
 * 
 * @author chengang
 *
 */
public class Common {

	/**
	 * 这个难道不是非线程安全的吗？
	 */
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 缓存了类Class与ClassInfo的映射。
	 */
	private static Map<Class<?>, ClassInfo> classInfoCache = new HashMap<>();

	/**
	 * 获得所有扫描到的ClassInfo映射关系。这个暴露出来不太好，万一谁手贱给改了呢？？应该要用Collections.unmodifiableMap封装一下的。
	 * @return Map<Class<?>, ClassInfo>
	 */
	public static Map<Class<?>, ClassInfo> getAllClassInfo() {
		return classInfoCache;
	}

	/**
	 * 获得映射类的数据库映射信息对象。这个地方也有并发问题。。。
	 * @param clazz - Class
	 * @return ClassInfo
	 */
	private static ClassInfo getClassInfo(Class<?> clazz) {
		ClassInfo ci = classInfoCache.get(clazz);
		if (ci == null) {
			try {
				ci = new ClassInfo(clazz);
				classInfoCache.put(clazz, ci);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return ci;
	}

	/**
	 * 获取类的存储过程操作的注解
	 * @param clazz - Class
	 * @return ProcedureName
	 */
	public static ProcedureName getProcedureName(Class<?> clazz) {
		return getClassInfo(clazz).getProcdure();
	}

	/**
	 * 实体类中指定属性的Set Method方法
	 * @param clazz - Class
	 * @param field - Field
	 * @return Method
	 * @throws Exception
	 */
	public static Method getSetterMethod(Class<?> clazz, Field field) throws Exception {
		return getClassInfo(clazz).getMapSetMethod().get(field.getName());
	}

	/**
	 * 实体类中指定属性的Get Method方法
	 * @param clazz - Class
	 * @param field - Field
	 * @return Method
	 */
	public static Method getGetterMethod(Class<?> clazz, Field field) {
		return getClassInfo(clazz).getMapGetMethod().get(field.getName());
	}

	/**
	 * 获取ID注解的字段集合
	 * @param clazz - Class
	 * @return List<Field>
	 */
	public static List<Field> getIdFields(Class<?> clazz) {
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapIDField().values();
		//这里相当于是复制出来一个List。。这边应该在初始化的时候直接维护不可修改的集合，直接返回即可。
		List<Field> fields = new ArrayList<>();
		for (Field f : coll) {
			fields.add(f);
		}
		return fields;
	}

	/**
	 * 获得所有的映射字段
	 * @param clazz - Class
	 * @return List<Field>
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapAllDBField().values();
		//一样
		List<Field> fields = new ArrayList<>();
		for (Field f : coll) {
			fields.add(f);
		}
		return fields;
	}

	/**
	 * 获得ID注解需要在insert语句中添加的字段集合
	 * @param clazz - Class
	 * @return List<Field>
	 */
	public static List<Field> getInsertableFields(Class<?> clazz) {
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapInsertableField().values();
		//一样
		List<Field> fields = new ArrayList<>();
		for (Field f : coll) {
			fields.add(f);
		}
		return fields;
	}

	/**
	 * 获得ID注解需要在update语句中 添加的字段集合
	 * @param clazz - Class
	 * @return List<Field>
	 */
	public static List<Field> getUpdatableFields(Class<?> clazz) {
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapUpdatableField().values();
		//
		List<Field> fields = new ArrayList<>();
		for (Field f : coll) {
			fields.add(f);
		}
		return fields;
	}

	/**
	 * 获得实体对应的表名
	 * @param clazz - Class
	 * @return String
	 */
	public static String getTableName(Class<?> clazz) {
		return getClassInfo(clazz).getTableName();
	}

	/**
	 * 获取实体属性对应的数据库字段名
	 * @param clazz - Class
	 * @param f - Field
	 * @return String
	 */
	public static String getDBCloumnName(Class<?> clazz, Field f) {
		return getClassInfo(clazz).getMapDBColumnName().get(f.getName());
	}

	/**
	 * 获得主键字段集合
	 * @param clazz - Class
	 * @return List<Field>
	 */
	public static List<Field> getIdentityFields(Class<?> clazz) {
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapIdentityField().values();
		//
		List<Field> fields = new ArrayList<>();
		for (Field f : coll) {
			fields.add(f);
		}
		return fields;
	}

	/**
	 * 获取实体类的ProcedureName注解
	 * @param clazz - Class
	 * @return ProcedureName
	 */
	public static ProcedureName getProc(Class<?> clazz) {
		return getClassInfo(clazz).getProcdure();
	}

	/**
	 * 判断属性是否有默认的值
	 * @param f - Field
	 * @return boolean
	 */
	public static boolean defaultDBValue(Field f) {
		if (f.isAnnotationPresent(Column.class)) {
			return f.getAnnotation(Column.class).defaultDBValue();
		}
		return false;
	}

	/**
	 * 获得对象中TableRename注解属性的名称，如果有多个的话，则默认只返回一个
	 * @param clazz - Class
	 * @param bean - Object
	 * @return String
	 */
	public static String getTableRename(Class<?> clazz, Object bean) {
		String value = null;
		ClassInfo ci = getClassInfo(clazz);
		Collection<Field> coll = ci.getMapTableRenameField().values();
		for (Field f : coll) {
			Method m = getTableRenameGetterMethod(clazz, f);
			try {
				value = String.valueOf(m.invoke(bean, new Object[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 获得实体属性标注TableRename注解的Get Method对象
	 * @param clazz - Class
	 * @param field - Field
	 * @return Method
	 */
	public static Method getTableRenameGetterMethod(Class<?> clazz, Field field) {
		return getClassInfo(clazz).getMapTableRenameGetMethod().get(field.getName());
	}

	/**
	 * 调用PreparedStatement对象的set设置参数值
	 * @param ps - PreparedStatement
	 * @param value - Object
	 * @param columnIndex - 索引位置
	 * @throws Exception
	 */
	public static void setPara(PreparedStatement ps, Object value, int columnIndex) throws Exception {
		if (value != null) {
			Class<?> valueType = value.getClass();
			if (valueType == String.class) {
				ps.setString(columnIndex, value.toString());
			} else if (valueType == Integer.TYPE || valueType == Integer.class) {
				ps.setInt(columnIndex, Integer.parseInt(value.toString(), 10));
			} else if (valueType == Long.TYPE || valueType == Long.class) {
				ps.setLong(columnIndex, Long.parseLong(value.toString()));
			} else if (valueType == Short.TYPE || valueType == Short.class) {
				ps.setShort(columnIndex, Short.parseShort(value.toString()));
			} else if (valueType == java.util.Date.class) {
				ps.setTimestamp(columnIndex, new Timestamp(((java.util.Date) value).getTime()));
			} else if (valueType == Boolean.TYPE || valueType == Boolean.class) {
				ps.setBoolean(columnIndex, Boolean.parseBoolean(value.toString()));
			} else if (valueType == Double.TYPE || valueType == Double.class) {
				ps.setDouble(columnIndex, Double.parseDouble(value.toString()));
			} else if (valueType == Float.TYPE || valueType == Float.class) {
				ps.setFloat(columnIndex, Float.parseFloat(value.toString()));
			} else if (valueType == Byte.TYPE || valueType == Byte.class) {
				ps.setByte(columnIndex, Byte.parseByte(value.toString()));
			} else if (valueType == byte[].class || valueType == Byte[].class) {
				ps.setBytes(columnIndex, (byte[]) value);
			} else if (valueType == BigDecimal.class) {
				ps.setBigDecimal(columnIndex, new BigDecimal(value.toString()));
			} else if (valueType == Timestamp.class) {
				ps.setTimestamp(columnIndex, (Timestamp) value);
			} else if (valueType == java.sql.Date.class) {
				ps.setTimestamp(columnIndex, new Timestamp(((java.sql.Date) value).getTime()));
			} else {
				ps.setObject(columnIndex, value);
			}
		} else {
			ps.setObject(columnIndex, null);
		}
	}

	/**
	 * 调用CallableStatement对象的set设置参数值
	 * @param cstmt - CallableStatement
	 * @param bean - 实体对象
	 * @param f - 属性
	 * @return CallableStatement
	 * @throws Exception
	 */
	public static CallableStatement setPara(CallableStatement cstmt, Object bean, Field f) throws Exception {
		Class<?> clazz = bean.getClass();
		//这里通过反射方式拿到属性对应的值
		Method m = getGetterMethod(clazz, f);
		if (m == null) {
			System.out.println("method is null fn:" + f.getName() + "---" + bean.toString());
		}
		Object value = m.invoke(bean, new Object[0]);
		
		//这里通过数据库字段名称来设置值
		if (value != null) {
			String columnName = getDBCloumnName(clazz, f);
			Class<?> valueType = m.getReturnType();
			if (valueType == String.class) {
				cstmt.setString(columnName, value.toString());
			} else if (valueType == BigDecimal.class) {
				cstmt.setBigDecimal(columnName, new BigDecimal(value.toString()));
			} else if (valueType == Integer.TYPE || valueType == Integer.class) {
				cstmt.setInt(columnName, Integer.parseInt(value.toString(), 10));
			} else if (valueType == Boolean.TYPE || valueType == Boolean.class) {
				cstmt.setBoolean(columnName, Boolean.parseBoolean(value.toString()));
			} else if (valueType == Double.TYPE || valueType == Double.class) {
				cstmt.setDouble(columnName, Double.parseDouble(value.toString()));
			} else if (valueType == Long.TYPE || valueType == Long.class) {
				cstmt.setLong(columnName, Long.parseLong(value.toString()));
			} else if (valueType == Float.TYPE || valueType == Float.class) {
				cstmt.setFloat(columnName, Float.parseFloat(value.toString()));
			} else if (valueType == Short.TYPE || valueType == Short.class) {
				cstmt.setShort(columnName, Short.parseShort(value.toString()));
			} else if (valueType == Byte.TYPE || valueType == Byte.class) {
				cstmt.setByte(columnName, Byte.parseByte(value.toString()));
			} else if (valueType == byte[].class || valueType == Byte[].class) {
				cstmt.setBytes(columnName, (byte[]) value);
			} else if (valueType == Timestamp.class) {
				cstmt.setTimestamp(columnName, (Timestamp) value);
			} else if (valueType == java.util.Date.class) {
				cstmt.setTimestamp(columnName, new Timestamp(((java.util.Date) value).getTime()));
			} else if (valueType == java.sql.Date.class) {
				cstmt.setTimestamp(columnName, new Timestamp(((java.sql.Date) value).getTime()));
			} else {
				throw new Exception("not define column type in WWW58COM.Utility.DAO.Utility.CallableStatement  Class:" + bean.getClass().getName() + " Method:" + f.getName());
			}
		} else {
			String columnName = getDBCloumnName(clazz, f);
			Class<?> valueType = m.getReturnType();
			if (valueType == String.class) {
				cstmt.setString(columnName, "");
			} else if (valueType == BigDecimal.class) {
				cstmt.setBigDecimal(columnName, new BigDecimal("0"));
			} else if (valueType == Integer.TYPE || valueType == Integer.class) {
				cstmt.setInt(columnName, 0);
			} else if (valueType == Boolean.TYPE || valueType == Boolean.class) {
				cstmt.setBoolean(columnName, false);
			} else if (valueType == Double.TYPE || valueType == Double.class) {
				cstmt.setDouble(columnName, 0.0D);
			} else if (valueType == Long.TYPE || valueType == Long.class) {
				cstmt.setLong(columnName, 0L);
			} else if (valueType == Float.TYPE || valueType == Float.class) {
				cstmt.setFloat(columnName, 0.0F);
			} else if (valueType == Short.TYPE || valueType == Short.class) {
				cstmt.setShort(columnName, (short) 0);
			} else if (valueType == Byte.TYPE || valueType == Byte.class) {
				cstmt.setByte(columnName, (byte) 0);
			} else if (valueType == byte[].class || valueType == Byte[].class) {
				cstmt.setBytes(columnName, null);
			} else if (valueType == Timestamp.class) {
				cstmt.setTimestamp(columnName, new Timestamp(new Date().getTime()));
			} else if (valueType == java.util.Date.class) {
				cstmt.setTimestamp(columnName, new Timestamp(new Date().getTime()));
			} else if (valueType == java.sql.Date.class) {
				cstmt.setTimestamp(columnName, new Timestamp(new Date().getTime()));
			} else {
				throw new Exception("not define column type in WWW58COM.Utility.DAO.Utility.CallableStatement  Class:" + bean.getClass().getName() + " Method:" + f.getName());
			}
		}
		return cstmt;
	}

	/**
	 * 通过实体对象和方法获取值，如果值为空，则返回默认值。如果是字符串等类型则会强制加上''
	 * @param bean - 实体对象
	 * @param method - Method
	 * @return String
	 * @throws Exception
	 */
	public static String getValue(Object bean, Method method) throws Exception {
		String retValue = "";

		Object valueObj = method.invoke(bean, new Object[0]);
		Class<?> valueType = method.getReturnType();
		if (valueType == String.class) {
			if (valueObj == null) {
				retValue = "''";
			} else {
				retValue = "'" + valueObj.toString() + "'";
			}
		} else if (valueType == Timestamp.class || valueType == java.sql.Date.class || valueType == java.util.Date.class) {
			if (valueObj == null) {
				retValue = "''";
			} else {
				retValue = "'" + sdf.format(valueObj) + "'";
			}
		} else if (valueType == Boolean.TYPE || valueType == Boolean.class) {
			if (valueObj != null && valueObj.toString().equalsIgnoreCase("true")) {
				retValue = "1";
			} else {
				retValue = "0";
			}
		} else if (valueObj == null) {
			retValue = "null";
		} else {
			retValue = valueObj.toString();
		}
		return retValue;
	}

	/**
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String getValue(Object value) throws Exception {
		String retValue = "";
		Class<?> clazz = value.getClass();
		if (clazz == String.class || clazz == java.util.Date.class || clazz == java.sql.Date.class) {
			retValue = "'" + value.toString() + "'";
		} else {
			retValue = value.toString();
		}
		return retValue;
	}
	
	private Common() {
		
	}

}
