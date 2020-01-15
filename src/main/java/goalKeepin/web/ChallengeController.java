package goalKeepin.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import goalKeepin.data.ChallengeMapper;
import goalKeepin.model.BaseChallenge;
import goalKeepin.model.OperatedChallenge;
import goalKeepin.model.Paging;
import goalKeepin.model.ParticipantEntry;
import goalKeepin.model.Review;
import goalKeepin.util.DateUtils;
import goalKeepin.util.FileUploadUtils;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {

	@Autowired
	private ChallengeMapper challengerMapper;
	private final static String FILE_UPLOAD_PATH = "/var/lib/tomcat8/webapps/goalkeepinImage/challengeImage/";

	@GetMapping("/baseManagement/{pageNum}")
	public String baseManagement(@PathVariable("pageNum") Integer pageNum, Model model) {
		
		int totalBaseChallengeNum = challengerMapper.getTotalBaseChallengeNum();
		Paging paging = PagingUtils.getPaging(pageNum, totalBaseChallengeNum);

		int startIndex = (pageNum - 1) * 10;
		List<Map<String, Object>> list = challengerMapper.selectBaseChallengeList(startIndex);
		
		model.addAttribute("challengeList", list);
		model.addAttribute("paging", paging);

		return "challenge/baseManagement";
	}

	@GetMapping("/showOperatedChallengeListByStatus/{status}/{pageNum}")
	public String showOperatedChallengeListByStatus(@RequestParam(value="habitTypeCd", required=false) String habitTypeCd, @PathVariable("status") String status, @PathVariable("pageNum") Integer pageNum, Model model) {
		
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
		Paging paging = PagingUtils.getPaging(pageNum, operatedChallengeCount);
		
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
	public String processChallengeGeneration(BaseChallenge baseChallenge, Errors errors, @RequestParam("baseThumbnailUrl") MultipartFile file) {
		/*
		if (errors.hasErrors()) {
			List<FieldError> fieldErrors = errors.getFieldErrors();
			for(FieldError error: fieldErrors) {
				System.out.println("===>" + error.getField());
			}
			return "challenge/baseChallengeDetailForm";
		}
		*/
		long fileSize = file.getSize();
		
		if (fileSize > 0) {
			
			String filePath = FileUploadUtils.processFileUpload(file, FILE_UPLOAD_PATH, null);
			baseChallenge.setBaseThumbnailUrl("/app/goalkeepinImage/challengeImage/" + filePath);
		}
        
        
        challengerMapper.insertOrUpdateBaseNmTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseAuthDescTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseDetailTrans(baseChallenge);
		
		challengerMapper.insertOrUpdateBaseChallenge(baseChallenge);

		return "redirect:/challenge/baseManagement/1";
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
		
		String startDate = challengeDetail.getStartDate();
		String endDate = challengeDetail.getEndDate();
		String baseAuthMethodCd = challengeDetail.getBaseAuthMethodCd();
		Integer baseAuthFrequency = challengeDetail.getBaseAuthFrequency();
		Integer baseAuthNumDaily = challengeDetail.getBaseAuthNumDaily();
		
			
		int maxResult = DateUtils.getMaxResult(startDate, endDate, baseAuthMethodCd, baseAuthFrequency, baseAuthNumDaily);
		challengeDetail.setMaxResult(maxResult);
		
		challengerMapper.insertOperatedChallengeInfo(challengeDetail);
		return "success";
	}
	
	@GetMapping("/showOperatedChallengeList/{pageNum}")
	public String showOperatedChallengeList(@RequestParam("baseNo") Long baseNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		int operatedChallengeCount = challengerMapper.getOperatedChallengeCountByBase(baseNo);
		Paging paging = PagingUtils.getPaging(pageNum, operatedChallengeCount);
		
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
		int participantCount = challengerMapper.getPaticipantCountByChallenge(operatedNo);
		int challengeProofCount = challengerMapper.getChallengeProofCount(operatedNo);
		int reviewCount = challengerMapper.getReviewCountByChallenge(operatedNo);
		
		model.addAttribute("operatedChallenge", operatedChallenge);
		model.addAttribute("participantCount", participantCount);
		model.addAttribute("challengeProofCount", challengeProofCount);
		model.addAttribute("reviewCount", reviewCount);
		
		return "challenge/operatedChallengeDetailForm";
	}
	
	@GetMapping("/showParticipantList/{pageNum}")
	public String showParticipantList(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		int challengeProofCount = challengerMapper.getChallengeProofCount(operatedNo);
		int participantCount = challengerMapper.getPaticipantCountByChallenge(operatedNo);
		Paging paging = PagingUtils.getPaging(pageNum, participantCount);

		int startIndex = (pageNum - 1) * 10;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("operatedNo", operatedNo);
		paramMap.put("startIndex", startIndex);
		
		List<ParticipantEntry> participantEntryList = challengerMapper.selectParticipantEntryList(paramMap);
		model.addAttribute("participantEntryList", participantEntryList);
		model.addAttribute("challengeProofCount", challengeProofCount);
		model.addAttribute("operatedNo", operatedNo);
		model.addAttribute("paging", paging);
		
		return "challenge/challengeParticipantList";
	}
	
	@GetMapping("/showParticipantProofInfo")
	public String showParticipantProofInfo(@RequestParam("entryNo") Long entryNo, Model model) {
		
		ParticipantEntry participantEntry = challengerMapper.selectEntryInfoByParticipant(entryNo);
		model.addAttribute("participantEntry", participantEntry);
		String baseAuthMethodCd = participantEntry.getOperatedChallenge().getBaseChallenge().getBaseAuthMethodCd();
		model.addAttribute("baseAuthMethodCd", baseAuthMethodCd);
		
		List<Map<String, String>> proofList = challengerMapper.selectParticipantProofList(entryNo);
		
		if ("AU02".equals(baseAuthMethodCd)) {
			for(Map<String, String> proof : proofList) {
				String proofUrl = proof.get("proofUrl");
				String fileName = proofUrl.substring(proofUrl.lastIndexOf("/"));
				
				proof.put("fileName", fileName);
			}
		}
		
		model.addAttribute("proofList", proofList);
		return "challenge/participantProofInfo";
	}
	
	@GetMapping("/showAllProofShotsByChallenge")
	public String showAllProofShotsByChallenge(@RequestParam("operatedNo") Long operatedNo, Model model) {
		OperatedChallenge operatedChallenge = challengerMapper.selectOperatedChallengeInfo(operatedNo);
		String baseAuthMethodCd = operatedChallenge.getBaseChallenge().getBaseAuthMethodCd();
		
		List<Map<String, String>> proofList = challengerMapper.selectChallengeProofList(operatedNo);
		if ("AU02".equals(baseAuthMethodCd)) {
			for(Map<String, String> proof : proofList) {
				String proofUrl = proof.get("proofUrl");
				String fileName = proofUrl.substring(proofUrl.lastIndexOf("/"));
				
				proof.put("fileName", fileName);
			}
		}
		
		model.addAttribute("proofList", proofList);
		model.addAttribute("operatedChallenge", operatedChallenge);
		model.addAttribute("baseAuthMethodCd", baseAuthMethodCd);
		return "challenge/challengeProofInfo";
	}
	
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<InputStreamResource> downloadAudioProofFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) throws IOException {
		String directory = "/var/lib/tomcat8/webapps/goalkeepinImage/authInfo/audio/";
		File file = new File(directory + "/" + fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
 
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length()) //
                .body(resource);
	}

	@GetMapping("/showReviewListByChallenge/{pageNum}")
	public String showReviewListByChallenge(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model) {
		
		int reviewCount = challengerMapper.getReviewCountByChallenge(operatedNo);
		Paging paging = PagingUtils.getPaging(pageNum, reviewCount);
		
		int startIndex = (pageNum - 1) * 10;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("operatedNo", operatedNo);
		paramMap.put("startIndex", startIndex);
		
		List<Review> reviewList = challengerMapper.selectReviewListByChallenge(paramMap);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("paging", paging);
		model.addAttribute("operatedNo", operatedNo);
		return "challenge/challengeReviewList";
	}
}

 