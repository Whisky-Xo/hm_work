package com.crm.common.util.page;

import java.io.Serializable;
import java.util.List;


/**
 * 封装分页结果
 * 
 */
public class PageResult<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 总记录数
	 */
	private int rowCount;

	/**
	 * 某一页的记录
	 */
	private List<T> rows;

	/**
	 * 总的页数
	 */
	private int pageSize;

	/**
	 * 每页的记录数
	 */
	private int rowsPerPage = PaginationQuery.DEFAULT_ROWS_PER_PAGE;

	/**
	 * 当前显示页数
	 */
	private int curPageNum = 1;

	/**
	 * 设置url查询参数
	 */
	private String queryParameters;
	
	public PageResult(){}
	
	public PageResult(List<T> rows,int rowCount,PaginationQuery query){
		this.rows = rows;
		this.rowCount = rowCount;
		this.rowsPerPage = query.getRowsPerPage();
		this.curPageNum = query.getPageIndex();
		this.queryParameters = query.getQueryParameters();
		this.countPageSize();
	}
	
	/**
	 * 得到分页器，通过Paginator可以得到总页数等值
	 * @return
	 */
	public Paginator getPaginator() {
		return new Paginator(curPageNum,rowsPerPage,rowCount);
	}

	public int getCurPageNum() {
		return curPageNum;
	}

	public void setCurPageNum(int curPageNum) {
		this.curPageNum = curPageNum;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		this.countPageSize();
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	private void countPageSize() {
		if (this.rowCount % this.rowsPerPage == 0) {
			this.pageSize = this.rowCount / this.rowsPerPage;
		} else {
			this.pageSize = this.rowCount / this.rowsPerPage + 1;
		}

		if (curPageNum > pageSize) {
			this.curPageNum = pageSize;
		}

		if (this.curPageNum < 1) {
			this.curPageNum = 1;
		}

	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public String getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(String queryParameters) {
		this.queryParameters = queryParameters;
	}

}
