package goalKeepin.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
public class BaseChallenge {

	private Long baseNo;
	
	private Long baseNmTransNo;
	
	@NotEmpty(message="Required")
	private String baseNmEn;
	
	@NotEmpty(message="Required")
	private String baseNmTc;
	
	@NotEmpty(message="Required")
	private String baseNmSc;
	
//	@NotEmpty(message="Required")
//	private byte[] baseThumbnailUrl;
	
	@NotEmpty(message="Required")
	private String baseHabitTypeCd;
	
	@NotEmpty(message="Required")
	private String baseAuthDateCd;
	
	@NotNull(message="Required")
	@Min(value=1, message="must be equal or greater than 1")  
    @Max(value=7, message="must be equal or less than 7")
	private Integer baseAuthFrequency;
	
	@NotEmpty(message="Required")
	private String baseAuthFromTime;
	
	@NotEmpty(message="Required")
	private String baseAuthToTime;
	
	@NotNull(message="Required")
	@Min(value=1, message="must be equal or greater than 1")  
	private Integer baseAuthNumDaily;
	
	@NotEmpty(message="Required")
	private String baseAuthMethodCd;
	
	@NotNull(message="Required")
	private Integer baseAuthIsOpen;
	
	@NotNull(message="Required")
	@Min(value=1, message="must be equal or greater than 1")  
    @Max(value=23, message="must be equal or less than 23")
	private Integer baseAuthInterval;
	
	@NotNull(message="Required")
	private Integer baseAuthIsAlbum;
	
	private Long baseAuthDescTransNo;
	
	@NotEmpty(message="Required")
	private String baseAuthDescEn;
	
	@NotEmpty(message="Required")
	private String baseAuthDescTc;
	
	@NotEmpty(message="Required")
	private String baseAuthDescSc;
	
	private Long baseDetailTransNo;
	
	@NotEmpty(message="Required")
	private String baseDetailEn;
	
	@NotEmpty(message="Required")
	private String baseDetailTc;
	
	@NotEmpty(message="Required")
	private String baseDetailSc;
	
	private Date baseRegDate;
	
	@NotEmpty(message="Required")
	private String searchKeyword;
}
