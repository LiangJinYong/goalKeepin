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

import goalKeepin.data.InquiryMapper;
import goalKeepin.model.Inquiry;
import goalKeepin.model.Paging;
import goalKeepin.util.FCMUtils;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

	@Autowired
	private InquiryMapper inquiryMapper;

	@GetMapping("/showInquiryList/{pageNum}")
	public String showInquiryList(@RequestParam("inquiryStatusCd") String inquiryStatusCd,
			@PathVariable("pageNum") Integer pageNum, Model model) {
		Map<String, Object> paramMap = new HashMap<>();

		if (inquiryStatusCd == null) {
			inquiryStatusCd = "IN00";
		}
		paramMap.put("inquiryStatusCd", inquiryStatusCd);

		int totalInquiryCount = inquiryMapper.getTotalInquiryCount(paramMap);
		Paging paging = PagingUtils.getPaging(pageNum, totalInquiryCount);

		int startIndex = (pageNum - 1) * 10;
		paramMap.put("startIndex", startIndex);

		List<Inquiry> inquiryList = inquiryMapper.selectInquiryList(paramMap);
		model.addAttribute("inquiryList", inquiryList);
		model.addAttribute("paging", paging);
		model.addAttribute("inquiryStatusCd", inquiryStatusCd);
		return "inquiry/inquiryList";
	}
	
	@GetMapping("/showInquiryDetail")
	public String showInquiryDetail(@RequestParam("inquiryNo") Long inquiryNo, Model model) {
		Inquiry inquiry = inquiryMapper.selectInquiryDetail(inquiryNo);
		model.addAttribute("inquiry", inquiry);
		return "inquiry/inquiryDetail";
	}
	
	@PostMapping("/processInquiryReply")
	public String processInquiryReply(Inquiry inquiry) {
		try {
			
			inquiryMapper.updateInquiryStatus(inquiry);
			
			long userNo = inquiry.getInquiryUser().getUserNo();
			
			String pushToken = inquiryMapper.getPushTokenByUserNo(userNo);
			String title = "Inquiry Answer";
			String body = inquiry.getInquiryReplyCotent();
			String type = "PS01";
			
			if(pushToken != null) {
				FCMUtils.sendFCM(userNo, pushToken, title, body, type);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/inquiry/showInquiryList/1?inquiryStatusCd=IN00";
	}

}
