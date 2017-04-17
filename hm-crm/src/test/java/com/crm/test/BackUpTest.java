package com.crm.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class BackUpTest {
	
	
    /** 
     * 备份数据库 
     *  
     * @param output 
     *            输出流 
     * @param dbname 
     *            要备份的数据库名 
     */  
	public static String comman="E:/Program Files/MySQL/MySQL Server 5.7/bin/mysql.exe -uroot -p12345 sun";
	
    public static void backup(String mySqlBackupName,String mysqlBackupPath, String command) {  
    	String fPath=mysqlBackupPath+"/"+new Date().getTime()+".sql";
        
        Runtime rt = Runtime.getRuntime();
        try {
            Process child = rt.exec(command);
            InputStream in = child.getInputStream();
           InputStreamReader input = new InputStreamReader(in,"utf8");
           
           String inStr;
           StringBuffer sb = new StringBuffer("");
           String outStr;
           
           BufferedReader br = new BufferedReader(input);
           System.out.println(br.readLine());
           while ((inStr = br.readLine()) != null) {     
               sb.append(inStr + "\r\n");     
           }     
           outStr = sb.toString(); 
           
           FileOutputStream fout = new FileOutputStream(fPath);
           OutputStreamWriter writer = new OutputStreamWriter(fout, "utf8");    
           writer.write(outStr);
           writer.flush();   
           
           in.close();     
          input.close();     
           br.close();     
           writer.close();     
           fout.close();     
           
           System.out.println("MYSQL备份成功");
       } catch (Exception e) {
           e.printStackTrace();
       } 
    }  
 
   
    public static void main(String[] args) {
    	
    	
//    	backup("ttt123.sql", "d:/", comman);
//        String binPath = "E:/Program Files/MySQL/MySQL Server 5.7/bin";  
//        String userName = "root";  
//        String pwd = "12345";
//        bak.backup("d:/ttt.sql", "sun");  
    	
    	String ss="已接单,#FF6C00";
    	String[] zz=ss.split(",");
    	System.out.println(zz[1]);
    	
    	
    }  
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
	
}
