package goalKeepin.web;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import goalKeepin.data.CommonMapper;
import goalKeepin.model.PushRecord;
import goalKeepin.util.FCMUtils;

@Controller
@RequestMapping("/pushNotification")
public class PushNotificationController {
	@Autowired
	private CommonMapper commonMapper;

	@GetMapping("/showPushNotificationDetail")
	public String showPushNotificationDetail() {

		return "pushNotification/pushNotificationDetail";
	}

	@PostMapping("/sendingPushNotification")
	@ResponseBody
	public String sendingPushNotification(@RequestParam("userNos") String userNos,
			@RequestParam("notificationMessage") String notificationMessage) throws JSONException {

		if ("sendToAll".equals(userNos)) {
			List<Integer> userNoList = commonMapper.selectAllUserNos();
			
			for(Integer userNo : userNoList) {
				sendPush(userNo, notificationMessage);
			}
		} else {

			String[] users = userNos.split(",");

			for (String userNoStr : users) {
				int userNo = Integer.parseInt(userNoStr);
				sendPush(userNo, notificationMessage);
			}
		}
		return "200";
	}
	
	private void sendPush(Integer userNo, String notificationMessage) throws JSONException {
		boolean isReceivingPushMessage = commonMapper.selectReceivingChallengePushStatus(userNo);
		if (isReceivingPushMessage) {
			String pushToken = commonMapper.getPushTokenByUserNo(userNo);
			String title = "GoalKeepin";
			String body = notificationMessage;
			String type = "PS01";

			if (pushToken != null) {
				PushRecord pushRecord = new PushRecord(userNo, title, body, type, -1);
				commonMapper.insertSendPushRecord(pushRecord);
				FCMUtils.sendFCM(userNo, pushToken, title, body, type);
			}
		}
	}
}
