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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import goalKeepin.config.GoalKeepinProps;
import goalKeepin.data.BalanceMapper;
import goalKeepin.data.DashboardMapper;
import goalKeepin.model.Page;
import goalKeepin.service.PageService;
import goalKeepin.web.dto.BalanceResponseDto;

@Controller
@RequestMapping("/balance")
public class BalanceController {
	@Autowired
	private PageService pageService;

	@Autowired
	private GoalKeepinProps props;
	
	@Autowired
	private DashboardMapper dashboardMapper;
	
	@Autowired
	private BalanceMapper balanceMapper;
	
	@GetMapping
	public String getBalanceData(Model model) {
		Double totalFeeAmount=dashboardMapper.selectTotalFeeAmount();
		Double totalRewardAmount=dashboardMapper.selectTotalRewardAmount();
		Double totalPaymentAmount=dashboardMapper.selectTotalPaymentAmount();
		Double totalCommissionAmount=dashboardMapper.selectTotalCommissionAmount();
		
		Double totalServiceCharge = balanceMapper.selectTotalServiceCharge();
		Double totalPenaltyAmount = balanceMapper.selectTotalPenaltyAmount();
		
		BalanceResponseDto balance = new BalanceResponseDto(totalFeeAmount, totalRewardAmount, totalServiceCharge, totalPaymentAmount, totalCommissionAmount, totalPenaltyAmount);
		
		model.addAttribute("balance", balance);
		return "balance/balance";
	}
	
	@GetMapping("/searchUserListByIdAndNickname/{pageNum}")
	@ResponseBody
	public String searchUserListByIdAndNickname(@PathVariable("pageNum") Integer pageNum, @RequestParam("userSearchText") String userSearchText,
			@RequestParam(value = "sort", required = false) String sort) {
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
		
		List<Map<String, String>> pageData = balanceMapper.selectUserListByIdAndNickname(paramMap);
		int totalRecordNum = balanceMapper.getTotalUserCount(paramMap);
		Page page = pageService.getPage(pageNum, pageData, totalRecordNum, pageSize);

		result.put("page", page);

		return gson.toJson(result);
	}
	
	@GetMapping("/excelDownload")
	public void excelDownload(HttpServletResponse response) throws Exception {

		List<Map<String, String>> userList = balanceMapper.selectUserListForDownload();

		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Balance User List");

		makeHead(wb, sheet);
		makeBody(wb, sheet, userList);

		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String now = sdf.format(new Date());

		String filename = "BalanceUserList_" + now + ".xls";

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
		cell.setCellValue("Cash Charge");

		cell = row.createCell(4);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Participation Fee");

		cell = row.createCell(5);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Withdraw Amount");

		cell = row.createCell(6);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Service Charge");

		cell = row.createCell(7);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Reward Amount");

		cell = row.createCell(8);
		cell.setCellStyle(headStyle);
		cell.setCellValue("Penalty Amount");
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
			cell.setCellValue(String.valueOf(user.get("cashCharge")));

			cell = row.createCell(4);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("participationFee")));

			cell = row.createCell(5);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("withdrawAmount")));

			cell = row.createCell(6);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("serviceCharge")));

			cell = row.createCell(7);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("rewardAmount")));

			cell = row.createCell(8);
			cell.setCellStyle(bodyStyle);
			cell.setCellValue(String.valueOf(user.get("penaltyAmount")));
		}
	}
}
