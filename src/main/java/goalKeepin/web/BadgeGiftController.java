package goalKeepin.web;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.data.BadgeGiftMapper;
import goalKeepin.data.CommonMapper;
import goalKeepin.model.PushRecord;
import goalKeepin.util.FCMUtils;

@Controller
@RequestMapping("/badgeGift")
public class BadgeGiftController {
	
	@Autowired
	private BadgeGiftMapper badgeGiftMapper;
	
	@Autowired
	private CommonMapper commonMapper;

	@GetMapping("/showBadgeGiftDetail")
	public String showPushNotificationDetail() {

		return "badgeGift/badgeGiftDetail";
	}

	@PostMapping("/sendingBadgeGift")
	@ResponseBody
	public String sendingBadgeGift(@RequestParam("userNos") String userNos) throws JSONException {
		String[] userNoArr = userNos.split(",");
		Map<String, Object> paramMap = new HashMap<>();
		
		for(String userNoStr : userNoArr) {
			paramMap.put("userNo", userNoStr);
			Double badgeValue = badgeGiftMapper.selectBadgeValue();
			paramMap.put("badgeValue", badgeValue);
			
			badgeGiftMapper.insertBadgeGiftRecords(paramMap);
			
			// push
			int userNo = Integer.parseInt(userNoStr);
			boolean isReceivingPushMessage = commonMapper.selectReceivingRelationPushStatus(userNo);

			if (isReceivingPushMessage) {
				String pushToken = commonMapper.getPushTokenByUserNo(userNo);
				String languageCode = commonMapper.getLastLanguageForUser(userNo);
				
				Map<String, String> param = new HashMap<>();
				param.put("mainCode", "P105");
				param.put("detailCode", "S001");
				param.put("languageCode", languageCode);
				
				String title = commonMapper.getContentForPushMessage(param);
				param.put("detailCode", "S002");
				String body = commonMapper.getContentForPushMessage(param);
				String type = "PS05";
				
				if (pushToken != null) {
					FCMUtils.sendFCM(userNo, pushToken, title, body, type);
					PushRecord pushRecord = new PushRecord(userNo, title, body, type, -1);
					commonMapper.insertSendPushRecord(pushRecord);
				}
			}
		}
		
		return "200";
	}
}
