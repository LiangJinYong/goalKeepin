package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Banner;
import goalKeepin.model.Popup;

@Mapper
public interface BannerMapper {

	List<Popup> selectBannerList(Map<String, Object> paramMap);

	int getTotalBannerCount();

	void insertNewBanner(Banner banner);

	void updateBanner(Banner banner);

	void deleteBanner(Long bannerNo);

	void updateBannerActiveStatus(Map<String, Object> banner);

	Banner selectBannerDetail(Long bannerNo);

	Integer selectMainBannerCount();

}
