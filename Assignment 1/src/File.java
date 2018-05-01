import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//import Assignment1.MyData;


public class File {
	ArrayList<MyData> data = new ArrayList<>();
	public ArrayList<MyData> readCSVFile(String filename) throws IOException{

		BufferedReader csvFile = new BufferedReader(new FileReader(filename));	
		String datarow = "";		
		String [] partsOfLine = null;
		datarow = csvFile.readLine();
		try{
			while(datarow != null){
				datarow = csvFile.readLine();
				//partsOfLine = datarow.split(",");
				partsOfLine = datarow.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				try{				
					String streetAddress = partsOfLine[12];
					String postalCode = partsOfLine[13];
					double previousLandValue = Double.parseDouble(partsOfLine[22]);
					double currentLandValue = Double.parseDouble(partsOfLine[19]);
					int yearBuilt = Integer.parseInt(partsOfLine[24]);
					String zone = partsOfLine[5];
					data.add(new MyData(streetAddress, postalCode, currentLandValue, previousLandValue, yearBuilt, zone));


				}catch(NumberFormatException e){
				}
			}	
		}catch(NullPointerException e){
		}

		csvFile.close();
		return data;
	}
}
