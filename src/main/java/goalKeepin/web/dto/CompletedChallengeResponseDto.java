package goalKeepin.web.dto;

import lombok.Data;

@Data
public class CompletedChallengeResponseDto {

	private Long challengeNo;
	private String challengeName;
	private Double totalParticipationFee;
	private Double refundRate;
	private Double totalRefundAmount;
	private Integer paidRewardNumber;
	private Integer unpaidRewardNumber;
}
