package cg.zz.spat.dao.util;

/**
 * 
 * SQL字符串维护对象
 * 
 * @author chengang
 *
 */
public class OutSQL {

	private String sql;

	public OutSQL() {
		
	}

	public OutSQL(String sql) {
		this.sql = sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return this.sql;
	}

}
