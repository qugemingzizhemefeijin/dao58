package cg.zz.spat.dao.statementcreater;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cg.zz.spat.dao.util.Common;
import cg.zz.spat.dao.util.OutSQL;

public abstract class PSCreaterBase implements IStatementCreater {

	public <I> PreparedStatement createGetByIDS(Class<?> clazz, Connection conn, I[] ids, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("SELECT * FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键ID获取数据：主键不存在 或 有两个以上的主键");
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

	public PreparedStatement createDeleteByCustom(Class<?> clazz, Connection conn, Map<String, Object> condition, OutSQL sql) throws Exception {
		if (condition == null || condition.size() <= 0) {
			throw new Exception("delete必须带条件");
		}
		StringBuffer sbSql = new StringBuffer("DELETE FROM ");
		sbSql.append(Common.getTableName(clazz));
		sbSql.append(" WHERE ");
		Set<String> keys = condition.keySet();
		
		int index = 0;
		for (String key : keys) {
			if (index != 0) {
				sbSql.append(" AND ");
			}
			sbSql.append(key);
			sbSql.append("=?");
		}
		sql.setSql(sbSql.toString());
		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		index = 1;
		for (String key : keys) {
			Common.setPara(ps, condition.get(key), index);
			index++;
		}
		return ps;
	}

	public PreparedStatement createGetByCustom(Class<?> clazz, Connection conn, String columns,
			Map<String, Object> condition, String orderBy, OutSQL sql) throws Exception {
		StringBuffer sbSql = new StringBuffer("SELECT ");
		if (columns == null || columns.trim().equals("")) {
			sbSql.append("*");
		} else {
			sbSql.append(columns);
		}
		sbSql.append(" FROM ");
		sbSql.append(Common.getTableName(clazz));
		
		int index = 0;
		Set<String> keys = null;
		if (condition != null) {
			sbSql.append(" WHERE ");
			keys = condition.keySet();
			for (String key : keys) {
				if (index != 0) {
					sbSql.append(" AND ");
				}
				sbSql.append(key);
				sbSql.append("=?");
			}
		}
		if ((orderBy != null) && (!orderBy.trim().equals(""))) {
			sbSql.append(" ORDER BY ");
			sbSql.append(orderBy);
		}
		sql.setSql(sbSql.toString());

		PreparedStatement ps = conn.prepareStatement(sql.getSql());
		if (condition != null) {
			index = 1;
			for (String key : keys) {
				Common.setPara(ps, condition.get(key), index);
				index++;
			}
		}
		return ps;
	}

	public PreparedStatement createGetByPage(Class<?> cls, Connection conn, Map<String, Object> map, String columns, int page, int pageSize, String orderBy, OutSQL sql) throws Exception {
		throw new Exception("该本版不支持");
	}

	public PreparedStatement createUpdateByCustom(Class<?> cls, Connection conn, Map<String, Object> map, Map<String, Object> map2, OutSQL sql) throws Exception {
		throw new Exception("该本版不支持");
	}

	public PreparedStatement createGetCount(Class<?> cls, Connection conn, Map<String, Object> map, OutSQL sql) throws Exception {
		throw new Exception("该本版不支持");
	}

	/**
	 * 这个方法没用上，好像是获取数据库行锁的字符
	 * @param dbType - 数据库类型
	 * @param opType - 操作类型
	 * @return String
	 */
	@SuppressWarnings("unused")
	private String getNoLock(DBType dbType, OperatorType opType) {
		String lock = null;
		if (opType == OperatorType.SELECT) {
			if (dbType == DBType.SqlServer) {
				lock = " WITH(NOLOCK) ";
			}
		} else if (opType == OperatorType.UPDATE && dbType == DBType.SqlServer) {
			lock = " WITH(ROWLOCK) ";
		}
		return lock;
	}

}
