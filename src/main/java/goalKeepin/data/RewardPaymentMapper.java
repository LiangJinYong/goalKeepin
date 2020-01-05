package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RewardPaymentMapper {

	List<Map<String, String>> selectUserListById(String userId);

	void insertRewardPaymentRecords(Map<String, Object> paramMap);

}
