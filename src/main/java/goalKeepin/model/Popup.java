package goalKeepin.model;

import lombok.Data;

@Data
public class Popup {

	private Long popupNo;
	private String popupImgUrl;
	private String popupTypeCd;
	private Long popupTargetNo;
	private String popupTargetNameEn;
	private boolean popupIsOn;
}
