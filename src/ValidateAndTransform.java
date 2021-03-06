import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

public class ValidateAndTransform {
	
	private static final Set<String> VERBVALUES = new HashSet<String>(Arrays.asList("new","update","upload"));
	
	public static boolean isEmpty(String value){
		if (value != null && value.trim().length() > 0){
			return false;
		}
		return true;
	}

	public static boolean isDateTimeValid(String dateTime) {
		if(dateTime == null)
			return false;
		
		String DATE_FORMAT = "yyyy-MM-dd";
		String TIME_FORMAT = "HH:mm:ss";
		DateFormat df = null;
	        try {
	        	dateTime = dateTime.replace("T", " ");
	        	String[] dateAndTime = dateTime.split(" ");	
	        	if (dateAndTime.length != 2){
//	        		System.out.println(dateAndTime.toString());
	        		System.out.println("Error processing dateTime "+dateTime);
	        		return false;
	        	}
	            df = new SimpleDateFormat(DATE_FORMAT);
	            df.setLenient(false);
	            df.parse(dateAndTime[0]);
	            
	            df = new SimpleDateFormat(TIME_FORMAT);
	            df.setLenient(false);
	            df.parse(dateAndTime[1]);
	            return true;
	        } catch (ParseException e) {
	        	
	        	System.out.println("Error processing dateTime "+dateTime);
	        	System.out.println(e.getMessage());
	        	return false;
	        }
	}
	
	public static String transformEventTime(String eventTime) {
		// TODO Auto-generated method stub
		return eventTime.replaceAll("[^0-9-:.]", " ").trim();
	}

	public static boolean checkVerbValues(String verb) {
		// TODO Auto-generated method stub
		if (verb!= null && VERBVALUES.contains(verb.toLowerCase())){
			return true;
		}
		return false;
	}
	
	static String getJsonValue(JSONObject jsonObject, String key) {
		// TODO Auto-generated method stub
		if (jsonObject.has(key)){
			return jsonObject.get(key).toString();
		}
		return null;
	}

	public static boolean isDouble(String stringToCheck) {
		try {
			String totalAmount = stringToCheck.replaceAll("[^0-9.]", "").trim();
			Double.valueOf(totalAmount);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
}
