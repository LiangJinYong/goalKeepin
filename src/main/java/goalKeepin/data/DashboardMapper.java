package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {

	Integer selectTotalUserCount();

	Integer selectTotalParicipantCount();

	Double selectTotalFeeAmount();

	Double selectTotalRewardAmount();

	Double selectTotalPaymentAmount();

	Double selectTotalCommissionAmount();

	List<Map<String, Object>> selectRecentOngongProjectList();

	List<Map<String, Object>> selectTodayApprovalList();

	List<Map<String, Object>> selectUnpaidRewardList();

}
