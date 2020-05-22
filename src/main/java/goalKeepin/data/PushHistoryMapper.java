package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.web.dto.PushHistoryResponseDto;

@Mapper
public interface PushHistoryMapper {

	List<PushHistoryResponseDto> selectPushHistoryList(Map<String, Object> paramMap);

	int getTotalPushHistoryCount();

}
