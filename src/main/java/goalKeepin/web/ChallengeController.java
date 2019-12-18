package goalKeepin.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;

import goalKeepin.data.ChallengeMapper;
import goalKeepin.model.BaseChallenge;
import goalKeepin.model.ChallengeDetail;
import goalKeepin.model.Paging;

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

		// totalPageSize totalRecordSize
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

	@GetMapping("/showChallengeGenerationForm")
	public String showChallengeGenerationForm(Model model) {
		model.addAttribute("baseChallenge", new BaseChallenge());
		return "challenge/challengeDetailForm";
		
	}
	
	@PostMapping("/processChallengeGeneration")
	public String processChallengeGeneration(@Valid BaseChallenge baseChallenge, Errors errors, @RequestParam("baseThumbnailUrl") MultipartFile multipartFile) {
		System.out.println(baseChallenge);
		
		if (errors.hasErrors()) {
			System.out.println("haserrors");
			return "challenge/challengeDetailForm";
		}
		challengerMapper.insertBaseNmTrans(baseChallenge);
		challengerMapper.insertBaseAuthDescTrans(baseChallenge);
		challengerMapper.insertBaseDetailTrans(baseChallenge);
		System.out.println("==>" + baseChallenge);
		
		try {
			multipartFile.transferTo(new File(multipartFile.getOriginalFilename()));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		challengerMapper.insertBaseChallenge(baseChallenge);
		return "redirect:/challenge/baseManagement";
		// file://localhost/Users/liangjinyong/Desktop/uploadImages/TacoCloud.png
	}
	
	@GetMapping("/showChallengeDetail")
	public String showChallengeDetail(@RequestParam("baseNo") Long baseNo, Model model) {
		
		BaseChallenge baseChallenge = challengerMapper.selectBaseChallengeByNo(baseNo);
		model.addAttribute("baseChallenge", baseChallenge);
		model.addAttribute("baseNo", baseChallenge.getBaseNo());
		List<Map<Integer, String>> categoryList = challengerMapper.selectCategoryList();
		model.addAttribute("categoryList", categoryList);
		
		int modifiable = challengerMapper.selectModifiable(baseNo); 
		model.addAttribute("modifiable", modifiable);
		
		return "challenge/challengeDetailForm";
	}
	
	@PostMapping("/createNewChallenge")
	@ResponseBody
	public String createNewChallenge(ChallengeDetail challengeDetail) {
		
		System.out.println(challengeDetail);
		challengerMapper.insertChallengeDetailInfo(challengeDetail);
		return "success";
	}
	
	@GetMapping("/showChallengeList/{pageNum}")
	public String showChallengeList(@RequestParam("baseNo") Long baseNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}

		paging.setCurrentPageNum(pageNum);
		
		int totalDetailChallengeNum = challengerMapper.getTotalDetailChallengeNumByBase(baseNo);
		paging.setTotalRecords(totalDetailChallengeNum);
		
		int startIndex = (pageNum - 1) * 10;
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("baseNo", baseNo);
		paramMap.put("startIndex", startIndex);
		
		List<ChallengeDetail> challengeDetailList = challengerMapper.selectChallengeListByBaseNo(paramMap);
		model.addAttribute("challengeDetailList", challengeDetailList);
		
		String baseChallengeNmEn = challengerMapper.selectBaseChallengeNmEn(baseNo);
		model.addAttribute("baseChallengeNmEn", baseChallengeNmEn);
		model.addAttribute("baseNo", baseNo);
		model.addAttribute("paging", paging);
		System.out.println(paging);
		
		return "challenge/challengeList";
	}
	
}
