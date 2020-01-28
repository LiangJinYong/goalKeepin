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

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.InquiryMapper;
import goalKeepin.model.Inquiry;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.FCMUtils;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private InquiryMapper inquiryMapper;

	@GetMapping("/showInquiryList/{pageNum}")
	public String showInquiryList(@RequestParam("inquiryStatusCd") String inquiryStatusCd,
			@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		int pageSize = props.getPageSize();
		
		Map<String, Object> paramMap = new HashMap<>();
		
		if (inquiryStatusCd == null) {
			inquiryStatusCd = "IN00";
		}
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		paramMap.put("inquiryStatusCd", inquiryStatusCd);
		
		if (sort != null) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		List<Inquiry> pageData = inquiryMapper.selectInquiryList(paramMap);
		int totalRecordNum = inquiryMapper.getTotalInquiryCount(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
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
