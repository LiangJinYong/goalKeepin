package goalKeepin.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import goalKeepin.data.NoticeMapper;
import goalKeepin.model.Notice;
import goalKeepin.model.Paging;
import goalKeepin.util.PagingUtils;

@Controller
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	private NoticeMapper noticeMapper;
	
	@GetMapping("/showNoticeList/{pageNum}")
	public String showNoticeList(@PathVariable("pageNum") Integer pageNum, Model model) {
		
		int totalNoticeCount = noticeMapper.getTotalNoticeCount();
		Paging paging = PagingUtils.getPaging(pageNum, totalNoticeCount);
		
		int startIndex = (pageNum - 1) * 10;

		List<Notice> noticeList = noticeMapper.selectNoticeList(startIndex);
		
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("paging", paging);
		return "notice/noticeList";
	}
	
	@GetMapping("/createNewNotice")
	public String createNewNotice(Model model) {
		model.addAttribute("notice", new Notice());
		return "notice/noticeDetailForm";
	}
	
	@PostMapping("/processNoticeDetail")
	public String processNoticeDetail(Notice notice, @RequestParam("noticeImgs") MultipartFile[] noticeImgs) {
		
		noticeMapper.insertOrUpdateNoticeTitle(notice);
		noticeMapper.insertOrUpdateNoticeContent(notice);
		noticeMapper.insertOrUpdateNotice(notice);
		return "redirect:/notice/showNoticeList/1";
	}
}
