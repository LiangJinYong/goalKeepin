package goalKeepin.web.dto;

import java.util.List;
import java.util.Map;

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
	
	private List<Map<String, Object>> recentOngoingProjectList;
	private List<Map<String, Object>> todayApprovalList;
	private List<Map<String, Object>> unpaidRewardList;
	
}
