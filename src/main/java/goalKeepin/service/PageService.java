package goalKeepin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import goalKeepin.model.Page;

@Service
public class PageService {

	public Page getPage(int pageNum, List pageData, int totalRecordNum, int pageSize) {
		
		Page page = new Page();
		int totalPageNum = (totalRecordNum + (pageSize - 1)) / pageSize;
		
		int firstPageNum = pageNum - 4;
		int lastPageNum = pageNum + 5;
		
		if(pageNum < 5) {
			firstPageNum = 1;
			lastPageNum = 10;
		}
		
		if (pageNum + 5 > totalPageNum) {
			lastPageNum = totalPageNum;
			firstPageNum = totalPageNum - 9;
		}
		
		if (totalPageNum < 10) {
			firstPageNum = 1;
			lastPageNum = totalPageNum;
		}
		
		page.setPageData(pageData);
		page.setPageNum(pageNum);
		page.setTotalPageNum(totalPageNum);
		page.setTotalRecordNum(totalRecordNum);
		page.setPageSize(pageSize);
		page.setFirstPageNum(firstPageNum);
		page.setLastPageNum(lastPageNum);
		
		return page;
	}
}
