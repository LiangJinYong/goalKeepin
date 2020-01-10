package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Popup;

@Mapper
public interface PopupMapper {

	int getTotalPopupCount();

	List<Popup> selectPopupList(Map<String, Object> paramMap);

	List<Map<String, String>> selectNoticeList(Map<String, Object> paramMap);

	List<Map<String, String>> selectChallengeList(Map<String, Object> paramMap);

	int getTotalNoticeCount();

	int getTotalChallengeCount();


}
