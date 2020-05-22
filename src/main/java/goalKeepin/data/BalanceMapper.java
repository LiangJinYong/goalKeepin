package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BalanceMapper {

	Double selectTotalServiceCharge();

	Double selectTotalPenaltyAmount();

	List<Map<String, String>> selectUserListByIdAndNickname(Map<String, Object> paramMap);

	int getTotalUserCount(Map<String, Object> paramMap);

	List<Map<String, String>> selectUserListForDownload();

}
