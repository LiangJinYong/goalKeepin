package goalKeepin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.data.UserMapper;

@Controller
@RequestMapping("/rewardPayment")
public class RewardPaymentController {

	@Autowired
	private UserMapper userMapper;
	
	@GetMapping("/showRewardPaymentDetail")
	public String showRewardPaymentPage() {
		
		return "rewardPayment/rewardPaymentDetail";
	}
}
