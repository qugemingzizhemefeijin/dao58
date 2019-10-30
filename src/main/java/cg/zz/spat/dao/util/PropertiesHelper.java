package cg.zz.spat.dao.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 配置文件工具类
 * 
 * @author chengang
 *
 */
public class PropertiesHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesHelper.class);

	private Properties pro = null;

	/**
	 * 创建对象并加载指定路径的配置文件
	 * @param path - String
	 * @throws Exception
	 */
	public PropertiesHelper(String path) throws Exception {
		this.pro = loadProperty(path);
	}

	/**
	 * 根据指定的流加载数据到配置对象中
	 * @param inputStream - InputStream
	 */
	public PropertiesHelper(InputStream inputStream) {
		this.pro = new Properties();
		try {
			this.pro.load(inputStream);
		} catch (IOException e) {
			logger.error(e.getMessage() , e);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return String
	 * @throws Exception
	 */
	public String getString(String key) throws Exception {
		try {
			return this.pro.getProperty(key);
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return int
	 * @throws Exception
	 */
	public int getInt(String key) throws Exception {
		try {
			return Integer.parseInt(this.pro.getProperty(key));
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return double
	 * @throws Exception
	 */
	public double getDouble(String key) throws Exception {
		try {
			return Double.parseDouble(this.pro.getProperty(key));
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return long
	 * @throws Exception
	 */
	public long getLong(String key) throws Exception {
		try {
			return Long.parseLong(this.pro.getProperty(key));
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return float
	 * @throws Exception
	 */
	public float getFloat(String key) throws Exception {
		try {
			return Float.parseFloat(this.pro.getProperty(key));
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的值
	 * @param key - 名称
	 * @return boolean
	 * @throws Exception
	 */
	public boolean getBoolean(String key) throws Exception {
		try {
			return Boolean.parseBoolean(this.pro.getProperty(key));
		} catch (Exception e) {
			throw new Exception("key:" + key);
		}
	}

	/**
	 * 获取配置文件中的所有的key
	 * @return Set<Object>
	 * @throws Exception
	 */
	public Set<Object> getAllKey() {
		return this.pro.keySet();
	}
	/**
	 * 获取配置文件中的所有的值
	 * @return Collection<Object>
	 * @throws Exception
	 */
	public Collection<Object> getAllValue() {
		return this.pro.values();
	}

	/**
	 * 返回配置文件中所有配置项
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getAllKeyValue() {
		Map<String, Object> mapAll = new HashMap<>();
		Set<Object> keys = getAllKey();

		Iterator<Object> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			mapAll.put(key, this.pro.get(key));
		}
		return mapAll;
	}

	/**
	 * 加载指定路径的配置文件中数据库配置
	 * @param filePath - 文件路径
	 * @return Properties
	 * @throws Exception
	 */
	private Properties loadProperty(String filePath) throws Exception {
		Properties pro = new Properties();
		try (FileInputStream fis = new FileInputStream(filePath);) {
			pro.load(fis);
			
			return pro;
		} catch (IOException e) {
			throw e;
		}
	}

}
