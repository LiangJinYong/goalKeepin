package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class Coupon {
	private Long couponNo;
	private String couponCode;
	private String couponType;
	private String couponName;
	private Integer couponGiftAmount;
	private Integer couponUseNumber;
	private Integer couponMaxUse;
	private Date couponExpiredDate;
	private boolean couponIsOn;
	private Date couponRegDate;
}
