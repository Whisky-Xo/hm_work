package com.crm.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelTest {
	
	
	public void writeExcel(OutputStream out, BufferedReader br) {
        if(br == null) {
            throw new IllegalArgumentException("写excel流需要file!");
        }
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(out);
            WritableSheet ws = workbook.createSheet("sheet 1", 0);
            int rowNum = 0; // 写入行数
             
            //文件行数
            String line = null;
            while((line = br.readLine()) != null){
                String[] data = line.split("\t");
                Object[] cells = (Object[]) data;
                putRow(ws, rowNum, cells); // 写一行到sheet
                rowNum++;
                
            }
             
            workbook.write();
            workbook.close(); // 关闭、保存Excel
             
            System.out.print("success!");
        } catch (RowsExceededException e) {
            System.out.println("jxl write RowsExceededException: "+e.getMessage());
        } catch (WriteException e) {
            System.out.println("jxl write WriteException: "+e.getMessage());
        } catch (IOException e) {
            System.out.println("jxl write file i/o exception!, cause by: "+e.getMessage());
        }
    }
	
	
	private void putRow(WritableSheet ws, int rowNum, Object[] cells) throws RowsExceededException, WriteException {
        
        for(int i=0; i<cells.length; i++) {
            Label cell = new Label(i, rowNum, ""+cells[i]);
            ws.addCell(cell);
        }
    }
	
	
	
	public static void main(String args[]) {
		
		
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("d:/data.txt"));
            OutputStream out = new FileOutputStream(new File("d:/test.xls"));
             
            ExcelTest jxlExcelWriter = new ExcelTest();
            jxlExcelWriter.writeExcel(out, br);
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
    }
	
	
	
	
	

//	public static void main(String[] args) throws Exception {
//        WritableWorkbook wwb = Workbook.createWorkbook(new File("d:/test.xls"));
//        for (int i = 0; i < 10; i++) {
//            WritableSheet sheet = wwb.createSheet("测试" + i, i);
//            Label lable = new Label(0, 0, "测试内容" + i);
//            sheet.addCell(lable);
// 
//        }
//        wwb.write();
//        wwb.close();
//        System.out.println("完成");
//    }
 
	
    
    
    
//	final String localPicturePath ="E:\\excel\\";
//	UUID uuid = UUID.randomUUID();
//    File excleFile = new File(localPicturePath , "ss.xls");
//    if (!excleFile.getParentFile().exists()) {
//    	excleFile.getParentFile().mkdirs();
//	}
//
////
////    //检查文件是否已存在，存在则删除  
////    if(excleFile.exists()) excleFile.delete();  
////      
////    //建立新的文件  
//    excleFile.createNewFile();
    
    
    
    
    
    
    
    
    
    
	
	
	
}
