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

import com.google.gson.Gson;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.UserMapper;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.util.AESUtil;
import goalKeepin.web.dto.UserDetailDto;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;

	@Autowired
	private UserMapper userMapper;

	@GetMapping("/showUserList/{country}/{pageNum}")
	public String showUserList(@PathVariable("country") String country, @PathVariable("pageNum") Integer pageNum,
			Model model, @RequestParam(value = "sort", required = false) String sort) {

		model.addAttribute("country", country);

		return "user/userList";
	}

	@GetMapping("/searchUserListByIdAndNickname/{country}/{pageNum}")
	@ResponseBody
	public String searchUserListByIdAndNickname(@PathVariable("country") String country,
			@PathVariable("pageNum") Integer pageNum, @RequestParam("userSearchText") String userSearchText,
			@RequestParam(value = "sort", required = false) String sort) {
		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();

		String nationalityCd = "NA02";

		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}

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
		paramMap.put("nationalityCd", nationalityCd);


		List<Map<String, String>> pageData = userMapper.selectUserListByIdAndNickname(paramMap);
		int totalRecordNum = userMapper.getTotalUserCount(paramMap);
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);

		result.put("page", page);

		return gson.toJson(result);
	}

	@GetMapping("/showUserDetail")
	public String showUserDetail(@RequestParam("userNo") Long userNo, Model model) throws Exception {
		UserDetailDto userDetail = userMapper.selectUserDetail(userNo);

		String phoneNumber = userDetail.getPhoneNumber();
		if (phoneNumber != null) {
			phoneNumber = AESUtil.aesDecrypt(phoneNumber);
			userDetail.setPhoneNumber(phoneNumber);
		}

		model.addAttribute("userDetail", userDetail);
		return "user/userDetail";
	}
	
	@PostMapping("/giveYellowCard")
	@ResponseBody
	public String giveYellowCard(@RequestParam("userNo") Long userNo) {
		int yellowCardNumber = userMapper.selectYellowCardNumber(userNo);
		
		if (yellowCardNumber == 0) {
			userMapper.increaseYellowCardNumber(userNo);
		} else if (yellowCardNumber == 1) {
			userMapper.increaseRedCardNumber(userNo);
			
			int redCardNumber = userMapper.selectRedCardNumber(userNo);
			
			Map<String, Object> param = new HashMap<>();
			param.put("userNo", userNo);
			if (redCardNumber == 1) {
				param.put("expiredMonthNumber", 1);
				userMapper.updateRedCardExpiredDate(param);
			} else if (redCardNumber == 2) {
				param.put("expiredMonthNumber", 3);
				userMapper.updateRedCardExpiredDate(param);
				userMapper.clearToken(userNo);
			}
		}
		return "200";
	}
	
	@PostMapping("/giveRedCard")
	@ResponseBody
	public String giveRedCard(@RequestParam("userNo") Long userNo) {
			
		int redCardNumber = userMapper.selectRedCardNumber(userNo);
		
		Map<String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		if (redCardNumber == 0) {
			param.put("expiredMonthNumber", 1);
			userMapper.increaseRedCardNumber(userNo);
			userMapper.updateRedCardExpiredDate(param);
		} else if (redCardNumber == 1) {
			param.put("expiredMonthNumber", 3);
			userMapper.increaseRedCardNumber(userNo);
			userMapper.updateRedCardExpiredDate(param);
			userMapper.clearToken(userNo);
		}
		return "200";
	}
	
	@GetMapping("/getCashReportList")
	@ResponseBody
	public List<Map<String, Object>> getCashReportList(@RequestParam("userNo") Long userNo, @RequestParam("cashReportType") String cashReportType) {
		Map<String, Object> param = new HashMap<>();
		param.put("userNo", userNo);
		param.put("cashReportType", cashReportType);
		List<Map<String, Object>> result = userMapper.selectCashReportList(param);
		
		return result;
	}

	@GetMapping("/excelDownload/{country}")
	public void excelDownload(@PathVariable("country") String country, HttpServletResponse response) throws Exception {
		String nationalityCd = "NA02";

		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}

		List<Map<String, String>> userList = userMapper.selectUserListForDownload(nationalityCd);

		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("User List");

		makeHead(wb, sheet);
		makeBody(wb, sheet, userList);

		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String now = sdf.format(new Date());

		String filename = "UserList_" + now + ".xls";

		response.setContentType("ms-vnd/excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		wb.write(response.getOutputStream());
		wb.close();
	}

	private void makeHead(Workbook wb, Sheet sheet) {

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
		cell.setCellValue("Nickname");

		cell = row.createCell(3);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Cash Amount");

		cell = row.createCell(4);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Withdraw Amount");

		cell = row.createCell(5);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Reward Amount");

		cell = row.createCell(6);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Yellow Card");

		cell = row.createCell(7);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Red Card");

		cell = row.createCell(8);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Red Card Expired Date");
	}

	private void makeBody(Workbook wb, Sheet sheet, List<Map<String, String>> userList) {

		CellStyle bodyStyle = wb.createCellStyle();
		bodyStyle.setBorderTop(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderBottom(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderLeft(ExtendedFormatRecord.THIN);
		bodyStyle.setBorderRight(ExtendedFormatRecord.THIN);
		Row row = null;
		Cell cell = null;
		int rowNo = 1;
		for (Map<String, String> user : userList) {
			row = sheet.createRow(rowNo++);
			cell = row.createCell(0);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(rowNo - 1);

			cell = row.createCell(1);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(user.get("userId"));

			cell = row.createCell(2);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(user.get("nickName"));

			cell = row.createCell(3);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(user.get("userCash"));

			cell = row.createCell(4);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("withdrawAmount")));

			cell = row.createCell(5);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("rewardAmount")));

			cell = row.createCell(6);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("yellowCardNumber")));

			cell = row.createCell(7);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("redCardNumber")));

			cell = row.createCell(8);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(user.get("redCardExpiredDate"));
		}
	}
}
