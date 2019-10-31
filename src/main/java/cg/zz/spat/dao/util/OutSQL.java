package cg.zz.spat.dao.util;

/**
 * 
 * SQL字符串维护，这个传入到方法内后，在方法内拼接的SQL存储到这里，以便外部使用，如日子打印等
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
