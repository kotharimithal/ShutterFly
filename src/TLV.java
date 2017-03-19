import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class TLV {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	public TLV() {
		// TODO Auto-generated constructor stub
	}

	public void TopXSimpleLTVCustomers(int number, File outputTlvFilePath) {
		// TODO Auto-generated method stub
		if (number > 0){
			String endDate = getDate(-1, 0);
			String startDate = getDate(0, -1);
			Database db = null;
			try {
				db = new Database();
				db.calculateTLV(startDate, endDate, String.valueOf(number), outputTlvFilePath);
			} catch (Exception e) {
				System.out.println("Error occured while calculating TLV");
			} finally {
				db.closeConnection();
			}
		}
	}
	
	private String getDate(int subtractDays, int subtractYear){
		Date date = new Date();
	    Calendar instance = Calendar.getInstance();
	    instance.setTime(date);
	    instance.add(Calendar.DAY_OF_YEAR, subtractDays);
	    instance.add(Calendar.YEAR, subtractYear);
	    SimpleDateFormat isoFormat = new SimpleDateFormat(DATE_FORMAT);
	    String stringDate = isoFormat.format(instance.getTime());
	    return stringDate;
	}

	private HashMap<LocalDate,LocalDate> getWeeksDate (String startDate, String endDate){
		Map<LocalDate,LocalDate> weekDates = new HashMap<LocalDate,LocalDate>();
		
		LocalDate startDateLocalDate = LocalDate.parse(startDate);
		LocalDate endDateLocalDate = LocalDate.parse(endDate);
		
		LocalDate startWeek = startDateLocalDate.with(previousOrSame(MONDAY));
		LocalDate endWeek = startDateLocalDate.with(nextOrSame(SUNDAY));
		
		// Loop until we surpass end date
		while(startWeek.isBefore(endDateLocalDate)) {
		   weekDates.put(startWeek,endWeek);
		   startWeek = startWeek.plusWeeks(1);
		   endWeek = endWeek.plusWeeks(1);
		}
		return (HashMap<LocalDate, LocalDate>) weekDates;
	}
}
