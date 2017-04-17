package com.crm.common.util.page;

/**
 * 分页器，根据currPage,pageSize,totalItem用于页面上分页显示多项内容，
 * 计算页码和当前页的偏移量，方便页面分页使用.
 */
public class Paginator implements java.io.Serializable {
	
	private static final long serialVersionUID = -1190191705268192699L;

	private static final int DEFAULT_SLIDERS_COUNT = 9;
	
    /** 分页大小 */
    private int               pageSize;
    /** 当前页码  */
    private int               currPage;
    /** 总记录数 */
    private int               totalItems;
    
    public Paginator() {}
	
	public Paginator(int currPage, int pageSize, int totalItems) {
		super();
		this.pageSize = pageSize;
		this.totalItems = totalItems;
		this.currPage = computePageNo(currPage);
	}
	
    /**
     * 取得当前页。
     */
	public int getCurrPage() {
		return currPage;
	}

	public int getPageSize() {
		return pageSize;
	}

    /**
     * 取得总项数。
     *
     * @return 总项数
     */
	public int getTotalItems() {
		return totalItems;
	}

    /**
     * 是否是首页（第一页），第一页页码为1
     *
     * @return 首页标识
     */
	public boolean isFirstPage() {
		return currPage <= 1;
	}

    /**
     * 是否是最后一页
     *
     * @return 末页标识
     */
	public boolean isLastPage() {
		return currPage >= getTotalPages();
	}
	
	public int getPrePage() {
		if (getIsHasPrePage()) {
			return currPage - 1;
		} else {
			return currPage;
		}
	}
	
	public int getNextPage() {
		if (getIsHasNextPage()) {
			return currPage + 1;
		} else {
			return currPage;
		}
	}
	
    /**
     * 判断指定页码是否被禁止，也就是说指定页码超出了范围或等于当前页码。
     *
     * @param page 页码
     *
     * @return boolean  是否为禁止的页码
     */
    public boolean isDisabledPage(int currPage) {
        return ((currPage < 1) || (currPage > getTotalPages()) || (currPage == this.currPage));
    }
    
    /**
     * 是否有上一页
     *
     * @return 上一页标识
     */
	public boolean getIsHasPrePage() {
		return (currPage - 1 >= 1);
	}	
    /**
     * 是否有下一页
     *
     * @return 下一页标识
     */
	public boolean getIsHasNextPage() {
		return (currPage + 1 <= getTotalPages());
	}
	
	
	/**
     * limit，可以用于mysql分页使用(0-based)
     **/
    public int getLimit() {
        if (currPage > 0) {
            return Math.min(pageSize * currPage, getTotalItems()) - (pageSize * (currPage - 1));
        } else {
            return 0;
        }
    }
	/**
	 * 得到 总页数
	 * @return
	 */
	public int getTotalPages() {
		if (totalItems <= 0) {
			return 0;
		}
		if (pageSize <= 0) {
			return 0;
		}

		int count = totalItems / pageSize;
		if (totalItems % pageSize > 0) {
			count++;
		}
		return count;
	}

    protected int computePageNo(int currPage) {
        return computePageNumber(currPage,pageSize,totalItems);
    }
    /**
     * 页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。
     * @return
     */
    public Integer[] getSlider() {
    	return slider(DEFAULT_SLIDERS_COUNT);
    }
    /**
     * 页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。
     * @return
     */
    public Integer[] slider(int slidersCount) {
    	return generateLinkPageNumbers(getCurrPage(),(int)getTotalPages(), slidersCount);
    }
    
    private static int computeLastPageNumber(int totalItems,int pageSize) {
    	if(pageSize <= 0) return 1;
        int result = (int)(totalItems % pageSize == 0 ? 
                totalItems / pageSize 
                : totalItems / pageSize + 1);
        if(result <= 1)
            result = 1;
        return result;
    }
    
    private static int computePageNumber(int page, int pageSize,int totalItems) {
        if(page <= 1) {
            return 1;
        }
        if (Integer.MAX_VALUE == page
                || page > computeLastPageNumber(totalItems,pageSize)) { //last page
            return computeLastPageNumber(totalItems,pageSize);
        }
        return page;
    }
    
    private static Integer[] generateLinkPageNumbers(int currentPageNumber,int lastPageNumber,int count) {
        int avg = count / 2;
        
        int startPageNumber = currentPageNumber - avg;
        if(startPageNumber <= 0) {
            startPageNumber = 1;
        }
        
        int endPageNumber = startPageNumber + count - 1;
        if(endPageNumber > lastPageNumber) {
            endPageNumber = lastPageNumber;
        }
        
        if(endPageNumber - startPageNumber < count) {
            startPageNumber = endPageNumber - count;
            if(startPageNumber <= 0 ) {
                startPageNumber = 1;
            }
        }
        
        java.util.List<Integer> result = new java.util.ArrayList<Integer>();
        if(startPageNumber>2){
    		result.add(1);
    		result.add(0);
    	}
        for(int i = startPageNumber; i <= endPageNumber; i++) {
            result.add(new Integer(i));
        }
        if(endPageNumber!=lastPageNumber){
        	result.add(0);
            result.add(lastPageNumber);
        }
        return result.toArray(new Integer[result.size()]);
    }
    
    public String toString() {
        return "currPage:"+currPage+" pageSize:"+pageSize+" totalItems:"+totalItems;
    }
    
}
