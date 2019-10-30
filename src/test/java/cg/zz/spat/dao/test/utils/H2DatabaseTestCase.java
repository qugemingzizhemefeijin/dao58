package cg.zz.spat.dao.test.utils;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.core.dbms.DbUtils;
import cg.zz.spat.dao.basedao.DAOBase;
import cg.zz.spat.dao.basedao.DAOHelper;
import cg.zz.spat.dao.util.Path;

public class H2DatabaseTestCase {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(H2DatabaseTestCase.class);
	
	protected static DAOHelper daoHelpers = null;
	
	@BeforeAll
	public static void setUp() throws Exception {
		LOGGER.info("初始化数据库连接池======");
		daoHelpers = DAOBase.createIntrance(Path.getCurrentPath() + "/h2.properties");
		initSchema();
	}
	
	/**
	 * 初始化h2数据库测试数据
	 * @throws Exception
	 */
	private static void initSchema() throws Exception {
		LOGGER.info("初始化测试数据======");
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = daoHelpers.getConnHelper().get();
			stmt = conn.createStatement();
			
			stmt.execute("drop all objects;");  
			stmt.execute("runscript from '" + Paths.get(Path.getCurrentPath() + "/sql/ddl.sql").toUri().toString() + "'");
			stmt.execute("runscript from '" + Paths.get(Path.getCurrentPath() + "/sql/dml.sql").toUri().toString() + "'");
		} catch (Exception e) {
			LOGGER.error(e.getMessage() , e);
		} finally {
			DbUtils.closeStatement(stmt);
			if(daoHelpers != null) {
				daoHelpers.getConnHelper().release(conn);
			}
		}
	}

}
