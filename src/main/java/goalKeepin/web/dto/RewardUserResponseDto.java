package goalKeepin.web.dto;

import lombok.Data;

@Data
public class RewardUserResponseDto {

	private Long userNo;
	private String userId;
	private Double participationFee;
	private Double completeRate;
	private Double rewardAmount;
	private Boolean hasRedCard;
	private Boolean hasPaidReward;
}
