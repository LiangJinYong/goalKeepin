package goalKeepin.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.data.BadgeGiftMapper;

@Controller
@RequestMapping("/badgeGift")
public class BadgeGiftController {
	
	@Autowired
	private BadgeGiftMapper badgeGiftMapper;

	@GetMapping("/showBadgeGiftDetail")
	public String showPushNotificationDetail() {

		return "badgeGift/badgeGiftDetail";
	}

	@PostMapping("/sendingBadgeGift")
	@ResponseBody
	public String sendingBadgeGift(@RequestParam("userNos") String userNos) {
		String[] userNoArr = userNos.split(",");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userNoArr", userNoArr);
		
		try {
			Double badgeValue = badgeGiftMapper.selectBadgeValue();
			paramMap.put("badgeValue", badgeValue);
			
			badgeGiftMapper.insertBadgeGiftRecords(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		
		return "200";
	}
}
