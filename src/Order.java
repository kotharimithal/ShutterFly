import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public class Order {
	private final static Set<String> ORDERNONEMPTYFIELDS = new HashSet<String> (Arrays.asList("type","verb","key","eventTime","customerId","totalAmount"));
	public Map<String, String> fields = new HashMap<String, String>();

	public Order() {
		// TODO Auto-generated constructor stub
		this.fields.put("type", "Order");
	}
	
	public boolean validateFields(JSONObject jsonObject){

		this.fields.put("verb",ValidateAndTransform.getJsonValue(jsonObject, "verb"));
		this.fields.put("key", ValidateAndTransform.getJsonValue(jsonObject, "key"));
		this.fields.put("eventTime", ValidateAndTransform.getJsonValue(jsonObject, "event_time"));
		this.fields.put("customerId", ValidateAndTransform.getJsonValue(jsonObject, "customer_id"));
		this.fields.put("totalAmount", ValidateAndTransform.getJsonValue(jsonObject, "total_amount"));

		for (String key : ORDERNONEMPTYFIELDS){
			if (ValidateAndTransform.isEmpty(this.fields.getOrDefault(key, null))){
//				System.out.println("Field "+key+" is empty for Order");
//				System.out.println(jsonObject.toString());
				return false;
			}
		}

		if (!ValidateAndTransform.isDouble(this.fields.getOrDefault("totalAmount", null))){
			return false;
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