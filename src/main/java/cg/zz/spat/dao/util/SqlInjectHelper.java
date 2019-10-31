package cg.zz.spat.dao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * SQL防注入工具类
 * @author chengang
 *
 */
public final class SqlInjectHelper {

	private static Map<String, String> mapKeyWord = new HashMap<>();
	private static Map<String, SqlKey> mapSqlKey = new HashMap<>();
	private static List<SqlKey> keyList = new ArrayList<>();
	private static Pattern fullPatternSql = null;
	private static Pattern simplePatternSql = null;
	
	static {
		fullPatternSql = null;
        simplePatternSql = null;
        //这些应该要写入配置文件中最好了
        keyList.add(new SqlKey("sp_", "ｓｐ_", "sp_", true));
        keyList.add(new SqlKey("xp_", "ｘｐ＿", "xp_", true));
        keyList.add(new SqlKey("0x", "０ｘ", "0x", true));
        keyList.add(new SqlKey("--", "－－", "--", true));
        keyList.add(new SqlKey(";", "；", ";", true));
        keyList.add(new SqlKey("exec", "ｅｘｅｃ", "[^0-9a-zA-Z_]exec[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("call", "ｃａｌｌ", "[^0-9a-zA-Z_]call[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("declare", "ｄｅｃｌａｒｅ", "[^0-9a-zA-Z_]declare[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("netuser", "ｎｅｔ ｕｓｅｒ", "[^0-9a-zA-Z_]net\\s+user[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("ascii", "ａｓｃｉｉ", "[^0-9a-zA-Z_]ascii[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("char", "ｃｈａｒ", "[^0-9a-zA-Z_]char[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("cast", "ｃａｓｔ", "[^0-9a-zA-Z_]cast[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("unicode", "ｕｎｉｃｏｄｅ", "[^0-9a-zA-Z_]unicode[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("insert", "ｉｎｓｅｒｔ", "[^0-9a-zA-Z_]insert[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("delete", "ｄｅｌｅｔｅ", "[^0-9a-zA-Z_]delete[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("update", "ｕｐｄａｔｅ", "[^0-9a-zA-Z_]update[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("drop", "ｄｒｏｐ", "[^0-9a-zA-Z_]drop[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("create", "ｃｒｅａｔｅ", "[^0-9a-zA-Z_]create[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("database", "ｄａｔａｂａｓｅ", "[^0-9a-zA-Z_]database[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("master", "ｍａｓｔｅｒ", "[^0-9a-zA-Z_]master[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("truncate", "ｔｒｕｎｃａｔｅ", "[^0-9a-zA-Z_]truncate[^0-9a-zA-Z_]", true));
        keyList.add(new SqlKey("select", "ｓｅｌｅｃｔ", "[^0-9a-zA-Z_]select[^0-9a-zA-Z_]", false));
        keyList.add(new SqlKey("from", "ｆｒｏｍ", "[^0-9a-zA-Z_]from[^0-9a-zA-Z_]", false));
        keyList.add(new SqlKey("where", "ｗｈｅｒｅ", "[^0-9a-zA-Z_]where[^0-9a-zA-Z_]", false));
        keyList.add(new SqlKey("'", "＇", "'", false));
        StringBuffer sbFullKey = new StringBuffer();
        StringBuffer sbSimpleKey = new StringBuffer();
        for (SqlKey key : keyList) {
        	//这里简单的过滤是将危险的SQL给替换掉
            if (key.isSerious) {
                sbSimpleKey.append("|");
                //右侧的表达式开启忽略大小写模式
                sbSimpleKey.append("(?i)");
                sbSimpleKey.append(key.getPattern());
            }
            
            //这里是添加了一些select from where '号等给替换掉
            sbFullKey.append("|");
            //右侧的表达式开启忽略大小写模式
            sbFullKey.append("(?i)");
            sbFullKey.append(key.getPattern());
            
            //维护key和替换后字符的映射关系，下面filterSql会使用到
            mapKeyWord.put(key.getKey(), key.getReplace());
            //对于有强迫症的人来说，替换为Set不是更好。。。
            mapSqlKey.put(key.getKey(), key);
        }
        sbFullKey.replace(0, 1, "");
        sbSimpleKey.replace(0, 1, "");
        //将拼接的正则编译位Pattern
        fullPatternSql = Pattern.compile(sbFullKey.toString(), 2);
        simplePatternSql = Pattern.compile(sbSimpleKey.toString(), 2);
	}
	
	/**
	 * 应用全部的过滤替换规则
	 * @param sql - String
	 * @return String
	 */
	public static String filterSql(String sql) {
		return filterSql(sql, fullPatternSql);
	}

	/**
	 * 只应用危险的规则进行过滤替换
	 * @param sql - String
	 * @return String
	 */
	public static String simpleFilterSql(String sql) {
		return filterSql(sql, simplePatternSql);
	}

	private static String filterSql(String sql, Pattern pattern) {
		if (sql != null && !sql.equalsIgnoreCase("")) {
			StringBuilder sbSql = new StringBuilder(sql);
			sbSql.insert(sbSql.length(), ' ');
			sbSql.insert(0, ' ');
			sql = sbSql.toString();
			Matcher matcher = pattern.matcher(sql);
			while (matcher.find()) {
				String key = matcher.group().toLowerCase();
				String regex = null;
				if (mapSqlKey.containsKey(key)) {
					regex = key;
				} else if (key.length() > 2) {
					String key2 = key.trim();
					regex = "[^0-9a-zA-Z_](?i)" + key2 + "[^0-9a-zA-Z_]";
					key = key2.replaceAll("\\s", "");
				}
				String replace = mapKeyWord.get(key);
				//这里只替换第一个符合条件的字符
				if (regex != null && replace != null) {
					sql = sql.replaceFirst(regex, replace);
				}
			}
		}
		return sql;
	}
	
	public static class SqlKey {
		
		/**
		 * KEY名称
		 */
		private String key;
		
		/**
		 * 匹配的正则表达式
		 */
		private String pattern;
		
		/**
		 * 要替换的字符
		 */
		private String replace;
		
		/**
		 * 是否是危险的SQL
		 */
		private boolean isSerious;
		
		/**
		 * KEY的字符数组，没啥用
		 */
		private char[] charArray;

		/**
		 * 构造SqlKey对象
		 * @param key - 过滤规则名称
		 * @param replace - 替换后的字符
		 * @param pattern - 替换匹配的正则字符
		 * @param isSerious - 是否是危险的
		 */
		public SqlKey(String key, String replace, String pattern, boolean isSerious) {
			setKey(key);
			setReplace(replace);
			setPattern(pattern);
			setSerious(isSerious);
			setCharArray(key.toCharArray());
		}

		public String getKey() {
			return this.key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getReplace() {
			return this.replace;
		}

		public void setReplace(String replace) {
			this.replace = replace;
		}

		public void setSerious(boolean isSerious) {
			this.isSerious = isSerious;
		}

		public boolean isSerious() {
			return this.isSerious;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return this.pattern;
		}

		public void setCharArray(char[] charArray) {
			this.charArray = charArray;
		}

		public char[] getCharArray() {
			return this.charArray;
		}
	}
	
	private SqlInjectHelper() {
		
	}

}
