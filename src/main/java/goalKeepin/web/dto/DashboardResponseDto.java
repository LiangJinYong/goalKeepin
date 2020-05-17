package goalKeepin.web.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DashboardResponseDto {

	private final Integer totalUserCount;
	private final Integer totalParicipantCount;
	private final Double totalFeeAmount;
	private final Double totalRewardAmount;
	private final Double totalPaymentAmount;
	private final Double totalCommissionAmount;
}
