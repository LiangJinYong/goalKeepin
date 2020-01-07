package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class Inquiry {

	private Long inquiryNo;
	private User inquiryUser;
	private String inquiryTitle;
	private String inquiryContentShort;
	private String inquiryContentFull;
	private Date inquiryRegDate;
	private String inquiryReplyCotent;
	private Date inquiryReplyRegDate;
	private String inquiryStatusCd;
}
