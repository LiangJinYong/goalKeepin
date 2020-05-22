package goalKeepin.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.constants.LoginType;
import goalKeepin.data.CommonMapper;
import goalKeepin.data.RewardPaymentMapper;
import goalKeepin.model.Page;
import goalKeepin.model.PushRecord;
import goalKeepin.service.PageService;
import goalKeepin.util.FCMUtils;
import goalKeepin.util.SortUtils;
import goalKeepin.web.dto.CompletedChallengeResponseDto;
import goalKeepin.web.dto.RewardUserResponseDto;

@Controller
@RequestMapping("/rewardPayment")
public class RewardPaymentController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private RewardPaymentMapper rewardPaymentMapper;
	
	@Autowired
	private CommonMapper commonMapper;
	
	@GetMapping("/showRewardPaymentDetail")
	public String showRewardPaymentPage(Model model) {
		
		return "rewardPayment/rewardPaymentDetail";
	}
	
	@GetMapping("/searchUserList/{pageNum}")
	@ResponseBody
	public String searchUserList(@PathVariable("pageNum") Integer pageNum, @RequestParam("userSearchText") String userSearchText, @RequestParam(value="sort", required=false) String sort) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startIndex", (pageNum - 1) * pageSize);
		paramMap.put("pageSize", pageSize);
		
		if (sort != null && !"".equals(sort)) {
			String[] sortElements = sort.split(",");
			String sortField = sortElements[0];
			String sortOrder = sortElements[1];
			result.put("sortField", sortField);
			result.put("sortOrder", sortOrder);
			
			paramMap.put("sortField", sortField);
			paramMap.put("sortOrder", sortOrder);
		}
		paramMap.put("userSearchText", userSearchText);
		
		List<Map<String, String>> pageData = rewardPaymentMapper.selectUserListById(paramMap);
		int totalRecordNum = rewardPaymentMapper.getTotalUserCount(paramMap);
		for(Map<String, String> user : pageData) {
			String loginTypeCd = user.get("loginTypeCd");
			String loginTypeName = LoginType.valueOf(loginTypeCd).getLoginTypeName();
			user.put("loginTypeCd", loginTypeName);
		}
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		
		result.put("page", page);
		
		return gson.toJson(result);
	}
	
	@GetMapping("/showCompletedChallengeList/{pageNum}")
	public String baseManagement(@RequestParam(name = "challengeIsAllPaid", required = false) String challengeIsAllPaid,
			@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("challengeIsAllPaid", challengeIsAllPaid);
		
		List<CompletedChallengeResponseDto> pageData = rewardPaymentMapper.selectCompletedChallengeList(paramMap);
		int totalRecordNum = rewardPaymentMapper.getTotalCompletedChallengeNum(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		model.addAttribute("challengeIsAllPaid", challengeIsAllPaid);
		
		return "rewardPayment/completedChallengeList";
	}
	
	@GetMapping("/showRewardUserList/{pageNum}")
	public String showRewardUserList(@RequestParam(name = "challengeNo") Long challengeNo,
			@PathVariable("pageNum") Integer pageNum, Model model, @RequestParam(value="sort", required=false) String sort) {
		
		int pageSize = props.getPageSize();
		Map<String, Object> paramMap = SortUtils.getParamMap(pageNum, pageSize, model, sort);
		
		paramMap.put("challengeNo", challengeNo);
		
		List<Map<String, Object>> pageData = rewardPaymentMapper.selectRewardUserList(paramMap);
		int totalRecordNum = rewardPaymentMapper.getTotalRewardUserNum(paramMap);
		
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);
		model.addAttribute("page", page);
		model.addAttribute("challengeNo", challengeNo);
		
		return "rewardPayment/rewardUserList";
	}
	
	@PostMapping("/processRewardPayment")
	@ResponseBody
	public String processRewardPayment(@RequestParam("rewardUserList") String rewardUserList, @RequestParam("challengeNo") Long challengeNo) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> rewardUserListObj = mapper.readValue(rewardUserList, List.class);
			
			for(Map<String, Object> rewardUser : rewardUserListObj) {
				
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.putAll(rewardUser);
				paramMap.put("challengeNo", challengeNo);

				// 지급할 리워드, 지급여부 업데이트
				rewardPaymentMapper.updateRewardUser(paramMap);
				
				rewardPaymentMapper.insertUserCashReport(paramMap);
				
				// push
				Integer userNo = Integer.parseInt((String) rewardUser.get("userNo"));
				boolean isReceivingPushMessage = commonMapper.selectReceivingChallengePushStatus(userNo);
				
				if (isReceivingPushMessage) {
					String pushToken = commonMapper.getPushTokenByUserNo(userNo);
					String languageCode = commonMapper.getLastLanguageForUser(userNo);
					
					Map<String, String> param = new HashMap<>();
					param.put("mainCode", "P103");
					param.put("detailCode", "S001");
					param.put("languageCode", languageCode);
					String title = commonMapper.getContentForPushMessage(param);
					param.put("detailCode", "S002");
					String body = commonMapper.getContentForPushMessage(param);
					
					param.put("operatedChallengeNo", String.valueOf(challengeNo));
					String challengeName = commonMapper.getChallengeNameByLanguage(param);
					body = body.replace("###", challengeName);
					
					String type = "PS07";

					if (pushToken != null) {
						FCMUtils.sendFCM(userNo, pushToken, title, body, type);
						PushRecord pushRecord = new PushRecord(userNo, title, body, type, challengeNo.intValue());
						commonMapper.insertSendPushRecord(pushRecord);
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			return "500";
		}
		return "200";
	}
	
	@GetMapping("/excelDownload/completedChallengeList")
	public void completedChallengeList(HttpServletResponse response) throws Exception {
		List<CompletedChallengeResponseDto> completedChallengeList = rewardPaymentMapper.selectCompletedChallengeListForDownload();
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Completed Challenge List");

		makeCompletedChallengeHead(wb, sheet);
		makeCompletedChallengeBody(wb, sheet, completedChallengeList);

		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String now = sdf.format(new Date());

		String filename = "CompletedChallengeList_" + now + ".xls";

		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		wb.write(response.getOutputStream());
		wb.close();
	}
	
	private void makeCompletedChallengeHead(Workbook wb, Sheet sheet) {

		CellStyle headStyle = wb.createCellStyle();

		headStyle.setBorderTop(ExtendedFormatRecord.THIN);
		headStyle.setBorderBottom(ExtendedFormatRecord.THIN);
		headStyle.setBorderLeft(ExtendedFormatRecord.THIN);
		headStyle.setBorderRight(ExtendedFormatRecord.THIN);

		headStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		headStyle.setFillPattern(ExtendedFormatRecord.SOLID_FILL);

		headStyle.setAlignment(ExtendedFormatRecord.CENTER);

		Row row = sheet.createRow(0);

		Cell cell = row.createCell(0);
		cell.setCellStyle(headStyle);
		cell.setCellValue("NO");

		cell = row.createCell(1);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Challenge Name");

		cell = row.createCell(2);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Total Paricipation Fee");

		cell = row.createCell(3);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Refund Rate");

		cell = row.createCell(4);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Refund Amount");

		cell = row.createCell(5);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Paid Reward Number");

		cell = row.createCell(6);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Unpaid Reward Number");
	}
	
	private void makeCompletedChallengeBody(Workbook wb, Sheet sheet, List<CompletedChallengeResponseDto> completedChallengeList) {

		CellStyle bodyStyle = wb.createCellStyle();
		bodyStyle.setBorderTop(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderBottom(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderLeft(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderRight(ExtendedFormatRecord.THIN);
		Row row = null;
		Cell cell = null;
		int rowNo = 1;
		for (CompletedChallengeResponseDto completedChallenge : completedChallengeList) {
			row = sheet.createRow(rowNo++);
			cell = row.createCell(0);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rowNo - 1);

			cell = row.createCell(1);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getChallengeName());

			cell = row.createCell(2);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getTotalParticipationFee());

			cell = row.createCell(3);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getRefundRate());

			cell = row.createCell(4);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getTotalRefundAmount());

			cell = row.createCell(5);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getPaidRewardNumber());

			cell = row.createCell(6);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(completedChallenge.getUnpaidRewardNumber());
		}
	}
	
	@GetMapping("/excelDownload/rewardUserList")
	public void rewardUserList(@RequestParam("challengeNo") Long challengeNo, HttpServletResponse response) throws Exception {
		List<RewardUserResponseDto> rewardUserList = rewardPaymentMapper.selectRewardUserListForDownload(challengeNo);
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Completed Challenge List");

		makeRewardUserHead(wb, sheet);
		makeRewardUserBody(wb, sheet, rewardUserList);

		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String now = sdf.format(new Date());

		String filename = "RewardUserList_" + now + ".xls";

		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		wb.write(response.getOutputStream());
		wb.close();
	}
	
	private void makeRewardUserHead(Workbook wb, Sheet sheet) {

		CellStyle headStyle = wb.createCellStyle();

		headStyle.setBorderTop(ExtendedFormatRecord.THIN);
		headStyle.setBorderBottom(ExtendedFormatRecord.THIN);
		headStyle.setBorderLeft(ExtendedFormatRecord.THIN);
		headStyle.setBorderRight(ExtendedFormatRecord.THIN);

		headStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		headStyle.setFillPattern(ExtendedFormatRecord.SOLID_FILL);

		headStyle.setAlignment(ExtendedFormatRecord.CENTER);

		Row row = sheet.createRow(0);

		Cell cell = row.createCell(0);
		cell.setCellStyle(headStyle);
		cell.setCellValue("NO");

		cell = row.createCell(1);
		cell.setCellStyle(headStyle);
		cell.setCellValue("User ID");

		cell = row.createCell(2);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Participation Fee");

		cell = row.createCell(3);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Complete Rate");

		cell = row.createCell(4);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Reward Amount");

		cell = row.createCell(5);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Has Red Card");

		cell = row.createCell(6);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Has Paid Reward");
	}
	
	private void makeRewardUserBody(Workbook wb, Sheet sheet, List<RewardUserResponseDto> rewardUserList) {

		CellStyle bodyStyle = wb.createCellStyle();
		bodyStyle.setBorderTop(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderBottom(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderLeft(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderRight(ExtendedFormatRecord.THIN);
		Row row = null;
		Cell cell = null;
		int rowNo = 1;
		for (RewardUserResponseDto rewardUser : rewardUserList) {
			row = sheet.createRow(rowNo++);
			cell = row.createCell(0);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rowNo - 1);

			cell = row.createCell(1);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rewardUser.getUserId());

			cell = row.createCell(2);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rewardUser.getParticipationFee());

			cell = row.createCell(3);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rewardUser.getCompleteRate());

			cell = row.createCell(4);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rewardUser.getRewardAmount());

			cell = row.createCell(5);
			cell.setCellStyle(bodyStyle);
			Boolean hasRedCard = rewardUser.getHasRedCard();
			String hasRedCardText = hasRedCard ? "Yes" : "No";
			cell.setCellValue(hasRedCardText);

			cell = row.createCell(6);
			cell.setCellStyle(bodyStyle);
			Boolean hasPaidReward = rewardUser.getHasPaidReward();
			String hasPaidRewardText = hasPaidReward ? "Yes" : "No";
			cell.setCellValue(hasPaidRewardText);
		}
	}
}
