package goalKeepin.model;

import lombok.Data;

@Data
public class Banner {

	private Long bannerNo;
	private String bannerImgUrl;
	private String bannerTypeCd;
	private String bannerTarget;
	private String bannerTargetNameEn;
	private boolean bannerIsOn;
	private boolean bannerIsMain;
}
