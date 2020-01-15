package goalKeepin.web;

import java.util.Date;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
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

import goalKeepin.data.CashReportMapper;
import goalKeepin.model.CashReport;
import goalKeepin.model.Paging;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/cashReport")
public class CashReportController {

	@Autowired
	private CashReportMapper cashReportMapper;
	
	@GetMapping("/showCashReportList/{country}/{pageNum}")
	public String showCashReportList(@PathVariable("country") String country, @PathVariable("pageNum") Integer pageNum, Model model, HttpServletRequest request) {
		
		String nationalityCd = "NA02";
		
		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}
		
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String param = parameterNames.nextElement();
			String value = request.getParameter(param);
			model.addAttribute(param, value);
		}
		
		// get bank list
		List<String> bankList = cashReportMapper.selectBankList(nationalityCd);
		model.addAttribute("bankList", bankList);
		
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, String[]> cashReportFormParam = request.getParameterMap();
		
		paramMap.putAll(convertParamMap(cashReportFormParam));
		paramMap.put("nationalityCd", nationalityCd);
		
		int totalCashReportCount = cashReportMapper.getTotalCashReportCount(paramMap);
		Paging paging = PagingUtils.getPaging(pageNum, totalCashReportCount);
		
		int startIndex = (pageNum - 1) * 10;
		
		paramMap.put("startIndex", startIndex);
		List<CashReport> cashReportList = cashReportMapper.selectCashReportList(paramMap);
		
		model.addAttribute("cashReportList", cashReportList);
		model.addAttribute("paging", paging);
		model.addAttribute("country", country);
		return "cashReport/cashReportList";
	}
	
	@PostMapping("/processCashReport")
	@ResponseBody
	public String processCashReport(@RequestParam("cashReportIdList[]") List<String> cashReportIdList) {
		try {
			cashReportMapper.updateCashReportStatus(cashReportIdList);
			return "success";
		} catch(Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	private Map<String, String> convertParamMap(Map<String, String[]> param) {
		Map<String, String> result = new HashMap<>();
		Set<Entry<String,String[]>> entrySet = param.entrySet();
		
		for(Entry<String, String[]> entry: entrySet) {
			result.put(entry.getKey(), entry.getValue()[0]);
		}
		
		return result;
	}
	
	@GetMapping("/excelDownload/{country}")
	public void excelDownload(@PathVariable("country") String country, HttpServletResponse response) throws Exception {
		
		String nationalityCd = "NA02";
		
		if ("hk".equals(country)) {
			nationalityCd = "NA01";
		}
		
		List<Map<String, String>> cashReportList = cashReportMapper.selectCashReportForDownload(nationalityCd);
		Workbook wb = new HSSFWorkbook();
	    Sheet sheet = wb.createSheet("Cash Reports");
	    
	    makeHead(wb, sheet);
	    makeBody(wb, sheet, cashReportList);
	    
	    for(int i=0; i<6; i++) {
	    	sheet.autoSizeColumn(i);
	    }
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String now = sdf.format(new Date());
	    
	    String filename = "CashReport_" + now + ".xls";
	    
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
	    cell.setCellValue("Amount");
	    
	    cell = row.createCell(3);
	    cell.setCellStyle(headStyle);
	    cell.setCellValue("Bank");
	    
	    cell = row.createCell(4);
	    cell.setCellStyle(headStyle);
	    cell.setCellValue("Account");
	    
	    cell = row.createCell(5);
	    cell.setCellStyle(headStyle);
	    cell.setCellValue("Account Holder");
	    
	    cell = row.createCell(6);
	    cell.setCellStyle(headStyle);
	    cell.setCellValue("Date");
	    
	    cell = row.createCell(7);
	    cell.setCellStyle(headStyle);
	    cell.setCellValue("Status");
	}
	
	private void makeBody(Workbook wb, Sheet sheet, List<Map<String, String>> cashReportList) {
		
		CellStyle bodyStyle = wb.createCellStyle();
	    bodyStyle.setBorderTop(ExtendedFormatRecord.THIN);
	    bodyStyle.setBorderBottom(ExtendedFormatRecord.THIN);
	    bodyStyle.setBorderLeft(ExtendedFormatRecord.THIN);
	    bodyStyle.setBorderRight(ExtendedFormatRecord.THIN);
	    Row row = null;
	    Cell cell = null;
	    int rowNo = 1;
		for(Map<String, String> cashReport: cashReportList) {
			row = sheet.createRow(rowNo++);
	        cell = row.createCell(0);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(rowNo - 1);
	        
	        cell = row.createCell(1);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("userId"));
	        
	        cell = row.createCell(2);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("reportCashAmount"));
	        
	        cell = row.createCell(3);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("reportBank"));
	        
	        cell = row.createCell(4);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("reportAccount"));
	        
	        cell = row.createCell(5);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("reportAccountHolder"));
	        
	        cell = row.createCell(6);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue(cashReport.get("reportRegDate"));
	        
	        cell = row.createCell(7);
	        cell.setCellStyle(bodyStyle);
	        
	        CellStyle cellStyle = wb.createCellStyle();
	        Font font = wb.createFont();
	        String reportStatusCd = cashReport.get("reportStatusCd");
	        if ("완료".equals(reportStatusCd)) {
	        	font.setColor(HSSFColor.GREEN.index);
			} else {
				font.setColor(HSSFColor.RED.index);
			}
	        cellStyle.setFont(font); 
	        cell.setCellStyle(cellStyle);
	        cell.setCellValue(reportStatusCd);
		}
	}

}
