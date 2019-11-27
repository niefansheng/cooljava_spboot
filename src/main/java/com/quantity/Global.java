package com.quantity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.util.PropertiesLoader;
import com.util.StringUtils;

/**
 * CoolJava全局常量类
 * 
 * @author wangh
 * 
 */
public class Global
{
	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();
	
	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader("classpath:dbconfig.properties");
	
	/**
	 * 检查点层级：一级
	 */
	public final static String CHECK_LEVEL_ONE = "1";
	
	/**
	 * 检查点层级：二级
	 */
	public final static String CHECK_LEVEL_TWO = "2";
	
	/**
	 * 检查点层级：三级
	 */
	public final static String CHECK_LEVEL_THREE = "3";
	
	/**
	 * 检查点层级：四级
	 */
	public final static String CHECK_LEVEL_FOUR = "4";

	/**
	 * 是
	 */
	public static final String YES = "1";

	/**
	 * 否
	 */
	public static final String NO = "0";

	//linux环境文件存储位置
	public static final String LINUXFILEPATH = "/usr/local/fileManager/";

	//windows环境文件存储位置
	public static final String WINFILEPATH = "F:\\fileManager/";

	//判断操作系统种类
	public static boolean getOS(){
		String osname = System.getProperty("os.name");
		boolean flag = osname.contains("Windows");
		return flag;
	}

	//获取当前年份
	public static String getYear(){
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		return year;
	}

	/**
	 * 获取配置
	 * @see
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null){
			value = loader.getProperty(key);
			map.put(key, value != null ? value : StringUtils.EMPTY);
		}
		return value;
	}
	
}
