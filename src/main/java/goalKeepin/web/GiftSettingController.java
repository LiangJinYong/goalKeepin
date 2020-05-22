package goalKeepin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.data.GiftSettingMapper;
import goalKeepin.model.GiftSetting;

@Controller
@RequestMapping("/giftSetting")
public class GiftSettingController {

	@Autowired
	private GiftSettingMapper referralCodeMapper;
	
	@GetMapping("/showGiftSettingDetail")
	public String showReferralCodeDetail(Model model) {
		
		GiftSetting giftSetting = referralCodeMapper.selectGiftSettingInfo();
		model.addAttribute("giftSetting", giftSetting);
		
		return "giftSetting/giftSettingDetailForm";
	}
	
	@PostMapping("/saveGiftSetting")
	public String saveGiftSetting(GiftSetting giftSetting) {
		referralCodeMapper.updateGiftSetting(giftSetting);
		return "redirect:/giftSetting/showGiftSettingDetail";
	}
}
