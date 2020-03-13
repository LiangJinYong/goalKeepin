package goalKeepin.data;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BadgeGiftMapper {

	Double selectBadgeValue();

	void insertBadgeGiftRecords(Map<String, Object> paramMap);

}
