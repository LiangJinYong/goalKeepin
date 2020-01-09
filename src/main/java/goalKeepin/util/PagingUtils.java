package goalKeepin.util;

import goalKeepin.model.Paging;

public class PagingUtils {

	private PagingUtils() {}
	
	public static Paging getPaging(Integer pageNum, Integer totalRecordCount) {
		
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}

		paging.setCurrentPageNum(pageNum);
		
		paging.setTotalRecords(totalRecordCount);
		
		return paging;
	}
}
