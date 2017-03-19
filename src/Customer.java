import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class Customer {
	private final static Set<String> CUSTOMERNONEMPTYFIELDS = new HashSet<String> (Arrays.asList("type","verb","key","eventTime"));
	public Map<String, String> fields = new HashMap<String, String>();

	public Customer() {
		// TODO Auto-generated constructor stub
		this.fields.put("type", "Customer");
	}
	
	public boolean validateFields(JSONObject jsonObject){
		this.fields.put("verb", ValidateAndTransform.getJsonValue(jsonObject, "verb"));
		this.fields.put("key", ValidateAndTransform.getJsonValue(jsonObject, "key"));
		this.fields.put("eventTime", ValidateAndTransform.getJsonValue(jsonObject, "event_time"));
		this.fields.put("lastName", ValidateAndTransform.getJsonValue(jsonObject, "last_name"));
		this.fields.put("adrCity", ValidateAndTransform.getJsonValue(jsonObject, "adr_city"));
		this.fields.put("adrState", ValidateAndTransform.getJsonValue(jsonObject, "adr_state"));
		
		for (String key : CUSTOMERNONEMPTYFIELDS){
			if (ValidateAndTransform.isEmpty(this.fields.getOrDefault(key, null))){
				System.out.println(key +" is empty for customer object");
				return false;
			}
		}
		
		if (!ValidateAndTransform.checkVerbValues(this.fields.getOrDefault("verb", null))){
			System.out.println("Verb is empty for customer object");
			return false;
		}
		
		String eventTime = this.fields.getOrDefault("eventTime", null);
		if (ValidateAndTransform.isDateTimeValid(eventTime)){
			this.fields.put("eventTime", ValidateAndTransform.transformEventTime(eventTime));
		} else {
			System.out.println("Error parsing event_time for customer");
			return false;
		}

		return true;	
	}
}