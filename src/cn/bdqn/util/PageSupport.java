package cn.bdqn.util;

import java.util.List;

/**
 * 分页工具类
 * @author 
 *
 */
public class PageSupport<T> {
	//当前页码-来自于用户输入
	private int currentPageNo;
	
	//总数量（表）
	private int totalCount;
	
	//页面容量
	private int pageSize;
	
	//总页数-totalCount/pageSize（+1）
	private int totalPageCount;
	
	//查询的集合
	private List<T> list;

	public int getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(int currentPageNo) {
		if(currentPageNo<=0){
			this.currentPageNo =1;
		}else if(currentPageNo>totalPageCount){
			this.currentPageNo = totalPageCount;
		}else{
			this.currentPageNo = currentPageNo;
		}
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		if(totalCount<0){
			this.totalCount = 0;
		}else{
			this.totalCount =totalCount;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if(pageSize<=0){
			this.pageSize = 10;
		}else{
			this.pageSize =pageSize;
		}
		setTotalPageCount();
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount() {
		this.totalPageCount = totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	public int getStartRow(){
		//1---10	  0
		//2---10     10
		//3---10     20
		//4---10	 30
		return (currentPageNo-1)*pageSize;
	}
}