package goalKeepin.model;

import lombok.Data;
import java.util.Date;

@Data
public class Offer {

	private Long offerNo;
	private String offerTitle;
	private String offerAuthInfo;

	private Long offerTitleTransNo;
	private String offerTitleEn;
	private String offerTitleTc;
	private String offerTitleSc;

	private Long offerAuthInfoTransNo;
	private String offerAuthInfoEn;
	private String offerAuthInfoTc;
	private String offerAuthInfoSc;

	private String offerStatusCd;
	private Date offerRegDate;
	private String offerStartDate;
	private Long offerLikeNum;

	private User user;
	
}
