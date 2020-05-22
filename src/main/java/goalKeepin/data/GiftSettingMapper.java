package goalKeepin.data;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.GiftSetting;

@Mapper
public interface GiftSettingMapper {

	GiftSetting selectGiftSettingInfo();

	void updateGiftSetting(GiftSetting giftSetting);

}
