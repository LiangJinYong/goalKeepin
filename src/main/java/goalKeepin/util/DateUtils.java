package goalKeepin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author liangjinyong
 * Caculate the max result when starting a new project
 */
public class DateUtils {

	private DateUtils() {}
	
	public static int getMaxResult(String startDate, String endDate, String authDateCd, int authFrequency, int authNumDaily) {
		
		List<String> authableDays = getAuthableDays(startDate, endDate, authDateCd);
		List<List<String>> authableDaysByWeek = groupAuthableDaysByWeek(authableDays);
		int maxResult = calculateMaxResult(authableDaysByWeek, authFrequency, authNumDaily);
		return maxResult;
	}
	
	private static List<String> getAuthableDays(String strStartDate, String strEndDate, String authDateCd) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cl1 = Calendar.getInstance();
		Calendar cl2 = Calendar.getInstance();

		cl1.setFirstDayOfWeek(Calendar.MONDAY);
		cl2.setFirstDayOfWeek(Calendar.MONDAY);

		try {
			cl1.setTime(df.parse(strStartDate));
			cl2.setTime(df.parse(strEndDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<String> result = new ArrayList<>();

		if ("AD01".equals(authDateCd)) {
			while (cl1.compareTo(cl2) < 0) {
				if (cl1.get(Calendar.DAY_OF_WEEK) != 7 && cl1.get(Calendar.DAY_OF_WEEK) != 1) {
					Date date1 = cl1.getTime();
					String date1Str = df.format(date1);
					result.add(date1Str);
				}
				cl1.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else if ("AD02".equals(authDateCd)) {
			while (cl1.compareTo(cl2) < 0) {
				if (cl1.get(Calendar.DAY_OF_WEEK) == 7 || cl1.get(Calendar.DAY_OF_WEEK) == 1) {
					Date date1 = cl1.getTime();
					String date1Str = df.format(date1);
					result.add(date1Str);
				}
				cl1.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else {
			while (cl1.compareTo(cl2) < 0) {
				Date date1 = cl1.getTime();
				String date1Str = df.format(date1);
				result.add(date1Str);

				cl1.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		
		return result;
	}

	private static List<List<String>> groupAuthableDaysByWeek(List<String> authableDays) {
		List<List<String>> result = new ArrayList<>();
		if (authableDays.size() > 1) {
			List<String> list = new ArrayList<>();
			list.add(authableDays.get(0));
			for (int i = 0; i < authableDays.size() - 1; i++) {
				if (isSameDate(authableDays.get(i), authableDays.get(i + 1))) {
					list.add(authableDays.get(i + 1));
				} else {
					result.add(list);
					list = new ArrayList<>();
					list.add(authableDays.get(i + 1));
				}
			}
			result.add(list);
		} else {
			result.add(authableDays);
		}
		
		return result;
	}
	
	private static int calculateMaxResult(List<List<String>> authableDaysByWeek, int authFrequency, int authNumDaily) {
		int totalDays = 0;
		for(List<String> list: authableDaysByWeek) {
			int weekFrequency = list.size();
			if (weekFrequency > authFrequency) {
				weekFrequency = authFrequency;
			}
			totalDays += weekFrequency;
		}
		
		return totalDays * authNumDaily;
	}

	private static boolean isSameDate(String date1, String date2) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(date1);
			d2 = format.parse(date2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setFirstDayOfWeek(Calendar.MONDAY);// set Monday as first day
		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		cal1.setTime(d1);
		cal2.setTime(d2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (subYear == 0)// same year
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) // not same year
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)
		{
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}
}
