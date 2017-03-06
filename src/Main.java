import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.*;


public class Main {
	public static final Set<String> REQUIREDSET = new HashSet<String> (Arrays.asList("type","verb","key","event_time"));
	public static final File INPUTFILEPATH = new File(new File(".").getAbsolutePath(), "input/events.txt");
	public static final File REJECTFILEPATH = new File(new File(".").getAbsolutePath(), "output/rejects.txt");
	public static final String NEWLINE = "\n";
	
	public static void main(String[] args) 
	{
		processData();
	}
	
	private static <T> void processData() 
	{
		String inputFileData = readInputFile();
		ingestData(inputFileData);
	}

	private static <T> void ingestData(String eventsData) 
	{
		Database db = null;
		try {
			db = new Database();
			List<T> eventObjects = getListOfEvents(new JSONArray(eventsData));
			for (T event: eventObjects){
				db.processEvent(event);
			}
		} catch (Exception e) {
			writeToFile(REJECTFILEPATH, eventsData);
		} finally {
			db.closeConnection();
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> getListOfEvents(JSONArray jsonArray) {
		Object object = null;
		List<T> eventObjects = new ArrayList<T>();
		for(int index=0; index<jsonArray.length(); index++){
	        JSONObject obj = jsonArray.getJSONObject(index);
	        if (obj.keySet().containsAll(REQUIREDSET)){	 	
				object = getInstanceOfClass(obj);
				if (object != null)
					eventObjects.add((T)object);
	        } else {
	        	writeToFile(REJECTFILEPATH, obj.toString());
	        	System.out.println("Missing information for : "+obj.toString());
	        }
	    }
		return eventObjects;
	}

	public static String camelCase(String input) 
	{	    
		input = input.replace("_", " ");
		StringBuilder titleCase = new StringBuilder();
	    boolean nextTitleCase = true;
	    for (char c : input.toCharArray()) {
	        if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	        } else if (nextTitleCase) {
	            c = Character.toTitleCase(c);
	            nextTitleCase = false;
	        }
	        titleCase.append(c);
	    }
	    return titleCase.toString().replaceAll(" ","");
	}
	
	private static Object getInstanceOfClass(JSONObject obj) 
	{
        Class<?> c = getClass(camelCase(obj.getString("type").toLowerCase()));
        Constructor<?> constructor = getConstructor(c);
        Object object = getInstance(constructor);
        Method method = getMethod(object);
        boolean returnValue = false;
        
        // If error getting class, constructor, instance or finding method then reject that entry and write it to rejects file.
        if (c == null || constructor == null || object == null || method == null){
        	writeToFile(REJECTFILEPATH, obj.toString());
        }
        
        try {
        	returnValue = (boolean) method.invoke(object, obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Error while invoking method validateFields");
		}
		if (returnValue){
			return object;
		}
		return null;
	}

	private static Method getMethod(Object object) 
	{
		if (object == null)
			return null;
		Method method = null;
		try {
			if (object != null)
				method = object.getClass().getMethod("validateFields", JSONObject.class);
		} catch (NoSuchMethodException | SecurityException e1) {					
			System.out.println("Error Occurred : Method Validate Data Not Found for class "+object.toString());
		}
		return method;
	}

	private static Object getInstance(Constructor<?> constructor) 
	{
        if (constructor == null)
        	return null;
		Object object = null;
		try {
			object = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException e) {
			System.out.println("Error Occurred : Instantiation Exception");
		}
		return object;
	}

	private static Constructor<?> getConstructor(Class<?> c) 
	{
		if (c == null)
			return null;
		Constructor<?> constructor = null;
		try {
			constructor = c.getConstructor();
		} catch (NoSuchMethodException | SecurityException e1) {					
			System.out.println("Error Occurred : Constructor Not Found for class "+ c);
			//e1.printStackTrace();
		}
		return constructor;
	}
	

	private static Class<?> getClass(String className) 
	{
		if (className == null)
			return null;
		Class<?> classObject = null;
		try {
			classObject = Class.forName(className);
		} catch (ClassNotFoundException | JSONException e2) {					
			System.out.println("Error Occurred : Class "+ className +" Not Found Exception");
		}
		return classObject;
	}

	private static void writeToFile(File file, String textToWrite) 
	{
		// TODO Auto-generated method stub
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(textToWrite+NEWLINE);
		} catch (IOException e) {
			System.out.println("Error Occurred : IOException while writing to "+ file.getName() +" file");
//			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				System.out.println("Error Occurred : IOException while closing file in writeToFile");
//				ex.printStackTrace();
			}
		}
	}

	private static String readInputFile()
	{
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder inputFileDataBuilder = new StringBuilder();
		try {
			fr = new FileReader(INPUTFILEPATH);
		    br = new BufferedReader(fr);
		    try {
		        String inputFileLine;
		        while ( (inputFileLine = br.readLine()) != null ) {
		            inputFileDataBuilder.append(inputFileLine);
		        }
		    } catch (IOException e) {
		    	System.out.println("Error occurred : IOException while reading file");
		        //e.printStackTrace();
		    }
		} catch (FileNotFoundException e) {
		    System.out.println("Error : Input File Not Found");
		    System.out.println("Input File Path is : "+INPUTFILEPATH);
		    //e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				System.out.println("Error occurred : IOException while closing file in readInputFile");
			}
		}
		return inputFileDataBuilder.toString();
	}

}
