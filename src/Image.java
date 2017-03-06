import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class Image {
	private final static Set<String> IMAGENONEMPTYFIELDS = new HashSet<String> (Arrays.asList("type","verb","key","eventTime","customerId"));
	public Map<String, String> fields = new HashMap<String, String>();

	public Image() {
		// TODO Auto-generated constructor stub
		this.fields.put("type", "Image");
	}
	
	public boolean validateFields(JSONObject jsonObject){

		this.fields.put("verb", ValidateAndTransform.getJsonValue(jsonObject, "verb"));
		this.fields.put("key", ValidateAndTransform.getJsonValue(jsonObject, "key"));
		this.fields.put("eventTime", ValidateAndTransform.getJsonValue(jsonObject, "event_time"));
		this.fields.put("customerId", ValidateAndTransform.getJsonValue(jsonObject, "customer_id"));
		this.fields.put("cameraMake", ValidateAndTransform.getJsonValue(jsonObject, "camera_make"));
		this.fields.put("cameraModel", ValidateAndTransform.getJsonValue(jsonObject, "camera_model"));

		for (String key : IMAGENONEMPTYFIELDS){
			if (ValidateAndTransform.isEmpty(this.fields.getOrDefault(key, null))){
//				System.out.println("Field "+key+" is empty for Image");
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