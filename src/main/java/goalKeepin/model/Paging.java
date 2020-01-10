package goalKeepin.model;

import lombok.Data;

@Data
public class Paging {
	
	private int currentPageNum;
	private int totalPages;
	private int totalRecords;
	private int pageSize = 10;
	private int prevPageNum;
	private int nextPageNum;
	
	public int getTotalPages() {
		totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : totalRecords / pageSize + 1;
		return totalPages;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : totalRecords / pageSize + 1;
	}
	
	public int getPrevPageNum() {
		if (currentPageNum > 1) {
			prevPageNum = currentPageNum - 1;
		} else {
			prevPageNum = currentPageNum;
		}
		
		return prevPageNum;
	}
	
	public int getNextPageNum() {
		if(currentPageNum < totalPages) {
			nextPageNum = currentPageNum + 1;
		} else {
			nextPageNum = currentPageNum;
		}
		
		return nextPageNum;
	}
}
