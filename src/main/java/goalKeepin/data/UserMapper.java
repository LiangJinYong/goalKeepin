package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.web.dto.UserDetailDto;

@Mapper
public interface UserMapper {

	int getTotalUserCount(Map<String, Object> paramMap);

	List<Map<String, String>> selectUserListByIdAndNickname(Map<String, Object> paramMap);

	List<Map<String, String>> selectUserListForDownload(String nationalityCd);

	UserDetailDto selectUserDetail(Long userNo);

	int selectYellowCardNumber(Long userNo);

	void increaseYellowCardNumber(Long userNo);

	void increaseRedCardNumber(Long userNo);

	int selectRedCardNumber(Long userNo);

	void updateRedCardExpiredDate(Map<String, Object> param);

	void clearToken(Long userNo);

	List<Map<String, Object>> selectCashReportList(Map<String, Object> param);
}
