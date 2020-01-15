package goalKeepin.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import goalKeepin.data.RewardPaymentMapper;

@Controller
@RequestMapping("/rewardPayment")
public class RewardPaymentController {

	@Autowired
	private RewardPaymentMapper rewardPaymentMapper;
	
	@GetMapping("/showRewardPaymentDetail")
	public String showRewardPaymentPage() {
		
		return "rewardPayment/rewardPaymentDetail";
	}
	
	@GetMapping("/searchUserList")
	@ResponseBody
	public String searchUserList(@RequestParam("userId") String userId) {
		Gson gson = new Gson();
		List<Map<String, String>> userList = rewardPaymentMapper.selectUserListById(userId);
		for(Map<String, String> user : userList) {
			String loginTypeCd = user.get("loginTypeCd");
			String loginTypeName = LoginType.valueOf(loginTypeCd).getLoginTypeName();
			user.put("loginTypeCd", loginTypeName);
		}
		return gson.toJson(userList);
	}
	
	@PostMapping("/processRewardPayment")
	@ResponseBody
	public String processRewardPayment(@RequestParam("userNos") String userNos, @RequestParam("rewardAmount") Double rewardAmount) {

		String[] userNoArr = userNos.split(",");
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userNoArr", userNoArr);
		paramMap.put("rewardAmount", rewardAmount);
		
		try {
			rewardPaymentMapper.insertRewardPaymentRecords(paramMap);
		} catch(Exception e) {
			e.printStackTrace();
			return "500";
		}
		
		return "200";
	}
}
