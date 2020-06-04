package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class BaseChallenge {

	private Long baseNo;
	
	private Long baseNmTransNo;
	
	private String baseNmEn;
	
	private String baseNmTc;
	
	private String baseNmSc;
	
	private String baseThumbnailUrl;
	
	private String baseHabitTypeCd;
	
	private String baseAuthDateCd;
	
	private Integer baseAuthFrequency;
	
	private String baseAuthFromTime;
	
	private String baseAuthToTime;
	
	private Integer baseAuthNumDaily;
	
	private String baseAuthMethodCd;
	
	private Integer baseAuthIsOpen;
	
	private Integer baseAuthInterval;
	
	private Integer baseAuthIsAlbum;
	
	private Long baseAuthDescTransNo;
	
	private String baseAuthDescEn;
	
	private String baseAuthDescTc;
	
	private String baseAuthDescSc;
	
	private Long baseDetailTransNo;
	
	private String baseDetailEn;
	
	private String baseDetailTc;
	
	private String baseDetailSc;
	
	private Date baseRegDate;
	
	private String searchKeyword;
	
	private boolean baseIsOn;
}
