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
import goalKeepin.data.CommonMapper;
import goalKeepin.data.InquiryMapper;
import goalKeepin.model.Inquiry;
import goalKeepin.model.Page;
import goalKeepin.model.PushRecord;
import goalKeepin.service.PageService;
import goalKeepin.util.FCMUtils;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private InquiryMapper inquiryMapper;
	
	@Autowired
	private CommonMapper commonMapper;

	@GetMapping("/showInquiryList/{pageNum}")
	public String showInquiryList(@RequestParam(name = "inquiryStatusCd", required = false) String inquiryStatusCd,
			@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {
		int pageSize = props.getPageSize();

		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		if (inquiryStatusCd == null) {
			inquiryStatusCd = "IN00";
		}
		paramMap.put("inquiryStatusCd", inquiryStatusCd);

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

			// push
			int userNo = inquiry.getInquiryUser().getUserNo().intValue();
			boolean isReceivingPushMessage = commonMapper.selectReceivingRelationPushStatus(userNo);

			if (isReceivingPushMessage) {
				String pushToken = commonMapper.getPushTokenByUserNo(userNo);
				String languageCode = commonMapper.getLastLanguageForUser(userNo);
				
				Map<String, String> param = new HashMap<>();
				param.put("mainCode", "P104");
				param.put("detailCode", "S001");
				param.put("languageCode", languageCode);
				
				String title = commonMapper.getContentForPushMessage(param); //"Inquiry Answer";
				String body = inquiry.getInquiryReplyCotent();
				String type = "PS06";
				int targetNumber = inquiry.getInquiryNo().intValue();
				
				if (pushToken != null) {
					FCMUtils.sendFCM(userNo, pushToken, title, body, targetNumber, type);
					PushRecord pushRecord = new PushRecord(userNo, title, body, type, targetNumber);
					commonMapper.insertSendPushRecord(pushRecord);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/inquiry/showInquiryList/1?inquiryStatusCd=IN00";
	}

}
