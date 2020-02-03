package goalKeepin.data;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import goalKeepin.model.Notice;

@Mapper
public interface NoticeMapper {

	int getTotalNoticeCount();

	List<Notice> selectNoticeList(Map<String, Object> paramMap);

	void insertOrUpdateNoticeTitle(Notice notice);

	void insertOrUpdateNoticeContent(Notice notice);

	void insertOrUpdateNoticeImgUrl(Notice notice);

	void insertOrUpdateNotice(Notice notice);

	Notice selectNoticeDetail(Long noticeNo);

	void deleteNotice(Long noticeNo);

	void deleteNoticeImage(Map<String, Object> paramMap);

}
