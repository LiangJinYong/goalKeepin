package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class Notice {

	private Long noticeNo;
	
	private Long noticeTitleTransNo;
	private String noticeTitleEn;
	private String noticeTitleTc;
	private String noticeTitleSc;
	
	private Long noticeContentTransNo;
	private String noticeContentEn;
	private String noticeContentTc;
	private String noticeContentSc;
	
	private Long noticeImgUrlTransNo;
	private String noticeImgUrlEn;
	private String noticeImgUrlTc;
	private String noticeImgUrlSc;
	
	private int noticeIsMain;
	private Date noticeRegDate;
}
