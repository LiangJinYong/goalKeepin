package goalKeepin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import goalKeepin.data.ChallengeMapper;
import goalKeepin.model.BaseChallenge;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {

	@Autowired
	private ChallengeMapper challengerMapper;

	@GetMapping("/baseManagement")
	public String baseManagement(HttpServletRequest request, Model model) {

		Map<String, Object> result = new HashMap<String, Object>();

		String pageNum = request.getParameter("pageNum");

		if (pageNum == null) {
			pageNum = "1";
		}

		int currentPageNum = Integer.parseInt(pageNum);

		// totalPageSize totalRecordSize
		int totalRecordSize = challengerMapper.getTotalRecordSize();
		int totalPageSize = (totalRecordSize - 1) / 10 + 1;

		int startIndex = (currentPageNum - 1) * 10;

		List<Map<String, Object>> list = challengerMapper.selectBaseChallengeList(startIndex);
		
		model.addAttribute("challengeList", list);
		model.addAttribute("totalPageSize", totalPageSize);

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
	public String challengeGenerationForm(Model model) {
		
		model.addAttribute("baseChallenge", new BaseChallenge());
		return "challenge/challengeGenerationForm";
	}
	
	@PostMapping("/processChallengeGeneration")
	public String processChallengeGeneration(@Valid BaseChallenge baseChallenge, Errors errors) {
		System.out.println(baseChallenge);
		if (errors.hasErrors()) {
			System.out.println("haserrors");
			return "challenge/challengeGenerationForm";
		}
		challengerMapper.insertBaseNmTrans(baseChallenge);
		challengerMapper.insertBaseAuthDescTrans(baseChallenge);
		challengerMapper.insertBaseDetailTrans(baseChallenge);
		System.out.println("==>" + baseChallenge);
		
		challengerMapper.insertBaseChallenge(baseChallenge);
		return "challenge/challengeDetail";
	}
}
