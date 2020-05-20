package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.web.dto.CompletedChallengeResponseDto;
import goalKeepin.web.dto.RewardUserResponseDto;


@Mapper
public interface RewardPaymentMapper {


	List<Map<String, String>> selectUserListById(Map<String, Object> paramMap);

	int getTotalUserCount(Map<String, Object> paramMap);

	List<CompletedChallengeResponseDto> selectCompletedChallengeList(Map<String, Object> paramMap);

	int getTotalCompletedChallengeNum(Map<String, Object> paramMap);

	List<Map<String, Object>> selectRewardUserList(Map<String, Object> paramMap);

	int getTotalRewardUserNum(Map<String, Object> paramMap);

	void updateRewardUser(Map<String, Object> paramMap);

	void insertUserCashReport(Map<String, Object> paramMap);

	List<CompletedChallengeResponseDto> selectCompletedChallengeListForDownload();

	List<RewardUserResponseDto> selectRewardUserListForDownload(Long challengeNo);
}
