package goalKeepin.web.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BalanceResponseDto {
	private final Double totalFeeAmount;
	private final Double totalRewardAmount;
	private final Double totalServiceCharge;
	private final Double totalPaymentAmount;
	private final Double totalCommissionAmount;
	private final Double totalPenaltyAmount;
}
