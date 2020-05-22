package goalKeepin.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.ChallengeMapper;
import goalKeepin.data.CommonMapper;
import goalKeepin.model.BaseChallenge;
import goalKeepin.model.OperatedChallenge;
import goalKeepin.model.Page;
import goalKeepin.model.ParticipantEntry;
import goalKeepin.model.PushRecord;
import goalKeepin.model.Review;
import goalKeepin.service.PageService;
import goalKeepin.util.DateUtils;
import goalKeepin.util.FCMUtils;
import goalKeepin.util.FileUploadUtils;
import goalKeepin.util.SortUtils;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private ChallengeMapper challengerMapper;
	
	@Autowired
	private CommonMapper commonMapper;
	
	@Value("${goalkeepin.upload.challenge-thumbnail}")
	private String thumbnailUploadPath;
	
	@Value("${goalkeepin.upload.challenge-detail}")
	private String detailUploadPath;

	@GetMapping("/baseManagement/{pageNum}")
	public String baseManagement(@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		List<Map<String, Object>> pageData = challengerMapper.selectBaseChallengeList(paramMap);
		int totalRecordNum = challengerMapper.getTotalBaseChallengeNum();
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		
		return "challenge/baseManagement";
	}

	@GetMapping("/showOperatedChallengeListByStatus/{status}/{pageNum}")
	public String showOperatedChallengeListByStatus(@RequestParam(value="habitTypeCd", required=false) String habitTypeCd, @PathVariable("status") String status, @PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
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
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("statusCd", statusCd);
		paramMap.put("habitTypeCd", habitTypeCd);
		
		int totalRecordNum = challengerMapper.getAllOperatedChallengeCount(paramMap);
		List<OperatedChallenge> pageData = challengerMapper.selectAllOperatedChallengeList(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		model.addAttribute("statusCd", statusCd);
		model.addAttribute("habitTypeCd", habitTypeCd);
		
		return "challenge/operatedChallengeListByStatus";
	}

	@GetMapping("/showBaseChallengeDetailForm")
	public String showChallengeGenerationForm(Model model, HttpServletRequest request) {
		model.addAttribute("baseChallenge", new BaseChallenge());
		model.addAttribute("categoryList", new ArrayList<Map<Integer, String>>());
		
		String commonUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("commonUrl", commonUrl);
		
		return "challenge/baseChallengeDetailForm";
		
	}
	
	@PostMapping("/processChallengeGeneration")
	public String processChallengeGeneration(BaseChallenge baseChallenge, Errors errors, @RequestParam("baseThumbnailUrl") MultipartFile file) {
		
		long fileSize = file.getSize();
		
		if (fileSize > 0) {
			
			String filePath = FileUploadUtils.processFileUpload(file, thumbnailUploadPath, null);
			baseChallenge.setBaseThumbnailUrl("/app/goalkeepinImage/challengeImage/" + filePath);
		}
        
        challengerMapper.insertOrUpdateBaseNmTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseAuthDescTrans(baseChallenge);
		challengerMapper.insertOrUpdateBaseDetailTrans(baseChallenge);
		
		challengerMapper.insertOrUpdateBaseChallenge(baseChallenge);

		return "redirect:/challenge/baseManagement/1";
	}
	
	@GetMapping("/showBaseChallengeDetail")
	public String showBaseChallengeDetail(@RequestParam("baseNo") Long baseNo, Model model, HttpServletRequest request) {
		
		BaseChallenge baseChallenge = challengerMapper.selectBaseChallengeByNo(baseNo);
		model.addAttribute("baseChallenge", baseChallenge);
		
		List<Map<Integer, String>> categoryList = challengerMapper.selectCategoryList();
		model.addAttribute("categoryList", categoryList);
		
		int modifiable = challengerMapper.selectModifiable(baseNo); 
		model.addAttribute("modifiable", modifiable);
		int operatedChallengeCount = challengerMapper.getOperatedChallengeCountByBase(baseNo);
		model.addAttribute("operatedChallengeCount", operatedChallengeCount);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt); 
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		
		String minStartDate = sdf.format(dt);
		
		c.add(Calendar.DATE, 1);
		dt = c.getTime();
		
		String minEndDate = sdf.format(dt);

		model.addAttribute("minStartDate", minStartDate);
		model.addAttribute("minEndDate", minEndDate);
		
		String commonUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		model.addAttribute("commonUrl", commonUrl);
		
		return "challenge/baseChallengeDetailForm";
	}
	
	@PostMapping("/createNewOperatedChallenge")
	@ResponseBody
	public String createNewChallenge(OperatedChallenge challengeDetail) {
		
		String startDate = challengeDetail.getStartDate();
		String endDate = challengeDetail.getEndDate();
		String baseAuthDateCd = challengeDetail.getBaseAuthDateCd();
		Integer baseAuthFrequency = challengeDetail.getBaseAuthFrequency();
		Integer baseAuthNumDaily = challengeDetail.getBaseAuthNumDaily();
		
		int maxResult = DateUtils.getMaxResult(startDate, endDate, baseAuthDateCd, baseAuthFrequency, baseAuthNumDaily);
		challengeDetail.setMaxResult(maxResult);
		challengerMapper.insertOperatedChallengeInfo(challengeDetail);
		return "success";
	}
	
	@GetMapping("/showOperatedChallengeList/{pageNum}")
	public String showOperatedChallengeList(@RequestParam("baseNo") Long baseNo, @PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("baseNo", baseNo);
		
		List<OperatedChallenge> pageData = challengerMapper.selectOperatedChallengeListByBaseNo(paramMap);
		int totalRecordNum = challengerMapper.getOperatedChallengeCountByBase(baseNo);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		
		String baseChallengeNmEn = challengerMapper.selectBaseChallengeNmEn(baseNo);
		model.addAttribute("baseChallengeNmEn", baseChallengeNmEn);
		model.addAttribute("baseNo", baseNo);
		
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
	public String showParticipantList(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("operatedNo", operatedNo);
		
		int challengeProofCount = challengerMapper.getChallengeProofCount(operatedNo);
		model.addAttribute("challengeProofCount", challengeProofCount);
		
		int totalRecordNum = challengerMapper.getPaticipantCountByChallenge(operatedNo);
		List<ParticipantEntry> pageData = challengerMapper.selectParticipantEntryList(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		
		model.addAttribute("operatedNo", operatedNo);
		
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
	public String showReviewListByChallenge(@RequestParam("operatedNo") Long operatedNo, @PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("operatedNo", operatedNo);
		List<Review> pageData = challengerMapper.selectReviewListByChallenge(paramMap);
		int totalRecordNum = challengerMapper.getReviewCountByChallenge(operatedNo);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		model.addAttribute("operatedNo", operatedNo);

		return "challenge/challengeReviewList";
	}
	
	@PostMapping("/detailImage")
	@ResponseBody
	public ResponseEntity<?> uploadDetailImages(@RequestParam("file") MultipartFile file) {
		try {
			String filePath = FileUploadUtils.processFileUpload(file, detailUploadPath, null);
			return ResponseEntity.ok().body("/app/goalkeepinImage/challengeDetailImage/" + filePath);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PostMapping("/cancelProject")
	@ResponseBody
	public String cancelProject(@RequestParam("operatedChallengeNo") Long operatedChallengeNo) {
		
		try {
			List<Map<String, Object>> entryUserList = challengerMapper.getEntryUserList(operatedChallengeNo);
			
			for(Map<String, Object> entryUser : entryUserList) {
				
				challengerMapper.paybackEntryFee(entryUser);
				
				challengerMapper.insertUserCashRecord(entryUser);
				
				// send push message
				Integer userNo = (Integer) entryUser.get("userNo");
				boolean isReceivingPushMessage = commonMapper.selectReceivingChallengePushStatus(userNo);
				
				if (isReceivingPushMessage) {

					String pushToken = commonMapper.getPushTokenByUserNo(userNo);
					String languageCode = commonMapper.getLastLanguageForUser(userNo);
					
					Map<String, String> param = new HashMap<>();
					param.put("mainCode", "P101");
					param.put("detailCode", "S001");
					param.put("languageCode", languageCode);
					String title = commonMapper.getContentForPushMessage(param);
					param.put("detailCode", "S002");
					String body = commonMapper.getContentForPushMessage(param);
					
					param.put("operatedChallengeNo", String.valueOf(operatedChallengeNo));
					String challengeName = commonMapper.getChallengeNameByLanguage(param);
					body = body.replace("###", challengeName);
					
					String type = "PS08";

					if (pushToken != null) {
						FCMUtils.sendFCM(userNo, pushToken, title, body, type);
						PushRecord pushRecord = new PushRecord(userNo, title, body, type, -1);
						commonMapper.insertSendPushRecord(pushRecord);
					}
				}
			}
			
			challengerMapper.deleteChallengeEntry(operatedChallengeNo);
			
			challengerMapper.deleteOperatedChallenge(operatedChallengeNo);
			
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}

	@PostMapping("/abortProject")
	@ResponseBody
	public String abortProject(@RequestParam("operatedChallengeNo") Long operatedChallengeNo) {
		
		try {
			List<Map<String, Object>> entryUserList = challengerMapper.getEntryUserList(operatedChallengeNo);
			
			for(Map<String, Object> entryUser : entryUserList) {
				
				challengerMapper.paybackEntryFee(entryUser);
				
				challengerMapper.insertUserCashRecord(entryUser);
				
				// send push message
				Integer userNo = (Integer) entryUser.get("userNo");
				boolean isReceivingPushMessage = commonMapper.selectReceivingChallengePushStatus(userNo);
				
				if (isReceivingPushMessage) {
					String pushToken = commonMapper.getPushTokenByUserNo(userNo);
					String languageCode = commonMapper.getLastLanguageForUser(userNo);
					
					Map<String, String> param = new HashMap<>();
					param.put("mainCode", "P102");
					param.put("detailCode", "S001");
					param.put("languageCode", languageCode);
					String title = commonMapper.getContentForPushMessage(param);
					param.put("detailCode", "S002");
					String body = commonMapper.getContentForPushMessage(param);
					
					param.put("operatedChallengeNo", String.valueOf(operatedChallengeNo));
					String challengeName = commonMapper.getChallengeNameByLanguage(param);
					body = body.replace("###", challengeName);
					
					String type = "PS08";

					if (pushToken != null) {
						FCMUtils.sendFCM(userNo, pushToken, title, body, type);
						PushRecord pushRecord = new PushRecord(userNo, title, body, type, operatedChallengeNo.intValue());
						commonMapper.insertSendPushRecord(pushRecord);
					}
				}
			}
			Map<String, Object> statusMap = new HashMap<>();
			
			statusMap.put("operatedChallengeNo", operatedChallengeNo);
			statusMap.put("challengeStatus", "CH04");
			challengerMapper.updateChallengeStatus(statusMap);
			
			return "200";
		} catch (Exception e) {
			e.printStackTrace();
			return "500";
		}
	}
}

 