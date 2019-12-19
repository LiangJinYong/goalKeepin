package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import goalKeepin.data.ChallengeMapper;
import goalKeepin.model.BaseChallenge;
import goalKeepin.model.OperatedChallenge;
import goalKeepin.model.Paging;
import goalKeepin.model.ParticipantEntry;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {

	@Autowired
	private ChallengeMapper challengerMapper;

	@GetMapping("/baseManagement/{pageNum}")
	public String baseManagement(@PathVariable("pageNum") Integer pageNum, Model model) {
		
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}

		paging.setCurrentPageNum(pageNum);

		int totalBaseChallengeNum = challengerMapper.getTotalBaseChallengeNum();
		paging.setTotalRecords(totalBaseChallengeNum);
		
		int startIndex = (pageNum - 1) * 10;

		List<Map<String, Object>> list = challengerMapper.selectBaseChallengeList(startIndex);
		
		model.addAttribute("challengeList", list);
		model.addAttribute("paging", paging);

		return "challenge/baseManagement";
	}

	@GetMapping("/recruiting")
	public String recruiting() {
		return "challenge/recruiting";
	}

	@GetMapping("/ongoing")
	public String onGoing() {
		return "challenge/ongoing";
	}

	@GetMapping("/expired")
	public String expired() {
		return "challenge/expired";
	}

	@GetMapping("/showBaseChallengeDetailForm")
	public String showChallengeGenerationForm(Model model) {
		model.addAttribute("baseChallenge", new BaseChallenge());
		return "challenge/baseChallengeDetailForm";
		
	}
	
	@PostMapping("/processChallengeGeneration")
	public String processChallengeGeneration(@Valid BaseChallenge baseChallenge, Errors errors, @RequestParam("baseThumbnailUrl") MultipartFile multipartFile) {
		
		if (errors.hasErrors()) {
			List<ObjectError> allErrors = errors.getAllErrors();
			for(ObjectError error : allErrors) {
				System.out.println("!!!!!" + error.toString());
			}
			return "challenge/challengeDetailForm";
		}
		
		challengerMapper.insertOrUpdateBaseNmTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseAuthDescTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseDetailTrans(baseChallenge);
		
		challengerMapper.insertOrUpdateBaseChallenge(baseChallenge);
		
		return "redirect:/challenge/baseManagement/1";
		// file://localhost/Users/liangjinyong/Desktop/uploadImages/TacoCloud.png
		
	}
	
	@GetMapping("/showBaseChallengeDetail")
	public String showBaseChallengeDetail(@RequestParam("baseNo") Long baseNo, Model model) {
		
		BaseChallenge baseChallenge = challengerMapper.selectBaseChallengeByNo(baseNo);
		
		model.addAttribute("baseChallenge", baseChallenge);
		
		List<Map<Integer, String>> categoryList = challengerMapper.selectCategoryList();
		model.addAttribute("categoryList", categoryList);
		
		int modifiable = challengerMapper.selectModifiable(baseNo); 
		model.addAttribute("modifiable", modifiable);
		int operatedChallengeCount = challengerMapper.getOperatedChallengeCountByBase(baseNo);
		model.addAttribute("operatedChallengeCount", operatedChallengeCount);
		
		return "challenge/baseChallengeDetailForm";
	}
	
	@PostMapping("/createNewOperatedChallenge")
	@ResponseBody
	public String createNewChallenge(OperatedChallenge challengeDetail) {
		
		System.out.println(challengeDetail);
		challengerMapper.insertOperatedChallengeInfo(challengeDetail);
		return "success";
	}
	
	@GetMapping("/showOperatedChallengeList/{pageNum}")
	public String showChallengeList(@RequestParam("baseNo") Long baseNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}

		paging.setCurrentPageNum(pageNum);
		
		int operatedChallengeCount = challengerMapper.getOperatedChallengeCountByBase(baseNo);
		paging.setTotalRecords(operatedChallengeCount);
		
		int startIndex = (pageNum - 1) * 10;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("baseNo", baseNo);
		paramMap.put("startIndex", startIndex);
		
		List<OperatedChallenge> operatedChallengeList = challengerMapper.selectOperatedChallengeListByBaseNo(paramMap);
		model.addAttribute("operatedChallengeList", operatedChallengeList);
		
		String baseChallengeNmEn = challengerMapper.selectBaseChallengeNmEn(baseNo);
		model.addAttribute("baseChallengeNmEn", baseChallengeNmEn);
		model.addAttribute("baseNo", baseNo);
		model.addAttribute("paging", paging);
		
		return "challenge/operatedChallengeList";
	}
	
	@GetMapping("/showOperatedChallengeDetail")
	public String showOperatedChallengeDetail(@RequestParam("operatedNo") Long operatedNo, Model model) {
		OperatedChallenge operatedChallenge = challengerMapper.selectOperatedChallengeByNo(operatedNo);
		System.out.println("===>" + operatedChallenge);
		model.addAttribute("operatedChallenge", operatedChallenge);
		return "challenge/operatedChallengeDetailForm";
	}
	
	@GetMapping("/showParticipantList/{pageNum}")
	public String showParticipantList(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}
		
		int participantCount = challengerMapper.getPaticipantCountByChallenge(operatedNo);
		paging.setTotalRecords(participantCount);

		int startIndex = (pageNum - 1) * 10;
		
		paging.setCurrentPageNum(pageNum);
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("operatedNo", operatedNo);
		paramMap.put("startIndex", startIndex);
		
		List<ParticipantEntry> participantEntryList = challengerMapper.selectParticipantEntryList(paramMap);
		model.addAttribute("participantEntryList", participantEntryList);
		model.addAttribute("paging", paging);
		
		return "challenge/challengeParticipantList";
	}
}
