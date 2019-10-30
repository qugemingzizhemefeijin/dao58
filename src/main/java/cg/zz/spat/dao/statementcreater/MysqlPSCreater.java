package cg.zz.spat.dao.statementcreater;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import cg.zz.spat.dao.util.Common;
import cg.zz.spat.dao.util.OutSQL;

/**
 * Mysql PreparedStatement 创建器
 * 
 * 这个类里面字符串拼接大量的用到了StringBuffer对象，应该优化为StringBuilder
 * 
 * @author chengang
 *
 */
public class MysqlPSCreater extends PSCreaterBase {

	@Override
	public <I> PreparedStatement createDelete(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception {
		String idColumnName = "";
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键删除：主键不存在 或 有两个以上的主键");
		}
		idColumnName = Common.getDBCloumnName(clazz, (Field) fieldList.get(0));

		StringBuffer sbSql = new StringBuffer("DELETE FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");
		sbSql.append("`");
		sbSql.append(idColumnName);
		sbSql.append("`");
		sbSql.append("=?");

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());

		Common.setPara(ps, id, 1);

		return ps;
	}

	@Override
	public PreparedStatement createDeleteByCustom(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("DELETE FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");
		if (condition == null || condition.trim().length() == 0) {
			condition = "1=2";
		}
		sbSql.append(condition);

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		return ps;
	}

	@Override
	public <I> PreparedStatement createDeleteByIDS(Class<?> clazz, Connection conn, I[] ids, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("DELETE FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");

		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键ID删除数据：主键不存在 或 有两个以上的主键");
		}
		sbSql.append(Common.getDBCloumnName(clazz, fieldList.get(0)));

		sbSql.append(" IN (");
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) {
				sbSql.append(",");
			}
			sbSql.append("?");
		}
		sbSql.append(")");

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		int index = 1;
		for (int i = 0; i < ids.length; index++) {
			Common.setPara(ps, ids[i], index);
			i++;
		}
		return ps;
	}

	@Override
	public PreparedStatement createGetByCustom(Class<?> clazz, Connection conn, String columns, String condition, String orderBy, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("SELECT ");
		if (columns == null || columns.trim().equals("")) {
			sbSql.append("*");
		} else {
			sbSql.append(columns);
		}
		sbSql.append(" FROM ");
		sbSql.append(Common.getTableName(clazz));
		if (condition != null && !condition.trim().equals("")) {
			sbSql.append(" WHERE ");
			sbSql.append(condition);
		}
		if (orderBy != null && !orderBy.trim().equals("")) {
			sbSql.append(" ORDER BY ");
			sbSql.append(orderBy);
		}
		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		return ps;
	}

	@Override
	public PreparedStatement createGetByPage(Class<?> clazz, Connection conn, String condition, String columns, int page, int pageSize, String orderBy, OutSQL sql) throws Exception {
		int offset = pageSize * (page - 1);
		StringBuffer sbSql = new StringBuffer("SELECT ");
		if (columns == null || columns.trim().equalsIgnoreCase("")) {
			sbSql.append("*");
		} else {
			sbSql.append(columns);
		}
		sbSql.append(" FROM ");
		sbSql.append(Common.getTableName(clazz));
		if (condition != null && !condition.equalsIgnoreCase("")) {
			sbSql.append(" WHERE ");
			sbSql.append(condition);
		}
		if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
			sbSql.append(" ORDER BY ");
			sbSql.append(orderBy);
		}
		sbSql.append(" LIMIT ");
		sbSql.append(offset);
		sbSql.append(",");
		sbSql.append(pageSize);

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		return ps;
	}

	@Override
	public PreparedStatement createGetCount(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("SELECT COUNT(0) FROM ");
		sbSql.append(Common.getTableName(clazz));
		if (condition != null && !condition.trim().equals("")) {
			sbSql.append(" WHERE ");
			sbSql.append(condition);
		}
		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		return ps;
	}

	@Override
	public <I> PreparedStatement createGetEntity(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception {
		String idColumnName = "";
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键ID获取数据：主键不存在 或 有两个以上的主键");
		}
		idColumnName = Common.getDBCloumnName(clazz, fieldList.get(0));

		StringBuffer sbSql = new StringBuffer("SELECT * ");

		sbSql.append(" FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");
		sbSql.append("`");
		sbSql.append(idColumnName);
		sbSql.append("`");
		sbSql.append("=?");

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		Common.setPara(ps, id, 1);
		return ps;
	}

	@Override
	public PreparedStatement createInsert(Object bean, Connection conn, OutSQL sql) throws Exception {
		Class<?> clazz = bean.getClass();
		StringBuffer sbSql = new StringBuffer("INSERT INTO ");

		String tableName = Common.getTableRename(clazz, bean);
		if (tableName == null || "".equals(tableName)) {
			tableName = Common.getTableName(clazz);
		}
		sbSql.append(tableName);
		sbSql.append("(");
		List<Field> listField = Common.getInsertableFields(clazz);

		StringBuilder sbColumn = new StringBuilder();
		StringBuilder sbValue = new StringBuilder();
		boolean isFirst = true;
		for (int i = 0; i < listField.size(); i++) {
			if (!isFirst) {
				sbColumn.append(", ");
				sbValue.append(", ");
			}
			sbColumn.append("`");
			sbColumn.append(Common.getDBCloumnName(clazz, listField.get(i)));
			sbColumn.append("`");

			sbValue.append("?");
			isFirst = false;
		}
		sbSql.append(sbColumn);
		sbSql.append(") VALUES (");
		sbSql.append(sbValue);
		sbSql.append(")");

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql(), 1);
		for (int i = 0; i < listField.size(); i++) {
			Method m = Common.getGetterMethod(clazz, listField.get(i));
			Object value = m.invoke(bean, new Object[0]);
			Common.setPara(ps, value, i + 1);
		}
		return ps;
	}

	@Override
	public PreparedStatement createUpdateByCustom(Class<?> clazz, Connection conn, String updateStatement, String condition, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("UPDATE ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" SET ");
		sbSql.append(updateStatement);
		sbSql.append(" WHERE ");
		if (condition == null || condition.trim().length() == 0) {
			condition = "1=2";
		}
		sbSql.append(condition);

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		return ps;
	}

	@Override
	public <I> PreparedStatement createUpdateByID(Class<?> clazz, Connection conn, String updateStatement, I id, OutSQL sql) throws Exception {
		String idName = null;
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键ID删除数据：主键不存在 或 有两个以上的主键");
		}
		idName = Common.getDBCloumnName(clazz, fieldList.get(0));

		StringBuffer sbSql = new StringBuffer("UPDATE ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" SET ");
		sbSql.append(updateStatement);
		sbSql.append(" WHERE ");
		sbSql.append(idName);
		sbSql.append("=?");

		sql.setSql(sbSql.toString());
		
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		Common.setPara(ps, id, 1);
		return ps;
	}

	@Override
	public PreparedStatement createUpdateEntity(Object bean, Connection conn, OutSQL sql) throws Exception {
		Class<?> clazz = bean.getClass();
		List<Field> idFields = Common.getIdFields(clazz);
		if (idFields.size() == 0) {
			throw new Exception("无法根据实体更新：主键不存在");
		}
		List<Field> listField = Common.getUpdatableFields(clazz);
		if (listField.size() > 0) {
			//这里理论上还是要用StringBuilder好点，虽然现代的JVM能够把这里优化掉，但是在JVM初始阶段还是不会有逃逸分析等。。。。。
			StringBuffer sbSql = new StringBuffer("UPDATE ");
			sbSql.append(Common.getTableName(clazz));
			boolean isFirst = true;
			for (int i = 0; i < listField.size(); i++) {
				if (isFirst) {
					sbSql.append(" SET ");
				} else {
					sbSql.append(", ");
				}
				sbSql.append("`");
				sbSql.append(Common.getDBCloumnName(clazz, listField.get(i)));
				sbSql.append("`");
				sbSql.append("=?");
				isFirst = false;
			}
			sbSql.append(" WHERE ");
			isFirst = true;
			for (int i = 0; i < idFields.size(); i++) {
				if (!isFirst) {
					sbSql.append(" AND ");
				}
				sbSql.append("`");
				sbSql.append(Common.getDBCloumnName(clazz, idFields.get(i)));
				sbSql.append("`");
				sbSql.append("=?");
				isFirst = false;
			}
			sql.setSql(sbSql.toString());
			
			PreparedStatement ps = conn.prepareStatement(sql.getSql());

			int index = 1;
			for (int i = 0; i < listField.size(); i++) {
				Method m = Common.getGetterMethod(clazz, listField.get(i));
				Object value = m.invoke(bean, new Object[0]);
				Common.setPara(ps, value, index);
				index++;
			}
			for (int i = 0; i < idFields.size(); i++) {
				Method m = Common.getGetterMethod(clazz, idFields.get(i));
				Object value = m.invoke(bean, new Object[0]);
				Common.setPara(ps, value, index);
				index++;
			}
			return ps;
		}
		throw new Exception("表实体没有字段");
	}
	
}
