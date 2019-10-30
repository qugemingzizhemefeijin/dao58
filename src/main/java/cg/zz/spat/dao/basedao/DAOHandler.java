package cg.zz.spat.dao.basedao;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cg.zz.spat.core.dbms.DbUtils;
import cg.zz.spat.dao.statementcreater.IStatementCreater;
import cg.zz.spat.dao.util.Common;
import cg.zz.spat.dao.util.OutSQL;
import cg.zz.spat.dao.util.SqlInjectHelper;

/**
 * DAO操作类
 * @author chengang
 *
 */
public class DAOHandler extends DAOBase {
	
	/**
	 * 传入Statement创造器构造DAO操作类
	 * @param creater - IStatementCreater
	 */
	public DAOHandler(IStatementCreater creater) {
		this.psCreater = creater;
	}

	@Override
	public <T> Object insert(T bean) throws Exception {
		return insert(bean, this.insertUpdateTimeOut);
	}

	@Override
	public <T> Object insert(T bean, int timeOut) throws Exception {
		Class<?> beanCls = bean.getClass();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Object rst = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createInsert(bean, conn, sql);
            ps.setQueryTimeout(timeOut);
            ps.executeUpdate();
            
            //判断PreparedStatement是否是调用的存储过程
            boolean isProc = false;
            Class<?>[] clsAry = ps.getClass().getInterfaces();
            int length = clsAry.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                } else if (clsAry[i] == CallableStatement.class) {
                    isProc = true;
                    break;
                } else {
                    i++;
                }
            }
            List<Field> identityFields = Common.getIdentityFields(beanCls);
            if (isProc) {
            	//如果是存储过程，并且主键只有1个字段，从返回值的CallableStatement对象中获取主键值
                if (identityFields.size() == 1) {
                    rst = ((CallableStatement) ps).getObject(Common.getDBCloumnName(beanCls, identityFields.get(0)));
                }
            } else if (identityFields.size() == 1) {//自增主键必须为1
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    List<Field> idFieldList = Common.getIdFields(beanCls);
                    //如果标记ID注解的属性有1个，则根据Field类型从结果集中取出对应类型的自增ID
                    if (idFieldList.size() == 1) {
                    	Class<?> fieldClazz = idFieldList.get(0).getType();
                        if (fieldClazz == Integer.TYPE || fieldClazz == Integer.class) {
                            rst = Integer.valueOf(rs.getInt(1));
                        } else if(fieldClazz == Long.TYPE || fieldClazz == Long.class) {
                        	rst = Long.valueOf(rs.getLong(1));
                        } else if(fieldClazz == String.class) {
                        	rst = rs.getString(1);
                        } else {
                        	rst = rs.getObject(1);
                        }
                    } else {
                    	//否则直接拿Object类型
                        rst = rs.getObject(1);
                    }
                }
            } else if (identityFields.size() == 0) {//如果没有自增主键，并且ID注解的字段只有1个话，则直接获取属性值返回
                List<Field> idFields = Common.getIdFields(beanCls);
                if (idFields.size() == 1) {
                    Field id = idFields.get(0);
                    id.setAccessible(true);
                    rst = id.get(bean);
                }
            }
            return rst;
        } catch (Exception e) {
            logger.error("insert error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
        	DbUtils.closeResultSet(null);
            DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public <I> void deleteByID(Class<?> clazz, I id) throws Exception {
		deleteByID(clazz, id, this.qurryTimeOut);
	}

	@Override
	public <I> void deleteByID(Class<?> clazz, I id, int timeOut) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createDelete(clazz, conn, id, sql);
            ps.setQueryTimeout(timeOut);
            ps.execute();
        } catch (Exception e) {
            logger.error("delete error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
            DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public <I> void deleteByIDS(Class<?> clazz, I[] ids) throws Exception {
		deleteByIDS(clazz, ids, this.qurryTimeOut);
	}

	@Override
	public <I> void deleteByIDS(Class<?> clazz, I[] ids, int timeOut) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createDeleteByIDS(clazz, conn, ids, sql);
            ps.setQueryTimeout(timeOut);
            ps.execute();
        } catch (Exception e) {
            logger.error("delete error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
        	DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public void deleteByCustom(Class<?> clazz, String condition) throws Exception {
		deleteByCustom(clazz, condition, this.qurryTimeOut);
	}

	@Override
	public void deleteByCustom(Class<?> clazz, String condition, int timeOut) throws Exception {
		String condition2 = SqlInjectHelper.simpleFilterSql(condition);
        Connection conn = null;
        PreparedStatement ps = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createDeleteByCustom(clazz, conn, condition2, sql);
            ps.setQueryTimeout(timeOut);
            ps.execute();
        } catch (Exception e) {
            logger.error("delete error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
        	DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public void deleteByCustom(Class<?> clazz, Map<String, Object> condition) throws Exception {
		deleteByCustom(clazz, condition, this.qurryTimeOut);
	}

	@Override
	public void deleteByCustom(Class<?> clazz, Map<String, Object> condition, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void updateEntity(Object bean) throws Exception {
		updateEntity(bean, this.qurryTimeOut);
	}

	@Override
	public void updateEntity(Object bean, int timeOut) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createUpdateEntity(bean, conn, sql);
            ps.setQueryTimeout(timeOut);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("update error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
        	DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public <I> void updateByID(Class<?> clazz, String updateStatement, I id) throws Exception {
		updateByID(clazz, updateStatement, id, this.insertUpdateTimeOut);
	}

	@Override
	public <I> void updateByID(Class<?> clazz, String updateStatement, I id, int timeOut) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        OutSQL sql = new OutSQL();
        try {
            conn = this.connHelper.get();
            ps = this.psCreater.createUpdateByID(clazz, conn, updateStatement, id, sql);
            ps.setQueryTimeout(timeOut);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("update error sql:" + sql.getSql(), e);
            throw e;
        } catch (Throwable th) {
            throw th;
        } finally {
        	DbUtils.closeStatement(ps);
            this.connHelper.release(conn);
        }
	}

	@Override
	public void updateByCustom(Class<?> clazz, String updateStatement, String condition) throws Exception {
		updateByCustom(clazz, updateStatement, condition, this.insertUpdateTimeOut);
	}

	@Override
	public void updateByCustom(Class<?> clazz, String updateStatement, String condition, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void updateByCustom(Class<?> clazz, Map<String, Object> kv, Map<String, Object> condition) throws Exception {
		updateByCustom(clazz, kv, condition, this.insertUpdateTimeOut);
	}

	@Override
	public void updateByCustom(Class<?> clazz, Map<String, Object> kv, Map<String, Object> condition, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public <T, I> T get(Class<T> clazz, I id) throws Exception {
		return get(clazz, id, this.qurryTimeOut);
	}

	@Override
	public <T, I> T get(Class<T> clazz, I id, int timeOut) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		OutSQL sql = new OutSQL();
		try {
			conn = this.connHelper.getReadConnection();
			ps = this.psCreater.createGetEntity(clazz, conn, id, sql);
			ps.setQueryTimeout(timeOut);
			rs = ps.executeQuery();
			List<T> dataList = populateData(rs, clazz);
			if (dataList == null || dataList.size() <= 0) {
				return null;
			}
			return dataList.get(0);
		} catch (Exception e) {
			logger.error("get error sql:" + sql.getSql(), e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	@Override
	public <T, I> List<T> getListByIDS(Class<T> clazz, I[] ids) throws Exception {
		return getListByIDS(clazz, ids, this.qurryTimeOut);
	}

	@Override
	public <T, I> List<T> getListByIDS(Class<T> clazz, I[] ids, int timeOut) throws Exception {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		OutSQL sql = new OutSQL();
		try {
			conn = this.connHelper.getReadConnection();
			ps = this.psCreater.createGetByIDS(clazz, conn, ids, sql);
			ps.setQueryTimeout(timeOut);
			rs = ps.executeQuery();
			return populateData(rs, clazz);
		} catch (SQLException e) {
			logger.error("getListByCustom error sql:" + sql.getSql(), e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	@Override
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, String condition, String orderBy) throws Exception {
		return getListByCustom(clazz, columns, condition, orderBy, this.qurryTimeOut);
	}

	@Override
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, String condition, String orderBy, int timeOut) throws Exception {
		String columns2 = SqlInjectHelper.simpleFilterSql(columns);
		String condition2 = SqlInjectHelper.simpleFilterSql(condition);
		String orderBy2 = SqlInjectHelper.simpleFilterSql(orderBy);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		OutSQL sql = new OutSQL();
		try {
			conn = this.connHelper.getReadConnection();
			ps = this.psCreater.createGetByCustom(clazz, conn, columns2, condition2, orderBy2, sql);
			ps.setQueryTimeout(timeOut);
			rs = ps.executeQuery();
			return populateData(rs, clazz);
		} catch (SQLException e) {
			logger.error("getListByCustom error sql:" + sql.getSql(), e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	@Override
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, Map<String, Object> condition, String orderBy) throws Exception {
		return getListByCustom(clazz, columns, condition, orderBy, this.qurryTimeOut);
	}

	@Override
	public <T> List<T> getListByCustom(Class<T> clazz, String columns, Map<String, Object> condition, String orderBy, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public <T> List<T> getListByPage(Class<T> clazz, String condition, String columns, int page, int pageSize, String orderBy) throws Exception {
		return getListByPage(clazz, condition, columns, page, pageSize, orderBy, this.qurryTimeOut);
	}

	@Override
	public <T> List<T> getListByPage(Class<T> clazz, String condition, String columns, int page, int pageSize, String orderBy, int timeOut) throws Exception {
		String columns2 = SqlInjectHelper.simpleFilterSql(columns);
		String condition2 = SqlInjectHelper.simpleFilterSql(condition);
		String orderBy2 = SqlInjectHelper.simpleFilterSql(orderBy);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		OutSQL sql = new OutSQL();
		try {
			conn = this.connHelper.getReadConnection();
			ps = this.psCreater.createGetByPage(clazz, conn, condition2, columns2, page, pageSize, orderBy2, sql);
			ps.setQueryTimeout(timeOut);
			rs = ps.executeQuery();
			return populateData(rs, clazz);
		} catch (Exception e) {
			logger.error("getListByPage error sql:" + sql.getSql(), e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}

	}

	@Override
	public <T> List<T> getListByPage(Class<T> clazz, Map<String, Object> condition, String columns, int page, int pageSize, String orderBy) throws Exception {
		return getListByPage(clazz, condition, columns, page, pageSize, orderBy, this.qurryTimeOut);
	}

	@Override
	public <T> List<T> getListByPage(Class<T> clazz, Map<String, Object> condition, String columns, int page, int pageSize, String orderBy, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public int getCount(Class<?> clazz, String condition) throws Exception {
		return getCount(clazz, condition, this.qurryTimeOut);
	}

	@Override
	public int getCount(Class<?> clazz, String condition, int timeOut) throws Exception {
		String condition2 = SqlInjectHelper.simpleFilterSql(condition);
		int count = 0;
		OutSQL sql = new OutSQL();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.connHelper.getReadConnection();
			ps = this.psCreater.createGetCount(clazz, conn, condition2, sql);
			ps.setQueryTimeout(timeOut);
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			return count;
		} catch (Exception e) {
			logger.error("getCount error sql:" + sql.getSql(), e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	@Override
	public int getCount(Class<?> clazz, Map<String, Object> condition) throws Exception {
		return getCount(clazz, condition, this.qurryTimeOut);
	}

	@Override
	public int getCount(Class<?> clazz, Map<String, Object> condition, int timeOut) throws Exception {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public <T> List<T> getListBySQL(Class<T> clazz, String sql, Object... param) throws Exception {
		return getListBySQL(clazz, sql, this.qurryTimeOut, param);
	}

	@Override
	public <T> List<T> getListBySQL(Class<T> clazz, String sql, int timeOut, Object... param) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.connHelper.getReadConnection();
			ps = conn.prepareStatement(sql);
			ps.setQueryTimeout(timeOut);
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					Common.setPara(ps, param[i], i + 1);
				}
			}
			rs = ps.executeQuery();
			return populateData(rs, clazz);
		} catch (SQLException e) {
			logger.error("getListByCustom error sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}

	}

	@Override
	public int execBySQL(String sql, Object... param) throws Exception {
		return execBySQL(sql, this.insertUpdateTimeOut, param);
	}

	@Override
	public int execBySQL(String sql, int timeOut, Object... param) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.connHelper.get();
			ps = conn.prepareStatement(sql);
			ps.setQueryTimeout(timeOut);
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					Common.setPara(ps, param[i], i + 1);
				}
			}
			return ps.executeUpdate();
		} catch (SQLException e) {
			logger.error("getListByCustom error sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}

	}

	@Override
	public int getCountBySQL(String sql, Object... param) throws Exception {
		return getCountBySQL(sql, this.qurryTimeOut, param);
	}

	@Override
	public int getCountBySQL(String sql, int timeOut, Object... param) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.connHelper.getReadConnection();
			ps = conn.prepareStatement(sql);
			ps.setQueryTimeout(timeOut);
			if (param != null) {
				for (int i = 0; i < param.length; i++) {
					Common.setPara(ps, param[i], i + 1);
				}
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (Exception ex) {
			throw ex;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(ps);
			this.connHelper.release(conn);
		}
	}

	@Override
	public List<Object[]> customSql(String sql, int columnCount) throws Exception {
		return customSql(sql, columnCount, this.qurryTimeOut);
	}

	@Override
	public List<Object[]> customSql(String sql, int columnCount, int timeOut) throws Exception {
		List<Object[]> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			conn = this.connHelper.get();
			stmt = conn.createStatement();
			stmt.setQueryTimeout(timeOut);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object[] objAry = new Object[columnCount];
				for (int i = 0; i < columnCount; i++) {
					objAry[i] = rs.getObject(i + 1);
				}
				list.add(objAry);
			}
			return list;
		} catch (Exception e) {
			logger.error("sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeResultSet(rs);
			DbUtils.closeStatement(stmt);
			this.connHelper.release(conn);
		}
	}

	@Override
	public void customSqlNoReturn(String sql) throws Exception {
		customSqlNoReturn(sql, this.qurryTimeOut);
	}

	@Override
	public void customSqlNoReturn(String sql, int timeOut) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = this.connHelper.get();
			stmt = conn.createStatement();
			stmt.setQueryTimeout(timeOut);
			stmt.execute(sql);
		} catch (Exception e) {
			logger.error("sql:" + sql, e);
			throw e;
		} catch (Throwable th) {
			throw th;
		} finally {
			DbUtils.closeStatement(stmt);
			this.connHelper.release(conn);
		}
	}

}
