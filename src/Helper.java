import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Helper {
	public static final String NEWLINE = "\n";
	
	public void writeToFile(File file, String textToWrite) 
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

	public String readInputFile(File inputFile)
	{
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder inputFileDataBuilder = new StringBuilder();
		try {
			fr = new FileReader(inputFile);
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
		    System.out.println("Input File Path is : "+inputFile);
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
