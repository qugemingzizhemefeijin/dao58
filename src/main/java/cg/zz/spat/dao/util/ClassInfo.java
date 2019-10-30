package cg.zz.spat.dao.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cg.zz.spat.dao.annotation.Column;
import cg.zz.spat.dao.annotation.Id;
import cg.zz.spat.dao.annotation.NotDBColumn;
import cg.zz.spat.dao.annotation.ProcedureName;
import cg.zz.spat.dao.annotation.Table;
import cg.zz.spat.dao.annotation.TableRename;

/**
 * 
 * 扫描的数据库映射实体信息
 * 
 * @author chengang
 *
 */
class ClassInfo {

	/**
	 * 所有的DB映射字段
	 */
	private Map<String, Field> mapAllDBField;
	
	/**
	 * 类属性名称与数据库字段的映射关系
	 */
	private Map<String, String> mapDBColumnName;
	
	/**
	 * DB字段对应的Get方法映射
	 */
	private Map<String, Method> mapGetMethod;
	
	/**
	 * ID注解的字段
	 */
	private Map<String, Field> mapIDField;
	
	/**
	 * ID注解的自增字段
	 */
	private Map<String, Field> mapIdentityField;
	
	/**
	 * ID注解中需要在Insert中插入值的字段
	 */
	private Map<String, Field> mapInsertableField;
	
	/**
	 * ID注解中需要在Update中插入值的字段
	 */
	private Map<String, Field> mapUpdatableField;
	
	/**
	 * DB字段对应的Set方法映射
	 */
	private Map<String, Method> mapSetMethod;
	
	/**
	 * TableRename注解的字段映射
	 */
	private Map<String, Field> mapTableRenameField;
	
	/**
	 * TableRename注解字段的Get方法映射
	 */
	private Map<String, Method> mapTableRenameGetMethod;
	
	/**
	 * TableRename注解字段的Set方法映射
	 */
	private Map<String, Method> mapTableRenameSetMethod;
	
	/**
	 * 存储过程注解
	 */
	private ProcedureName procdure;
	
	/**
	 * 实体映射的表名
	 */
	private String tableName;

	public ClassInfo(Class<?> clazz) throws Exception {
		//解析数据库名
		setTableName(getTableName(clazz));
		//解析标注ID注解的属性
		setMapIDField(getIdFields(clazz));
		//解析所有的字段属性
		setMapAllDBField(getAllDBFields(clazz));
		//解析插入的时候字段属性
		setMapInsertableField(getInsertableFields(clazz));
		//解析更新的时候字段属性
		setMapUpdatableField(getUpdatableFields(clazz));
		//解析字段名跟数据库字段的映射
		setMapDBColumnName(getCloumnName(clazz));
		//解析字段对应的Set方法
		setMapSetMethod(getSetterMethod(clazz));
		//解析字段对应的Get方法
		setMapGetMethod(getGetterMethod(clazz));
		//
		setProcdure(getProc(clazz));
		//解析主键ID（应该自增主键）
		setMapIdentityField(getIdentityFields(clazz));
		//
		setMapTableRenameField(getTableRenameFields(clazz));
		//
		setMapTableRenameGetMethod(getTableRenameGetterMethod(clazz));
		//
		setMapTableRenameSetMethod(getTableRenameSetterMethod(clazz));
	}

	/**
	 * 获取数据库表名称，如果注解没有给出具体的表名称，则默认类名为表名
	 * @param clazz - Class
	 * @return String
	 */
	private String getTableName(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			if (!table.name().equalsIgnoreCase("className")) {
				return table.name();
			}
		}
		String name = clazz.getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}

	/**
	 * 获取所有需要在insert语句中写入的字段映射
	 * @param clazz - Class
	 * @return Map<String, Field> , key=字段名称,value=Field
	 */
	private Map<String, Field> getInsertableFields(Class<?> clazz) {
		Map<String, Field> mapFfields = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			//必须是db字段，这里页面也没有过滤serialVersionUID字段？？
			if (!f.isAnnotationPresent(NotDBColumn.class)) {
				if (!f.isAnnotationPresent(Id.class)) {
					Column column = (Column) f.getAnnotation(Column.class);
					if (column == null) {
						mapFfields.put(f.getName(), f);
					} else if (!column.defaultDBValue()) {
						mapFfields.put(f.getName(), f);
					}
				} else if (f.getAnnotation(Id.class).insertable()) {
					//如果是ID注解，并且要求insert的时候插入
					mapFfields.put(f.getName(), f);
				}
			}
		}
		return mapFfields;
	}

	/**
	 * 获得所有的数据库字段
	 * @param clazz - Class
	 * @return Map<String, Field> , key=字段名称,value=Field
	 */
	private Map<String, Field> getAllDBFields(Class<?> clazz) {
		Map<String, Field> mapFfields = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			//过滤掉NotDBColumn注解和serialVersionUID字段
			if (!f.isAnnotationPresent(NotDBColumn.class) && !"serialVersionUID".equalsIgnoreCase(f.getName())) {
				mapFfields.put(f.getName(), f);
			}
		}
		return mapFfields;
	}

	/**
	 * 获得所有标注了ID的字段
	 * @param clazz - Class
	 * @return Map<String, Field> , key=字段名称,value=Field
	 */
	private Map<String, Field> getIdFields(Class<?> clazz) {
		Map<String, Field> mapFfields = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(Id.class)) {
				mapFfields.put(f.getName(), f);
			}
		}
		return mapFfields;
	}

	/**
	 * 获得所有在Update语句中需要写入的字段
	 * @param clazz - Class
	 * @return Map<String, Field> , key=字段名称,value=Field
	 */
	private Map<String, Field> getUpdatableFields(Class<?> clazz) {
		Map<String, Field> mapFfields = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			//逻辑更insert into 差不多，没有过滤serialVersionUID字段
			if (!f.isAnnotationPresent(NotDBColumn.class)) {
				if (!f.isAnnotationPresent(Id.class)) {
					Column column = f.getAnnotation(Column.class);
					if (column == null) {
						mapFfields.put(f.getName(), f);
					} else if (!column.defaultDBValue()) {
						mapFfields.put(f.getName(), f);
					}
				} else if (f.getAnnotation(Id.class).updatable()) {
					mapFfields.put(f.getName(), f);
				}
			}
		}
		return mapFfields;
	}

	/**
	 * 获得字段名与数据库字段名的映射
	 * @param cls - Class
	 * @return Map<String, String> , key=字段名称,value=数据库字段名称
	 */
	private Map<String, String> getCloumnName(Class<?> cls) {
		Map<String, String> mapNames = new HashMap<>();
		for (Field f : this.mapAllDBField.values()) {
			if (f.isAnnotationPresent(Column.class)) {
				Column column = (Column) f.getAnnotation(Column.class);
				if (!column.name().equalsIgnoreCase("fieldName")) {
					mapNames.put(f.getName(), column.name());
				} else {
					mapNames.put(f.getName(), f.getName());
				}
			} else {
				mapNames.put(f.getName(), f.getName());
			}
		}
		return mapNames;
	}

	/**
	 * 获得字段名称对应的Set方法句柄
	 * @param clazz - Class
	 * @return Map<String, Method> , key=字段名,value=Method
	 * @throws Exception
	 */
	private Map<String, Method> getSetterMethod(Class<?> clazz) throws Exception {
		String setFunName;
		Map<String, Method> mapMethod = new HashMap<>();
		//获取所有的DB字段列表
		Collection<Field> fList = this.mapAllDBField.values();
		//获取Bean方法描述数组
		PropertyDescriptor[] lPropDesc = Introspector.getBeanInfo(clazz, Introspector.USE_ALL_BEANINFO).getPropertyDescriptors();
		//匹配出对应的Set方法
		for (Field f : fList) {
			for(PropertyDescriptor aLPropDesc : lPropDesc) {
				if (aLPropDesc.getName().equalsIgnoreCase(f.getName())) {
                    mapMethod.put(f.getName(), aLPropDesc.getWriteMethod());
                    break;
                }
			}
        }
		//如果上面未匹配到对应的Method，那么则根据Column配置的setFuncName属性找到对应的Set方法
		for (Field f2 : fList) {
			Method setterMethod = mapMethod.get(f2.getName());
			if (setterMethod == null) {
				if (f2.isAnnotationPresent(Column.class)) {
					Column column = f2.getAnnotation(Column.class);
					if (!column.setFuncName().equalsIgnoreCase("setField")) {
						setFunName = column.setFuncName();
					} else {
						setFunName = "set" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
					}
				} else {
					setFunName = "set" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
				}
				Method[] methods = clazz.getMethods();
				for(Method m : methods) {
					if (m.getName().equals(setFunName)) {
						setterMethod = m;
						break;
					}
				}
				mapMethod.put(f2.getName(), setterMethod);
			}
		}
		//如果还有没有找到的则抛出异常
		for (Field f3 : fList) {
			if (mapMethod.get(f3.getName()) == null) {
				throw new Exception("can't find set method field:" + f3.getName() + "  class:" + clazz.getName());
			}
		}
		return mapMethod;
	}

	/**
	 * 获得字段名称对应的Get方法句柄
	 * @param clazz - Class
	 * @return Map<String, Method> , key=字段名,value=Method
	 * @throws Exception
	 */
	private Map<String, Method> getGetterMethod(Class<?> clazz) throws Exception {
		String getFunName;
		Map<String, Method> mapMethod = new HashMap<>();
		//获取所有的DB字段列表
		Collection<Field> fList = this.mapAllDBField.values();
		//获取Bean方法描述数组
		PropertyDescriptor[] lPropDesc = Introspector.getBeanInfo(clazz, Introspector.USE_ALL_BEANINFO).getPropertyDescriptors();
		//匹配出对应的Set方法
		for (Field f : fList) {
			for(PropertyDescriptor aLPropDesc : lPropDesc) {
				if (aLPropDesc.getName().equalsIgnoreCase(f.getName())) {
					mapMethod.put(f.getName(), aLPropDesc.getReadMethod());
					break;
				}
			}
		}
		//如果上面未匹配到对应的Method，那么则根据Column配置的getFuncName属性找到对应的Set方法
		for (Field f2 : fList) {
			Method getterMethod = mapMethod.get(f2.getName());
			if (f2.isAnnotationPresent(Column.class)) {
				Column column = f2.getAnnotation(Column.class);
				if (!column.getFuncName().equalsIgnoreCase("getField")) {
					getFunName = column.getFuncName();
				} else {
					getFunName = "get" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
				}
			} else {
				getFunName = "get" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
			}
			Method[] methods = clazz.getMethods();
			for(Method m : methods) {
				if (m.getName().equals(getFunName)) {
					getterMethod = m;
					break;
				}
			}
			mapMethod.put(f2.getName(), getterMethod);
		}
		//如果还有没有找到的则抛出异常
		for (Field f3 : fList) {
			if (mapMethod.get(f3.getName()) == null) {
				throw new Exception("can't find get method field:" + f3.getName() + "  class:" + clazz.getName());
			}
		}
		return mapMethod;
	}

	private ProcedureName getProc(Class<?> clazz) {
		return clazz.getAnnotation(ProcedureName.class);
	}

	/**
	 * 获得自增主键字段映射，这里必须要求不能是主动在update和insert中包含的ID注解的字段
	 * @param clazz - Class
	 * @return Map<String, Field>
	 */
	private Map<String, Field> getIdentityFields(Class<?> clazz) {
		Map<String, Field> mapField = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(Id.class)) {
				Id id = f.getAnnotation(Id.class);
				if (!id.insertable() && !id.updatable()) {
					mapField.put(f.getName(), f);
				}
			}
		}
		return mapField;
	}

	/**
	 * 获取被TableRename注解标注的字段映射
	 * @param clazz - Class
	 * @return Map<String, Field>
	 */
	private Map<String, Field> getTableRenameFields(Class<?> clazz) {
		Map<String, Field> mapTableRenamefields = new HashMap<>();
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(TableRename.class)) {
				mapTableRenamefields.put(f.getName(), f);
			}
		}
		return mapTableRenamefields;
	}

	/**
	 * 获取被TableRename注解标注的字段名和Set方法句柄映射
	 * @param clazz - Class
	 * @return Map<String, Method>
	 * @throws Exception
	 */
	private Map<String, Method> getTableRenameSetterMethod(Class<?> clazz) throws Exception {
		String setFunName;
		Map<String, Method> mapMethod = new HashMap<>();
		Collection<Field> fList = this.mapTableRenameField.values();
		PropertyDescriptor[] lPropDesc = Introspector.getBeanInfo(clazz, Introspector.USE_ALL_BEANINFO).getPropertyDescriptors();
		for (Field f : fList) {
			for(PropertyDescriptor aLPropDesc : lPropDesc) {
				if (aLPropDesc.getName().equalsIgnoreCase(f.getName())) {
					mapMethod.put(f.getName(), aLPropDesc.getWriteMethod());
					break;
				}
			}
		}
		for (Field f2 : fList) {
			Method setterMethod = (Method) mapMethod.get(f2.getName());
			if (setterMethod == null) {
				if (f2.isAnnotationPresent(Column.class)) {
					Column column = f2.getAnnotation(Column.class);
					if (!column.setFuncName().equalsIgnoreCase("setField")) {
						setFunName = column.setFuncName();
					} else {
						setFunName = "set" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
					}
				} else {
					setFunName = "set" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
				}
				Method[] methods = clazz.getMethods();
				for(Method m : methods) {
					if (m.getName().equals(setFunName)) {
						setterMethod = m;
						break;
					}
				}
				mapMethod.put(f2.getName(), setterMethod);
			}
		}
		for (Field f3 : fList) {
			if (mapMethod.get(f3.getName()) == null) {
				throw new Exception("can't find set method field:" + f3.getName() + "  class:" + clazz.getName());
			}
		}
		return mapMethod;
	}
	
	/**
	 * 获取被TableRename注解标注的字段名和Get方法句柄映射
	 * @param clazz - Class
	 * @return Map<String, Method>
	 * @throws Exception
	 */
	private Map<String, Method> getTableRenameGetterMethod(Class<?> clazz) throws Exception {
		String getFunName;
		Map<String, Method> mapMethod = new HashMap<>();
		Collection<Field> fList = this.mapTableRenameField.values();
		PropertyDescriptor[] lPropDesc = Introspector.getBeanInfo(clazz, 1).getPropertyDescriptors();
		for (Field f : fList) {
			for(PropertyDescriptor aLPropDesc : lPropDesc) {
				if (aLPropDesc.getName().equalsIgnoreCase(f.getName())) {
					mapMethod.put(f.getName(), aLPropDesc.getReadMethod());
					break;
				}
			}
		}
		for (Field f2 : fList) {
			Method getterMethod = (Method) mapMethod.get(f2.getName());
			if (f2.isAnnotationPresent(Column.class)) {
				Column column = f2.getAnnotation(Column.class);
				if (!column.getFuncName().equalsIgnoreCase("getField")) {
					getFunName = column.getFuncName();
				} else {
					getFunName = "get" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
				}
			} else {
				getFunName = "get" + f2.getName().substring(0, 1).toUpperCase() + f2.getName().substring(1);
			}
			Method[] methods = clazz.getMethods();
			for(Method m : methods) {
				if (m.getName().equals(getFunName)) {
					getterMethod = m;
					break;
				}
			}
			mapMethod.put(f2.getName(), getterMethod);
		}
		for (Field f3 : fList) {
			if (mapMethod.get(f3.getName()) == null) {
				throw new Exception("can't find get method field:" + f3.getName() + "  class:" + clazz.getName());
			}
		}
		return mapMethod;
	}

	public Map<String, Field> getMapIDField() {
		return this.mapIDField;
	}

	public void setMapIDField(Map<String, Field> mapIDField) {
		this.mapIDField = mapIDField;
	}

	public Map<String, Field> getMapAllDBField() {
		return this.mapAllDBField;
	}

	public void setMapAllDBField(Map<String, Field> mapAllDBField) {
		this.mapAllDBField = mapAllDBField;
	}

	public Map<String, Field> getMapInsertableField() {
		return this.mapInsertableField;
	}

	public void setMapInsertableField(Map<String, Field> mapInsertableField) {
		this.mapInsertableField = mapInsertableField;
	}

	public Map<String, Field> getMapUpdatableField() {
		return this.mapUpdatableField;
	}

	public void setMapUpdatableField(Map<String, Field> mapUpdatableField) {
		this.mapUpdatableField = mapUpdatableField;
	}

	public Map<String, String> getMapDBColumnName() {
		return this.mapDBColumnName;
	}

	public void setMapDBColumnName(Map<String, String> mapDBColumnName) {
		this.mapDBColumnName = mapDBColumnName;
	}

	public Map<String, Method> getMapSetMethod() {
		return this.mapSetMethod;
	}

	public void setMapSetMethod(Map<String, Method> mapSetMethod) {
		this.mapSetMethod = mapSetMethod;
	}

	public Map<String, Method> getMapGetMethod() {
		return this.mapGetMethod;
	}

	public void setMapGetMethod(Map<String, Method> mapGetMethod) {
		this.mapGetMethod = mapGetMethod;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setProcdure(ProcedureName procdure) {
		this.procdure = procdure;
	}

	public ProcedureName getProcdure() {
		return this.procdure;
	}

	public void setMapIdentityField(Map<String, Field> mapIdentityField) {
		this.mapIdentityField = mapIdentityField;
	}

	public Map<String, Field> getMapIdentityField() {
		return this.mapIdentityField;
	}

	public Map<String, Field> getMapTableRenameField() {
		return this.mapTableRenameField;
	}

	public void setMapTableRenameField(Map<String, Field> mapTableRenameField) {
		this.mapTableRenameField = mapTableRenameField;
	}

	public Map<String, Method> getMapTableRenameSetMethod() {
		return this.mapTableRenameSetMethod;
	}

	public void setMapTableRenameSetMethod(Map<String, Method> mapTableRenameSetMethod) {
		this.mapTableRenameSetMethod = mapTableRenameSetMethod;
	}

	public Map<String, Method> getMapTableRenameGetMethod() {
		return this.mapTableRenameGetMethod;
	}

	public void setMapTableRenameGetMethod(Map<String, Method> mapTableRenameGetMethod) {
		this.mapTableRenameGetMethod = mapTableRenameGetMethod;
	}

}
