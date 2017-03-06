import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class SiteVisit {
	private final static Set<String> SITEVISITNONEMPTYFIELDS = new HashSet<String> (Arrays.asList("type","verb","key","eventTime","customerId"));
	public Map<String, String> fields = new HashMap<String, String>();
	public Map<String, String> tags = new HashMap<String, String>();

	public SiteVisit() {
		// TODO Auto-generated constructor stub
		this.fields.put("type", "Site_Visit");
	}
	
	
	
	
	public boolean validateFields(JSONObject jsonObject){

		this.fields.put("verb", ValidateAndTransform.getJsonValue(jsonObject, "verb"));
		this.fields.put("key", ValidateAndTransform.getJsonValue(jsonObject, "key"));
		this.fields.put("eventTime", ValidateAndTransform.getJsonValue(jsonObject, "event_time"));
		this.fields.put("customerId", ValidateAndTransform.getJsonValue(jsonObject, "customer_id"));
		this.fields.put("tags", ValidateAndTransform.getJsonValue(jsonObject, "tags"));

		for (String key : SITEVISITNONEMPTYFIELDS){
			if (ValidateAndTransform.isEmpty(this.fields.getOrDefault(key, null))){
//				System.out.println("Field "+key+" is empty for siteVisit");
//				System.out.println(jsonObject.toString());
				return false;
			}
		}
		
		if (!ValidateAndTransform.checkVerbValues(this.fields.getOrDefault("verb", null))){
			return false;
		}
		
		String eventTime = this.fields.getOrDefault("eventTime", null);
		if (ValidateAndTransform.isDateTimeValid(eventTime)){
			this.fields.put("eventTime", ValidateAndTransform.transformEventTime(eventTime));
		} else {
			return false;
		}
		
		return true;	
	}
}