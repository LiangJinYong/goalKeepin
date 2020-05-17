package goalKeepin.data;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {

	Integer selectTotalUserCount();

	Integer selectTotalParicipantCount();

	Double selectTotalFeeAmount();

	Double selectTotalRewardAmount();

	Double selectTotalPaymentAmount();

	Double selectTotalCommissionAmount();

}
