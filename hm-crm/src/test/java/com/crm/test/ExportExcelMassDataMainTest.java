package com.crm.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
  
import jxl.write.WriteException;  
  
public class ExportExcelMassDataMainTest {  
      
  
    /**   
     * @创建人:  
     * @时间 : 2014-7-22 下午04:29:15   
     * @功能 : TODO  
     * @param args     
     * @throws IOException  
     * @throws WriteException  
     */  
    public static void main(String[] args) {  
        long start = System.currentTimeMillis();  
        ExportExcelMassData tool = null;  
        try {  
        tool = new ExportExcelMassData();  
      
        // 执行查询操作         
        List list = new ArrayList();  
          
        OutputStream os = new FileOutputStream("d:/test.xls");  
          
        //excel表头  
        List<Map> argList = new ArrayList<Map>();  
        Map<String, String> title = new HashMap<String, String>();  
        title.put("COL_NUM_ONE", "第一列");  
        title.put("COL_NUM_TWO", "第二列");  
        title.put("COL_NUM_THREE", "第三列");  
        title.put("COL_NUM_FOUR", "第四列");  
        title.put("COL_NUM_FIVE", "第五列");  
        argList.add(title);  
        //组织Excel，各列数据，一个for循环一行  
        Map<String, String> seqmap = new HashMap<String, String>();  
        seqmap.put("COL_NUM_ONE", "0");  
        seqmap.put("COL_NUM_TWO", "1");  
        seqmap.put("COL_NUM_THREE", "2");  
        seqmap.put("COL_NUM_FOUR", "3");  
        seqmap.put("COL_NUM_FIVE", "4");  
  
          
        tool.setTotalSize(80000);//总数8万条，每次请求5000条数据，每个工作表最多1万行  
        tool.setPerPageSize(5000);  
        tool.setPerSheetMaxRows(10000);  
        tool.init(os);  
        tool.setMapTitle(title);  
        tool.setMapColumn(seqmap);  
        System.out.println("请求次数："+(tool.getDataTimes));  
        for (int k=0; k<tool.getDataTimes; k++) {  
            System.out.println("请求数据："+(k+1)+"\t开始行号："+tool.firstRow);  
  
            //自己模拟获取后台数据，实际应用时，将这段代码换成调用后台分页查询接口获取数据  
            for (int i=tool.firstRow; (i<(tool.firstRow+tool.perPageSize) && i<8000); i++) {  
                Map map = new HashMap();  
                map.put("COL_NUM_ONE",i);  
                map.put("COL_NUM_TWO", i+1);  
                map.put("COL_NUM_THREE", i+2);  
                map.put("COL_NUM_FOUR", new Integer(23));  
                map.put("COL_NUM_FIVE", new BigDecimal(20));  
                list.add(map);  
            }  
              
            //传入一次数据  
            tool.writeExcel(list);  
              
            list.clear();  
            list = new ArrayList();  
        }  
  
          
        }catch (Exception e) {  
            System.out.println(e.toString());  
  
  
        } finally {  
            try {  
                tool.closed();  
            } catch (WriteException e) {  
                System.out.println(e.toString());  

            } catch (IOException e) {  
                System.out.println(e.toString());  
  
            }  
        }  
        System.out.println("耗时："+(System.currentTimeMillis()-start));  
    }  
} 