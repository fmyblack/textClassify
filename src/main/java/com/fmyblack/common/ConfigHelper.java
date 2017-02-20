package com.fmyblack.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigHelper {

	private static Map<String, Properties> confMap = new HashMap<String, Properties>();

	/**
	 * 若conf文件夹在项目同级目录下，可以用它来初始化
	 */
	public static void init() {
		init(ConfigHelper.class.getClass().getResource("/").getPath() + File.separator + "conf");
	}
	
	/**
	 * 我们默认main函数中的args参数第一项为配置文件路径
	 * 如果调用时没有传参数，它会调用init()函数，这使得整个方法总是安全的
	 * @param args
	 */
	public static void init(String[] args) {
		if(args == null || args.length == 0) {
			init();
		} else {
			init(args[0]);
		}
	}
	
	/**
	 * file_path可以是文件路径或者文件夹路径
	 * 如果是文件夹，它会遍历文件夹下的所有文件
	 * @param file_path
	 */
	public static void init(String file_path) {
		if(file_path == null) {
			init();
		} else {
			File dir = new File(file_path);
			init(dir);
		}
	}

	/**
	 * @param dir
	 */
	private static void init(File dir) {
		if(dir == null) {
			init();
		} else if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				init(f);
			}
		} else {
			init_conf(dir);
		}
	}

	private static void init_conf(File f) {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(f);
			prop.load(in);
			f.getName();
			System.out.println(f.getName());
			confMap.put(f.getName(), prop);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getConf(String conf, String key) {
		return getConf(conf, key, null);
	}
	
	/**
	 * 获取配置，conf为配置文件名，key为配置项
	 * 如果文件名以常用的properties结尾，则无需传递文件拓展名
	 * @param conf
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getConf(String conf, String key, String defaultValue) {
		if(!conf.contains(".")) {
			conf = conf + "." + "properties";
		}
		if(confMap.containsKey(conf)) {
			return confMap.get(conf).getProperty(key, defaultValue);
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		//ConfigHelper.init();
		ConfigHelper.init(args);
		System.out.println(getConf("config", "test"));
	}
}
