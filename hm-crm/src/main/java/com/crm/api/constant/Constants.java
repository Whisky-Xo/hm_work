package com.crm.api.constant;

/**
 * 接口访问常量类
 * 
 * @author JingChenglong 2016-07-08 09:29
 *
 */
public class Constants {

	public static final String RESOURCEPATH = new Object() {
		public String getResourcePath() {
			return this.getClass().getClassLoader().getResource("").getPath();
		}
	}.getResourcePath();

	/*-- 编码方式 --*/
	public static final String ENCODING_GBK = "GBK";
	public static final String ENCODING_UTF8 = "UTF-8";
	public static final String ENCODING_ISO88591 = "ISO-8859-1";
	public static final String CHARSETNAME_DEFAULT = ENCODING_UTF8;

	/*-- 未知错误 --*/
	public static final String UNKNOWN = "unknown";

	/*-- 字节长度 --*/
	public static final long BYTE_HEX = 1024L;
	public static final long MBYTE = 1048576L;
	public static final long GBYTE = 1073741824L;

	/*-- 分页信息 --*/
	public static final int DEFAULT_PAGESIZE = 10;
	public static final int MAX_PAGESIZE = 1000;
	public static final int DEFAULT_CURRENTPAGE = 1;
	public static final int MAX_PAGE = 10000000;

	/*-- 文件分隔符 --*/
	public static final String FILE_SEPARATOR = "/";
	/*-- 多个参数字符串分隔符 --*/
	public static final String STR_SEPARATOR = ",";

	public static final String ROWTYPE_STATIC = "{static}";
	public static final String ROWTYPE_CLOBTOMAP = "{clobtomap}";

	/*-- 查询排序 --*/
	public static final String ORDERSORT_ASC = "ASC";
	public static final String ORDERSORT_DESC = "DESC";

	// 签名类型
	public static final String SIGNTYPE_MD5 = "1";// md5签名
	public static final String SIGNTYPE_SHA1 = "2";// hmacsh1 签名

	// 企业是否开启提醒
	public static final String COMPANY_TIP_YES = "1";// 开启

	// 常用企业ID
	public static final int COMP_ID_COMMON = 1;// 通用
	public static final int COMP_ID_JINFUREN = 3;// 金夫人

	public static final int WEDDING_DAY_DEFAULT = 180;// 默认婚期临近查询时间
}