package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.constants.LoginType;
import goalKeepin.data.RewardPaymentMapper;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;

@Controller
@RequestMapping("/rewardPayment")
public class RewardPaymentController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private RewardPaymentMapper rewardPaymentMapper;
	
	@GetMapping("/showRewardPaymentDetail")
	public String showRewardPaymentPage(Model model) {
		
		return "rewardPayment/rewardPaymentDetail";
	}
	
	@GetMapping("/searchUserList/{pageNum}")
	@ResponseBody
	public String searchUserList(@PathVariable("pageNum") Integer pageNum, @RequestParam("userId") String userId, @RequestParam(value="sort", required=false) String sort) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		paramMap.put("userId", userId);
		
		if (sort != null && !"".equals(sort)) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			result.put("sortField", sortField);
			result.put("sortOrder", sortOrder);
			
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		List<Map<String, String>> pageData = rewardPaymentMapper.selectUserListById(paramMap);
		int totalRecordNum = rewardPaymentMapper.getTotalUserCount(paramMap);
		for(Map<String, String> user : pageData) {
			String loginTypeCd = user.get("loginTypeCd");
			String loginTypeName = LoginType.valueOf(loginTypeCd).getLoginTypeName();
			user.put("loginTypeCd", loginTypeName);
		}
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		result.put("page", page);
		
		
		return gson.toJson(result);
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
