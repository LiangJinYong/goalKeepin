package goalKeepin.model;

import java.util.Date;

import lombok.Data;

@Data
public class OperatedChallenge {

	private Long operatedChallengeNo;

	private Long baseChallengeNo;
	
	private Long categoryNo;

	private String gradeCd;

	private String minFee;

	private String maxFee;

	private String startDate;
	
	private String endDate;

	private String statusCd;
	
	private Long entryNum;
	
	private String totalFee;
	
	private Date regDate;
	
	private int maxResult;
	
	private Category category;
	
	private BaseChallenge baseChallenge;
	
	private String baseAuthMethodCd;
	private Integer baseAuthFrequency;
	private Integer baseAuthNumDaily;
}
   