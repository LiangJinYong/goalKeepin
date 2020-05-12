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

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.constants.FaqAction;
import goalKeepin.data.FaqMapper;
import goalKeepin.model.Faq;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;

@Controller
@RequestMapping("/faq")
public class FaqController {
	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private FaqMapper faqMapper;

	@GetMapping("/showFaqList/{pageNum}")
	public String showFaqList(@PathVariable("pageNum") Integer pageNum, Model model,
			@RequestParam(value = "sort", required = false) String sort) {

		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		if (sort != null) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortOrder", sortOrder);
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		
		List<Faq> pageData = faqMapper.selectFaqList(paramMap);
		int totalRecordNum = faqMapper.getTotalFaqCount();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		model.addAttribute("page", page);
		
		return "faq/faqList";
	}
	
	@GetMapping("/createNewFaq")
	public String createNewFaq(Model model) {
		model.addAttribute("faq", new Faq());
		return "faq/faqDetailForm";
	}
	
	@GetMapping("/showFaqDetail")
	public String showFaqDetail(@RequestParam("faqNo") Long faqNo, Model model) {
		Faq faq = faqMapper.selectFaqDetail(faqNo);
		model.addAttribute("faq", faq);
		
		return "faq/faqDetailForm";
	}
	
	@PostMapping("/deleteFaq")
	@ResponseBody
	public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
		
		try {
			faqMapper.deleteFaq(faqNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
		return "200";
	}
	
	@PostMapping("/processFaqDetail")
	public String processFaqDetail(Faq faq) {
		faqMapper.insertOrUpdateFaqQuestion(faq);
		faqMapper.insertOrUpdateFaqAnswer(faq);
		
		String faqActionCode = faq.getFaqActionCode();
		
		if (!"".equals(faqActionCode)) {
			faqMapper.insertOrUpdateFaqKeyword(faq);
		} else {
			faq.setFaqActionCode(null);
			faq.setFaqKeywordTransNo(null);
		}
		
		faqMapper.insertOrUpdateFaq(faq);
		
		return "redirect:/faq/showFaqList/1";
	}
}
