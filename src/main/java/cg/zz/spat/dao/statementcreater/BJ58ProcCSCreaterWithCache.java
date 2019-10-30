package cg.zz.spat.dao.statementcreater;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cg.zz.spat.dao.annotation.ProcedureName;
import cg.zz.spat.dao.util.Common;
import cg.zz.spat.dao.util.OutSQL;

/**
 * 这个类是专门用于执行存储过程的SQL拼接，此类会缓存CallableStatement。。。估计是内部历史原因造成需要通过存储过程来进行删除等操作吧。
 * 
 * 这个类里面字符串拼接大量的用到了StringBuffer对象，应该优化为StringBuilder
 * 
 * @author chengang
 *
 */
public class BJ58ProcCSCreaterWithCache extends PSCreaterBase {
	
	private static Map<String, CallableStatement> mapStatement = new HashMap<>();
	
	public synchronized void addStatement(String key, CallableStatement cs) {
		if (!mapStatement.containsKey(key)) {
			mapStatement.put(key, cs);
		}
	}

	@Override
	public <I> PreparedStatement createDelete(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception {
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键删除：主键不存在 或 有两个以上的主键");
		}
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.delete());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.delete());
				sbSql.append("(?,?,?)");
				sbSql.append("}");

				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			String columnName = Common.getDBCloumnName(clazz, fieldList.get(0));
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			cstmt.setString("Where", "[" + columnName + "]=" + Common.getValue(id));

			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public PreparedStatement createDeleteByCustom(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception {
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.delete());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.delete());
				sbSql.append("(?,?,?)");
				sbSql.append("}");
				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			cstmt.setString("Where", condition);
			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public <I> PreparedStatement createDeleteByIDS(Class<?> clazz, Connection conn, I[] ids, OutSQL sql) throws Exception {
		throw new UnsupportedOperationException("Not supported for proc");
	}

	@Override
	public PreparedStatement createGetByCustom(Class<?> clazz, Connection conn, String columns, String condition, String orderBy, OutSQL sql) throws Exception {
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.load());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.load());
				sbSql.append("(?,?,?,?,?)");
				sbSql.append("}");

				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			cstmt.setString("OrderByFields", orderBy);
			cstmt.setString("SelectColumns", columns);
			cstmt.setString("Where", condition);

			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public PreparedStatement createGetByPage(Class<?> clazz, Connection conn, String condition, String columns, int page, int pageSize, String orderBy, OutSQL sql) throws Exception {
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.locaByPage());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.locaByPage());
				sbSql.append("(?,?,?,?,?,?,?)");
				sbSql.append("}");

				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);

			cstmt.setInt("PageSize", pageSize);
			cstmt.setInt("CurrentPage", page);
			cstmt.setString("SelectColumns", columns);
			cstmt.setString("OrderByFields", orderBy);
			cstmt.setString("Where", condition);

			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public PreparedStatement createGetCount(Class<?> clazz, Connection conn, String condition, OutSQL sql) throws Exception {
		throw new Exception("not implement");
	}

	@Override
	public <I> PreparedStatement createGetEntity(Class<?> clazz, Connection conn, I id, OutSQL sql) throws Exception {
		List<Field> fieldList = Common.getIdFields(clazz);
		if (fieldList.size() != 1) {
			throw new Exception("无法根据主键ID获取数据：主键不存在 或 有两个以上的主键");
		}
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.load());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.load());
				sbSql.append("(?,?,?,?,?)");
				sbSql.append("}");

				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			String columnName = Common.getDBCloumnName(clazz, fieldList.get(0));
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			cstmt.setString("OrderByFields", "");
			cstmt.setString("SelectColumns", "*");
			cstmt.setString("Where", "[" + columnName + "]=" + Common.getValue(id));

			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public PreparedStatement createInsert(Object bean, Connection conn, OutSQL sql) throws Exception {
		Class<?> clazz = bean.getClass();
		List<Field> fieldList = Common.getInsertableFields(clazz);
		List<Field> idFields = Common.getIdentityFields(clazz);
		if (fieldList.size() <= 0 || (idFields != null && idFields.size() > 1)) {
			throw new Exception("表实体没有字段，或有两个以上的ID字段");
		}
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.insert());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.insert());
				sbSql.append("(?,?");
				if (idFields != null && idFields.size() == 1) {
					sbSql.append(",?");
				}
				for (int i = 0; i < fieldList.size(); i++) {
					sbSql.append(",?");
				}
				sbSql.append(")");
				sbSql.append("}");
				sql.setSql(sbSql.toString());
				cstmt = conn.prepareCall(sql.getSql());
			}
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			if (idFields != null && idFields.size() == 1) {
				String idColumn = Common.getDBCloumnName(bean.getClass(), idFields.get(0));
				Class<?> idColumnType = idFields.get(0).getType();
				if (idColumnType == Long.TYPE || idColumnType == Long.class) {
					cstmt.registerOutParameter(idColumn, -5);
				} else if (idColumnType == Integer.TYPE || idColumnType == Integer.class) {
					cstmt.registerOutParameter(idColumn, 4);
				} else if (idColumnType == String.class) {
					cstmt.registerOutParameter(idColumn, 12);
				} else if (idColumnType == Short.class || idColumnType == Short.TYPE) {
					cstmt.registerOutParameter(idColumn, 5);
				} else if (idColumnType == Byte.class || idColumnType == Byte.TYPE) {
					cstmt.registerOutParameter(idColumn, -7);
				} else {
					throw new Exception("未知的主键类型： WWW58COM.Common.DAO.SqlCreate.ProcParaCreate");
				}
			}
			for (Field f : fieldList) {
				Common.setPara(cstmt, bean, f);
			}
			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public PreparedStatement createUpdateByCustom(Class<?> clazz, Connection conn, String updateStatement, String condition, OutSQL sql) throws Exception {
		ProcedureName des = Common.getProc(clazz);
		if (des != null) {
			CallableStatement cstmt = mapStatement.get(des.update());
			if (cstmt == null) {
				StringBuffer sbSql = new StringBuffer("{call ");
				sbSql.append(des.update());
				sbSql.append("(?,?,?,?)");
				sbSql.append("}");
				sql.setSql(sbSql.toString());

				cstmt = conn.prepareCall(sql.getSql());
			}
			cstmt.registerOutParameter("ReturnValue", 4);
			cstmt.registerOutParameter("RowCount", 4);
			cstmt.setString("UpdateStatement", updateStatement);
			cstmt.setString("Where", condition);

			return cstmt;
		}
		throw new Exception("实体没有定义:@ProcedureName");
	}

	@Override
	public <I> PreparedStatement createUpdateByID(Class<?> clazz, Connection conn, String updateStatement, I id, OutSQL sql) throws Exception {
		throw new UnsupportedOperationException("Not supported for proc");
	}

	@Override
	public PreparedStatement createUpdateEntity(Object bean, Connection conn, OutSQL sql) throws Exception {
		List<Field> fieldList = Common.getAllFields(bean.getClass());
		if (fieldList.size() > 0) {
			ProcedureName des = Common.getProc(bean.getClass());
			if (des != null) {
				CallableStatement cstmt = mapStatement.get(des.updateByID());
				if (cstmt == null) {
					StringBuffer sbSql = new StringBuffer("{call ");
					sbSql.append(des.updateByID());
					sbSql.append("(?,?");
					for (int i = 0; i < fieldList.size(); i++) {
						sbSql.append(",?");
					}
					sbSql.append(")");
					sbSql.append("}");
					sql.setSql(sbSql.toString());
					cstmt = conn.prepareCall(sql.getSql());
				}
				cstmt.registerOutParameter("ReturnValue", 4);
				cstmt.registerOutParameter("RowCount", 4);
				for (Field f : fieldList) {
					Common.setPara(cstmt, bean, f);
				}
				return cstmt;
			}
			throw new Exception("实体没有定义:@ProcedureName");
		}
		throw new Exception("表实体没有字段");
	}

}
