package com.crm.test;  
  
import java.io.BufferedOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Locale;  
  
import java.util.Map;  
  
  
import jxl.Workbook;  
import jxl.WorkbookSettings;  
import jxl.write.Label;  
import jxl.write.WritableSheet;  
import jxl.write.WritableWorkbook;  
import jxl.write.WriteException;  
import jxl.write.biff.RowsExceededException;  
  
import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  
  
/** 
 *  
 * @author  
 * @时间 : 2014-7-24 上午09:23:47   
 * @功能 : TODO 大数据量导出Excel 
 */  
public class ExportExcelMassData {  
    private static final Log log = LogFactory.getLog(ExportExcelMassData.class);  
  
    public int totalSize;//总行数  
    public int firstRow = 0;    // 开始请求数据行号   
    public int getDataTimes;//请求数据次数 totalSize / perPageSize + 1  
      
    public int perPageSize = 5000; // 一次请求分页大小  
    public int perSheetMaxRows = 10000; //每个工作表最大行数  
      
    public Map mapTitle = null; //列头  
    public Map mapColumn = null;    //列数据  
    public List argList = null; //导出数据  
      
    //导出流  
    private OutputStream toClient;  
    // 得到一个工作薄  
    private WritableWorkbook workbook;  
    // 得到一个工作表  
    private WritableSheet lastSheet;  
    //总的工作表数目  
    private int totalSheets = 0;  
    //最后一行行号  
    private int lastRow = 0;      
      
    public Map getMapTitle() {  
        return mapTitle;  
    }  
  
    public void setMapTitle(Map mapTitle) {  
        this.mapTitle = mapTitle;  
    }  
  
    public Map getMapColumn() {  
        return mapColumn;  
    }  
  
    public void setMapColumn(Map mapColumn) {  
        this.mapColumn = mapColumn;  
    }  
  
    public int getTotalSize() {  
        return totalSize;  
    }  
  
    public void setTotalSize(int totalSize) {  
        this.totalSize = totalSize;  
    }  
  
    public int getPerPageSize() {  
        return perPageSize;  
    }  
  
    public void setPerPageSize(int perPageSize) {  
        this.perPageSize = perPageSize;  
    }  
  
    public int getPerSheetMaxRows() {  
        return perSheetMaxRows;  
    }  
  
    public void setPerSheetMaxRows(int perSheetMaxRows) {  
        this.perSheetMaxRows = perSheetMaxRows;  
    }  
  
    public void init(OutputStream os) throws IOException {  
        // 查询次数  
        getDataTimes = totalSize / perPageSize + 1;  
/*      //计算工作表数目 
        totalSheets = totalSize / perSheetMaxRows + 1;*/  
          
        toClient = new BufferedOutputStream(os);  
        // 得到一个工作薄  
        workbook = Workbook.createWorkbook(toClient);  
          
        if (log.isDebugEnabled()) { log.debug("JXL版本："+Workbook.getVersion());}  
        WorkbookSettings ws = new WorkbookSettings();  
        ws.setLocale(new Locale("en", "EN"));  
    }  
    /** 
     *  
     * @创建人:  
     * @时间 : 2014-7-24 上午10:18:08   
     * @功能 : TODO 数据合法性 
     * @param list 
     * @return 
     */  
    private List<Map> checkDatas(List<Map> list) {  
        List<Map> rtnlist = new ArrayList<Map>();;  
        Map map = null;  
        Map temp = null;  
        String key = null;  
        String value = null; // 值  
        while (list.size() > 0) {  
            map = (Map) list.get(0);  
            temp = new HashMap();  
            Iterator it = mapTitle.keySet().iterator();  
            while (it.hasNext()) {  
                key = (String) it.next();  
                value = map.get(key) == null ? "" : map.get(key).toString();                      
                temp.put(key, value);                 
            }  
            rtnlist.add(temp);  
            temp = null;  
            list.remove(0);  
              
        }  
  
        return rtnlist;  
    }  
      
    /** 
     *  
     * @创建人: yinxm 
     * @时间 : 2014-7-24 上午10:19:45   
     * @功能 : TODO 写入excel 
     * @param datas 
     * @throws RowsExceededException 
     * @throws WriteException 
     * @throws IOException 
     */  
    public void writeExcel(List datas) throws RowsExceededException, WriteException, IOException {  
          
        List<Map> list = checkDatas(datas);  
          
        int length = list.size();  
        Map map = null;  
        String key = null;  
        String value = null; // 值  
        Label label = null; // 行  
        int col = 0; // 列  
  
        for (int i = 0; i < length; i++) {  
            if (lastRow >= perSheetMaxRows || lastRow == 0) {// 创建新工作表：当前行数超过本表限制条数，或者初始开始数据  
                if (log.isDebugEnabled()) { log.debug("创建工作表: " + (totalSheets+1));}          
                lastSheet = workbook.createSheet(("工作表" + (totalSheets+1)),totalSheets);  
                // 行数归1，0行为标题头  
                lastRow = 0;  
                // 为新工作表加列标题头  
                Iterator it = mapTitle.keySet().iterator();  
                while (it.hasNext()) {  
                    key = (String) it.next();  
                    value = new String((String) mapTitle.get(key));  
                    col = Integer.valueOf((String) mapColumn.get(key))  
                            .intValue();  
                    label = new Label(col, lastRow, value);  
                    lastSheet.addCell(label);  
                }  
                // 工作表数 增加  
                totalSheets = totalSheets + 1;  
                lastRow++;  
            }  
  
            map = (Map) list.get(i);  
            Iterator it = map.keySet().iterator();  
            while (it.hasNext()) {  
                key = (String) it.next();  
                // log.debug("key="+key);  
                value = new String(map.get(key).toString());  
                // log.debug("mapColumn.get(key)="+mapColumn.get(key));  
                col = Integer.valueOf((String) mapColumn.get(key)).intValue();  
                label = new Label(col, lastRow, value);  
                lastSheet.addCell(label);  
            }  
            lastRow++;  
        }  
  
  
        // 下一次分页请求开始行号位置  
        firstRow = firstRow + length;  
//      if (log.isDebugEnabled()) { log.debug("循环一次，下一次行号："+firstRow);}  
        toClient.flush();  
  
    }  
  
    public void closed() throws IOException, WriteException {  
          
        // 关闭流  
        if  (toClient != null) {  
            toClient.flush();  
            toClient.close();  
        }  
        if (workbook != null) {  
            workbook.write();  
            workbook.close();  
        }  
          
          
    }  
}  