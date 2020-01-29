package goalKeepin.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import goalKeepin.constants.LoginType;
import goalKeepin.data.InquiryMapper;
import goalKeepin.data.RewardPaymentMapper;
import goalKeepin.util.FCMUtils;

@Controller
@RequestMapping("/pushNotification")
public class PushNotificationController {

	@Autowired
	private RewardPaymentMapper rewardPaymentMapper;
	
	@Autowired
	private InquiryMapper inquiryMapper;
	
	@GetMapping("/showPushNotificationDetail")
	public String showPushNotificationDetail() {
		
		return "pushNotification/pushNotificationDetail";
	}
	
	@GetMapping("/searchUserList")
	@ResponseBody
	public String searchUserList(@RequestParam("userId") String userId) {
		Gson gson = new Gson();
		/*
		List<Map<String, String>> userList = rewardPaymentMapper.selectUserListById(userId);
		for(Map<String, String> user : userList) {
			String loginTypeCd = user.get("loginTypeCd");
			String loginTypeName = LoginType.valueOf(loginTypeCd).getLoginTypeName();
			user.put("loginTypeCd", loginTypeName);
		}
		return gson.toJson(userList);
		*/
		return null;
	}
	
	@PostMapping("/sendingPushNotification")
	@ResponseBody
	public String sendingPushNotification(@RequestParam("userNos") String userNos, @RequestParam("notificationMessage") String notificationMessage) {
		
		String[] users = userNos.split(",");
		
		for(String userNo : users) {
			String pushToken = inquiryMapper.getPushTokenByUserNo(Long.parseLong(userNo));
			String title = "[GoalKeepin]";
			String body = notificationMessage;
			String type = "PS06";
			
			if(pushToken != null) {
				try {
					FCMUtils.sendFCM(Long.parseLong(userNo), pushToken, title, body, type);
				} catch (Exception e) {
					e.printStackTrace();
					return "500";
				}
			}
		}
		return "200";
	}
	
	@PostMapping("/sendingPushNotificationToAll")
	@ResponseBody
	public String sendingPushNotificationToAll(@RequestParam("notificationMessage") String notificationMessage) {
		String title = "[GoalKeepin]";
		String body = notificationMessage;
		String type = "PT01";
		
		try {
			FCMUtils.sendFCMAll(title, body, type);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		
		return "200";
	}
}
