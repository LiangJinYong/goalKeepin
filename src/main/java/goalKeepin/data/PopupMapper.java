package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Popup;

@Mapper
public interface PopupMapper {

	int getTotalPopupCount();

	List<Popup> selectPopupList(Map<String, Object> paramMap);

}
