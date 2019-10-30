package cg.zz.spat.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 应该是发送告警信息用的
 * 
 * @author chengang
 *
 */
public class MessageAlertFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageAlertFactory.class);

	private static String config;
	private static int port;
	private static String smsIp;
	private static String mobiles;
	private static String title;
	private static Map<String, Long> contentTimeMap = new ConcurrentHashMap<>();
	private static final long interval = 300000L;

	private static MessageAlert noAlert = new MessageAlert() {
		
		@Override
		public void sendMessage(String message) {
			
		}
	};
	
	private static MessageAlert alert = new MessageAlert() {
		
		@Override
		public void sendMessage(String message) {
			long time = System.currentTimeMillis();
			Long lasttime = MessageAlertFactory.contentTimeMap.get(message);
			if (lasttime != null && time - lasttime.longValue() < MessageAlertFactory.interval) {
				return;
			}
			MessageAlertFactory.contentTimeMap.put(message, time);
			if (MessageAlertFactory.mobiles == null || MessageAlertFactory.mobiles.length() < 5) {
				return;
			}
			String sendDataStr = "{\"mobile\":[" + MessageAlertFactory.mobiles + "],\"content\":\"" + MessageAlertFactory.title + message + "\"}";
			logger.info(sendDataStr);
			logger.info(MessageAlertFactory.smsIp +":" + MessageAlertFactory.port);
			try {
				//发送信息
				//UDPClient.sendMsg(sendDataStr, MessageAlertFactory.smsIp, MessageAlertFactory.port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static void setConfig(String config) {
		System.out.println("Alert config:" + config);
		MessageAlertFactory.config = config;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int getMaxThreadsPerDs() {
		int max_threads_per_ds = 5;
		if (config == null) {
			return max_threads_per_ds;
		}
		String alertConfig = config + "/alert.config";
		File alertFile = new File(alertConfig);
		if (!alertFile.exists()) {
			return max_threads_per_ds;
		}
		try {
			HashMap<String, String> receiversPhones = (HashMap) Yaml.loadType(alertFile, HashMap.class);
			max_threads_per_ds = receiversPhones.containsKey("max_threads_per_ds") ? Integer.parseInt((String) receiversPhones.get("max_threads_per_ds")) : 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max_threads_per_ds;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static MessageAlert get() {
		if (config == null) {
			return noAlert;
		}
		String alertConfig = config + "/alert.config";
		File alertFile = new File(alertConfig);
		if (!alertFile.exists()) {
			return noAlert;
		}
		try {
			HashMap<String, String> receiversPhones = (HashMap) Yaml.loadType(alertFile, HashMap.class);
			String[] phones = receiversPhones.get("receiver").split(",");

			smsIp = receiversPhones.get("smsIp");
			port = Integer.parseInt(receiversPhones.get("port"));
			title = receiversPhones.get("title");
			if (title == null) {
				title = "";
			}
			StringBuilder sb = new StringBuilder();
            int length = phones.length;
            for (int i = 0; i < length; i++) {
                sb.append("\"" + phones[i] + "\",");
            }
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			mobiles = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (mobiles.equals("")) {
			return noAlert;
		}
		return alert;
	}

}
