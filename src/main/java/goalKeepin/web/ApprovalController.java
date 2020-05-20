package goalKeepin.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import goalKeepin.data.ApprovalMapper;
import goalKeepin.model.Approval;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.SortUtils;
import goalKeepin.web.dto.ApprovalDetailResponseDto;

@Controller
@RequestMapping("/approval")
public class ApprovalController {

	@Autowired
	private ApprovalMapper approvalMapper;

	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;

	@GetMapping("/showApprovalList/{pageNum}")
	public String showApprovalList(@PathVariable("pageNum") Integer pageNum, Model model, HttpServletRequest request,
			@RequestParam(value = "sort", required = false) String sort) {

		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String param = parameterNames.nextElement();
			String value = request.getParameter(param);
			model.addAttribute(param, value);
		}

		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		Map<String, String[]> approvalFormParam = request.getParameterMap();
		paramMap.putAll(convertParamMap(approvalFormParam));

		List<Approval> pageData = approvalMapper.selectApprovalList(paramMap);
		int totalRecordNum = approvalMapper.getTotalApprovalCount(paramMap);

		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);

		model.addAttribute("page", page);
		
		List<Map<String, Object>> projectList = approvalMapper.selectProjectList();
		model.addAttribute("projectList", projectList);

		return "approval/approvalList";
	}

	private Map<String, String> convertParamMap(Map<String, String[]> param) {
		Map<String, String> result = new HashMap<>();
		Set<Entry<String, String[]>> entrySet = param.entrySet();

		for (Entry<String, String[]> entry : entrySet) {
			result.put(entry.getKey(), entry.getValue()[0]);
		}

		return result;
	}

	@GetMapping("/showApprovalDetail")
	public String showApprovalDetail(@RequestParam("authNo") Long authNo, Model model) {

		ApprovalDetailResponseDto approvalDetail = approvalMapper.selectApprovalDetail(authNo);
		model.addAttribute("approvalDetail", approvalDetail);
		return "approval/approvalDetailForm";
	}

	@PostMapping("/denyVerification")
	@ResponseBody
	public Long denyVerification(@RequestParam("authNo") Long authNo) {

		Map<String, Object> param = new HashMap<>();
		param.put("authNo", authNo);
		param.put("approvalStatus", "AS03");

		approvalMapper.updateAuthAprovalStatus(param);

		Long nextAuthNo = approvalMapper.getNextWatingAuthNo(authNo);
		return nextAuthNo;
	}

	@PostMapping("/approveVerification")
	@ResponseBody
	public Long approveVerification(@RequestParam("authNo") Long authNo) {

		Map<String, Object> param = new HashMap<>();
		param.put("authNo", authNo);
		param.put("approvalStatus", "AS02");

		approvalMapper.updateAuthAprovalStatus(param);

		int authNums = approvalMapper.getChallengeAuthNum(authNo);
		int maxResult = approvalMapper.getChallengeMaxResult(authNo);

		double authNumsDouble = (double) authNums;
		double maxResultDouble = (double) maxResult;

		int currentResult = (int) (authNumsDouble / maxResultDouble * 100);

		String resultGrade = "-1";
		param.put("result", currentResult);

		if (currentResult == 100) {
			resultGrade = "0.1";
		} else if (currentResult >= 85) {
			resultGrade = "0";
		}
		
		param.put("resultGrade", resultGrade);
		approvalMapper.updateChallengeResult(param);

		Long nextAuthNo = approvalMapper.getNextWatingAuthNo(authNo);
		return nextAuthNo;
	}
	
	@PostMapping("/deleteApproval")
	@ResponseBody
	public String deleteApproval(@RequestParam("authNo") Long authNo) {
		
		try {
			approvalMapper.deleteApproval(authNo);
			
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}
}
