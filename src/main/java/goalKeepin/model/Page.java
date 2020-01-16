package goalKeepin.model;

import lombok.Data;
import java.util.List;

@Data
public class Page {

	private List pageData;
	private int pageNum;
	private int totalRecordNum;
	private int totalPageNum;
	private int pageSize;
	private int firstPageNum;
	private int lastPageNum;
}
