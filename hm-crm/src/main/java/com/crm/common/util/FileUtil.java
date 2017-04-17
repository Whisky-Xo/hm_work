package com.crm.common.util;



public class FileUtil {
	public static final String windowsPath = "C:\\dev\\shopPicture\\";
	public static final String linuxPath = "//root//uploadPicture//"; 

	// 判断系统 并使用不同路径
	public static String  getOsPath() {
		String os = System.getProperty("os.name");  
        if(os.toLowerCase().startsWith("win")){  
        	return windowsPath.toString();
        }else {
        	return linuxPath.toString();
		}
	}





}
