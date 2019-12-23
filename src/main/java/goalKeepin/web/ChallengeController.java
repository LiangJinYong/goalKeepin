package goalKeepin.web;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import goalKeepin.model.Review;

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

	@GetMapping("/showOperatedChallengeListByStatus/{status}/{pageNum}")
	public String showOperatedChallengeListByStatus(@RequestParam(value="habitTypeCd", required=false) String habitTypeCd, @PathVariable("status") String status, @PathVariable("pageNum") Integer pageNum, Model model) {
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}

		paging.setCurrentPageNum(pageNum);
		
		String statusCd;
		if ("recruiting".equals(status)) {
			statusCd = "CH01";
		} else if ("ongoing".equals(status)) {
			statusCd = "CH02";
		} else {
			statusCd = "CH03";
		}
		
		if (habitTypeCd == null) {
			habitTypeCd = "HT00";
		}
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("statusCd", statusCd);
		paramMap.put("habitTypeCd", habitTypeCd);
		int operatedChallengeCount = challengerMapper.getAllOperatedChallengeCount(paramMap);
		paging.setTotalRecords(operatedChallengeCount);
		
		int startIndex = (pageNum - 1) * 10;
		paramMap.put("startIndex", startIndex);
		
		List<OperatedChallenge> operatedChallengeList = challengerMapper.selectAllOperatedChallengeList(paramMap);
		model.addAttribute("operatedChallengeList", operatedChallengeList);
		model.addAttribute("statusCd", statusCd);
		model.addAttribute("paging", paging);
		model.addAttribute("habitTypeCd", habitTypeCd);
		
		return "challenge/operatedChallengeListByStatus";
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
		model.addAttribute("operatedNo", operatedNo);
		model.addAttribute("paging", paging);
		
		return "challenge/challengeParticipantList";
	}
	
	@GetMapping("/showParticipantProofInfo")
	public String showParticipantProofInfo(@RequestParam("entryNo") Long entryNo, Model model) {
		
		ParticipantEntry participantEntry = challengerMapper.selectEntryInfoByParticipant(entryNo);
		System.out.println("====>" + participantEntry);
		model.addAttribute("participantEntry", participantEntry);
		String baseAuthMethodCd = participantEntry.getOperatedChallenge().getBaseChallenge().getBaseAuthMethodCd();
		
		if ("AU01".equals(baseAuthMethodCd)) {
			// Photo
			// need photo url, proof date
		} else {
			// Audio
			// need audio url, proof date, file name
		}
			
		List<Map<String, String>> proofList = new ArrayList();
		Map<String, String> map = new HashMap<>();
		map.put("proofUrl", "http://localhost:8080/challenge/download/b.mp3");
		map.put("proofDate", "2019.11.02 12:00:00");
		map.put("fileName", "b.mp3");
		proofList.add(map);
		
		map = new HashMap<>();
		map.put("proofUrl", "http://localhost:8080/challenge/download/b.jpg");
		map.put("proofDate", "2019.11.01 13:00:00");
		map.put("fileName", "bbb.jpg");
		proofList.add(map);
		
		model.addAttribute("proofList", proofList);
		return "challenge/participantProofInfo";
	}
	
	@GetMapping("/showAllProofShotsByChallenge")
	public String showAllProofShotsByChallenge(@RequestParam("operatedNo") Long operatedNo, Model model) {
		OperatedChallenge operatedChallenge = challengerMapper.selectOperatedChallengeInfo(operatedNo);
		String baseAuthMethodCd = operatedChallenge.getBaseChallenge().getBaseAuthMethodCd();
		
		model.addAttribute("operatedChallenge", operatedChallenge);
		model.addAttribute("baseAuthMethodCd", baseAuthMethodCd);
		return "challenge/participantProofInfo";
	}
	
	@GetMapping("/download/{fileName:.+}")
	public void downloadAudioProofFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) {
		String dataDirectory = request.getServletContext().getRealPath("/WEB-INF/downloads/");
		Path file = Paths.get(dataDirectory, fileName);
		
		
		if (Files.exists(file)) {
			String mimeType = URLConnection.guessContentTypeFromName(fileName);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			
			response.setContentType(mimeType);
			response.addHeader("Content-Disposition", String.format("attachment;filename=" + fileName));
			
			try {
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("/showReviewListByChallenge/{pageNum}")
	public String showReviewListByChallenge(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		Paging paging = new Paging();

		if (pageNum == null) {
			pageNum = 1;
		}
		
		int reviewCount = challengerMapper.getReviewCountByChallenge(operatedNo);
		paging.setTotalRecords(reviewCount);

		int startIndex = (pageNum - 1) * 10;
		paging.setCurrentPageNum(pageNum);
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("operatedNo", operatedNo);
		paramMap.put("startIndex", startIndex);
		
		List<Review> reviewList = challengerMapper.selectReviewListByChallenge(paramMap);
		System.out.println(reviewList);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("paging", paging);
		model.addAttribute("operatedNo", operatedNo);
		return "challenge/challengeReviewList";
	}
	
}