

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.Scene;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Assignment1 extends Application{
	private String filename = "property_tax_report.csv";
	ArrayList<MyData> data = new ArrayList<>();
	HashMap<String, List<String>> arrayOfStreetPostalCodes = new HashMap<>();
	HashMap<String, Double> postalCodeValueChange = new HashMap<>();
	HashMap<String, Double> mapOfPostalCodeAge = new HashMap<>();
	HashMap<String, Double> mapOfPostalCodeLandValue = new HashMap<>();
	Map<Double, Integer> mp = new HashMap<Double, Integer>();
	ArrayList<Integer> x = new ArrayList<Integer>();
	ArrayList<Double> y = new ArrayList<Double>();
	List<Double> count = new ArrayList<>();
	double maxPrice;

	public void start(Stage s) throws IOException {
		File readFile = new File();
		data = readFile.readCSVFile(filename);
		getStreetAndPostalCode();

		BorderPane border = new BorderPane();
		VBox v1 = new VBox();
		VBox v2 = new VBox();
		VBox v3 = new VBox();

		v1.getChildren().addAll(getLabelForAPV(), getLabelForAHA(), getLabelForTHV());		

		getCount();
		for (Double num : count) {
			if(mp.keySet().contains(num)){
				mp.put(num, mp.get(num)+1);
			}else{
				mp.put(num, 1);
			}
		}	
		x.addAll(mp.values());
		y.addAll(mp.keySet());
		ArrayList<Double> pricemax = new ArrayList<>();
		for(int i = 0; i < data.size(); i++){
			pricemax.add(data.get(i).getCurrentLandValue());
		}
		maxPrice = Collections.max(pricemax);
		Label max = new Label("\tMaximum increments of 25,000: \n\t" + Collections.max(y));//needs to be the max value that has increments of 25000
		max.setFont(Font.font("Georgia", FontWeight.BOLD, 18));

		Label min = new Label("\tMinimum increments of 25,000: \n\t" + Collections.min(y));//min value that has increments of 25000
		min.setFont(Font.font("Georgia", FontWeight.BOLD, 18));

		v2.getChildren().addAll(getLineChart());		
		v3.getChildren().addAll(min, max);		

		border.setLeft(v1);
		border.setRight(getPieChart());
		border.setBottom(v2);
		border.setCenter(v3);
		border.setStyle("-fx-background-color:white");

		Scene scene = new Scene(border, 1550, 995);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Property Tax for Vancouver");
		stage.show();
	}
	private PieChart getPieChart(){

		int numofOneFamily = 0;
		for(int i = 0; i <= data.size()-1; i++){
			String z = data.get(i).getZones();
			if(z.equals("One Family Dwelling")){
				numofOneFamily++;
			}
		}		
		int numofMultipleFamily = 0;
		for(int i = 0; i <= data.size()-1; i++){
			String z = data.get(i).getZones();
			if(z.equals("Multiple Family Dwelling")){
				numofMultipleFamily++;
			}
		}
		int numofCommercial = 0;		
		for(int i = 0; i <= data.size()-1; i++){
			String z = data.get(i).getZones();
			if(z.equals("Commercial")){				
				numofCommercial++;
			}
		}
		ObservableList<PieChart.Data> pieChartData =
				FXCollections.observableArrayList(
						new PieChart.Data("One Family", numofOneFamily),
						new PieChart.Data("Mulitple Family", numofMultipleFamily),
						new PieChart.Data("Commerical", numofCommercial));
		final PieChart pieChart = new PieChart(pieChartData);
		pieChart.setTitle("Type of Dwelling");

		return pieChart;
	}
	private void getCount(){
		for(int i = 0; i< data.size(); i++){
			count.add(data.get(i).getCurrentLandValue()/25000);
		}
	}
	private LineChart getLineChart(){
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Number of increments of 25,000");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);

		int bins [] = new int[1+((int)maxPrice/25000)];

		lineChart.setTitle("House Value Histogram");
		XYChart.Series series = new XYChart.Series();
		for(int i = 0; i < data.size(); i++){			
			int chart = (int)data.get(i).getCurrentLandValue()/25000;	
			bins[chart]++;			
		}	
		for(int i = 0; i <= maxPrice; i++){//for(int i : bins){
			series.getData().add(new XYChart.Data(i, bins[i]));
			if(bins[i] == 0){
				lineChart.getData().add(series);
				series.setName("Number of houses in increments of 25,000");	
				return lineChart;
			}
		}
		return lineChart;		
	}

	private Label getLabelForAPV() throws IOException {
		double avgPropertyValue = 0;
		for(int i = 0; i <= data.size()-1; i++){
			avgPropertyValue += data.get(i).getCurrentLandValue();
		}

		double sd = Math.sqrt(differencesOfSquares(avgPropertyValue, getMean(avgPropertyValue)) / data.size());
		avgPropertyValue = avgPropertyValue/data.size();
		Label apv = new Label("Average Property Value: \n" + avgPropertyValue + "\n\nStandard Deviation for avg property value: \n" + sd);//TODO standard deviation for entire data set
		apv.setStyle("-fx-padding:5px; -fx-margin:5px;");		
		apv.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
		apv.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY)){
				if(e.getClickCount() == 2){
					BorderPane bp = new BorderPane();

					HashMap<String, Double> map = new HashMap();
					//double addedLandValue = 0;
					for(int i = 0; i < data.size(); i++){
						String street = data.get(i).getStreetaddress();
						double landvalue = data.get(i).getCurrentLandValue();				 
						if(map.containsKey(street)){
							double add = map.get(street);
							add += landvalue;
							map.put(street, add);
						}else{
							map.put(street, landvalue);
						}
					}
					TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<>("Street");
					column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {						
							return new SimpleStringProperty(p.getValue().getKey());
						}
					});
					TableColumn<Map.Entry<String, Double>, String> column2 = new TableColumn<>("Current Land Value");
					column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {							
							return new SimpleStringProperty(String.valueOf(p.getValue().getValue()));
						}
					});
					ObservableList<Entry<String, Double>> items = FXCollections.observableArrayList(map.entrySet());
					final TableView<Entry<String, Double>> table = new TableView<>(items);
					table.getColumns().setAll(column1, column2);
					table.setOnMousePressed(event->{
						if(event.getButton().equals(MouseButton.PRIMARY)){
							if(event.getClickCount() == 2){

								String street = String.valueOf(table.getSelectionModel().getSelectedItem());
								String[] seperate =  street.split("=");
								String streetClicked = seperate[0];
								Text centerText = new Text(streetClicked + " calculation by postal code\n------>");
								bp.setCenter(centerText);
								getListOfPostalCodeAndLandValue();

								TreeMap<String, Double> map2 = new TreeMap<>();
								ArrayList<String> arrays = (ArrayList<String>) arrayOfStreetPostalCodes.get(streetClicked);
								for(int i = 0; i < arrays.size(); i++){
									String postal = arrays.get(i);
									double landValue = mapOfPostalCodeLandValue.get(postal);
									map2.put(postal, landValue);							
								}
								TableColumn<Map.Entry<String, Double>, String> columns1 = new TableColumn<>("Postal Code");
								columns1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {						
										return new SimpleStringProperty(p.getValue().getKey());
									}
								});
								TableColumn<Map.Entry<String, Double>, String> columns2 = new TableColumn<>("Current Land Value");
								columns2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {							
										return new SimpleStringProperty(String.valueOf(p.getValue().getValue()));
									}
								});
								ObservableList<Entry<String, Double>> items2 = FXCollections.observableArrayList(map2.entrySet());
								final TableView<Entry<String, Double>> table2 = new TableView<>(items2);
								table2.getColumns().setAll(columns1, columns2);
								bp.setRight(table2);
							}
						}
					});
					bp.setLeft(table);

					Stage avaStage = new Stage();
					Scene avaScene = new Scene(bp, 1050, 750);						
					avaStage.setScene(avaScene);
					avaStage.show();						
				}
			}						
		});
		return apv;

	}
	private Label getLabelForAHA(){
		int avgHouseAge = 0;
		for(int i = 1000; i < data.size(); i++){
			int getage = (int) (2015 - data.get(i).getYearBuilt());			
			avgHouseAge += getage;			
		}
		double sd = Math.sqrt(differencesOfSquares(avgHouseAge, getMean(avgHouseAge)) / data.size());
		avgHouseAge = avgHouseAge/data.size();
		Label aha = new Label("\n\nAverage House Age: \n" + avgHouseAge + "\n\nStandard Deviation For House Age: \n" + sd);
		aha.setStyle("-fx-padding:5px; -fx-margin:5px;");
		aha.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
		aha.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY)){
				if(e.getClickCount() == 2){
					BorderPane bp = new BorderPane();

					HashMap<String, Double> map = new HashMap();
					double calcAvgAge = 0;
					for(int i = 0; i < data.size(); i++){
						String street = data.get(i).getStreetaddress();
						double year = data.get(i).getYearBuilt();	
						double age = 2015-year;
						if(map.containsKey(street)){
							double avgage = map.get(street);
							avgage += age;
							avgage = avgage/map.size();
						}else{
							map.put(street, age);
						}
					}
					TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<>("Street");
					column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {						
							return new SimpleStringProperty(p.getValue().getKey());
						}
					});
					TableColumn<Map.Entry<String, Double>, String> column2 = new TableColumn<>("Average House Age");
					column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {							
							return new SimpleStringProperty(String.valueOf(p.getValue().getValue()));
						}
					});
					ObservableList<Entry<String, Double>> items = FXCollections.observableArrayList(map.entrySet());
					final TableView<Entry<String, Double>> table = new TableView<>(items);
					table.getColumns().setAll(column1, column2);					 
					table.setOnMousePressed(event -> {
						if(event.getButton().equals(MouseButton.PRIMARY)){
							if(event.getClickCount() == 2){
								String street = String.valueOf(table.getSelectionModel().getSelectedItem());
								String[] seperate =  street.split("=");
								String s = seperate[0];
								Text centerText = new Text(s + " calculation by postal code\n------>");
								bp.setCenter(centerText);							
								getListOfPostalCodeAndAge();

								TreeMap<String, Double> map2 = new TreeMap<>();
								ArrayList<String> arrays = (ArrayList<String>) arrayOfStreetPostalCodes.get(s); //arraylist of postal code for street clicked

								for(int i = 0; i < arrays.size(); i++){
									String postalCode = arrays.get(i);
									double age = mapOfPostalCodeAge.get(postalCode);									
									map2.put(postalCode, age);

								}
								TableColumn<Map.Entry<String, Double>, String> columns1 = new TableColumn<>("Postal Code");
								columns1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {						
										return new SimpleStringProperty(p.getValue().getKey());
									}
								});
								TableColumn<Map.Entry<String, Double>, String> columns2 = new TableColumn<>("Average House Age");
								columns2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {							
										return new SimpleStringProperty(String.valueOf(p.getValue().getValue()));
									}
								});
								ObservableList<Entry<String, Double>> items2 = FXCollections.observableArrayList(map2.entrySet());
								final TableView<Entry<String, Double>> table2 = new TableView<>(items2);
								table2.getColumns().setAll(columns1, columns2);
								bp.setRight(table2);
							}
						}
					});
					bp.setLeft(table);

					Stage avaStage = new Stage();
					Scene avaScene = new Scene(bp, 1050, 750);						
					avaStage.setScene(avaScene);
					avaStage.show();
				}
			}
		});
		return aha;
	}	
	private double getMean(double m){
		double mean = m/data.size();
		return mean;

	}
	private double differencesOfSquares(double list, double mean) {
		double sum = 0;		
		double calcNum = list - mean;
		sum += (calcNum)*(calcNum);

		return sum;		
	}
	private Label getLabelForTHV(){
		double change = 0;		
		for(int i = 0; i <= data.size()-1; i++){			
			change += data.get(i).getTotalLandChange();			
		}
		Label totalHouseValueChange = new Label("\n\nTotal House Value Change: \n" + change);//TODO for entire data set (current land value-previous land value)
		totalHouseValueChange.setStyle("-fx-padding:5px; -fx-margin:5px; ");
		totalHouseValueChange.setFont(Font.font("Georgia", FontWeight.BOLD, 24));
		totalHouseValueChange.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY)){
				if(e.getClickCount() == 2){
					BorderPane bp = new BorderPane();	

					TreeMap<String, Double> map = new TreeMap<>();				
					for(int i = 0; i < data.size(); i++){
						String street = data.get(i).getStreetaddress();
						double landValueChange = data.get(i).getTotalLandChange();					 
						map.put(street, landValueChange);
					}
					TableColumn<Map.Entry<String, Double>, String> column1 = new TableColumn<>("Street");
					column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {						
							return new SimpleStringProperty(p.getValue().getKey());
						}
					});
					TableColumn<Map.Entry<String, Double>, String> column2 = new TableColumn<>("Land Value Change");
					column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p) {							
							return new SimpleStringProperty(String.valueOf(p.getValue().getValue()));
						}
					});
					ObservableList<Entry<String, Double>> items = FXCollections.observableArrayList(map.entrySet());
					final TableView<Entry<String, Double>> table = new TableView<>(items);
					table.getColumns().setAll(column1, column2);					 
					table.setOnMousePressed(event -> {
						if(event.getButton().equals(MouseButton.PRIMARY)){
							if(event.getClickCount() == 2){

								String street = String.valueOf(table.getSelectionModel().getSelectedItem());
								String[] seperate =  street.split("=");
								String s = seperate[0];
								Text centerText = new Text(s + " calculation by postal code\n\t------>");
								bp.setCenter(centerText);
								getPostalCodeAndLandValueChange();

								TreeMap<String, Double> map2 = new TreeMap<>();								
								ArrayList<String> arrays = (ArrayList<String>) arrayOfStreetPostalCodes.get(s); //arraylist of postal code for street clicked
								for(int i = 0; i < arrays.size(); i++){
									String postalCode = arrays.get(i);
									double valueChange = postalCodeValueChange.get(postalCode);
									map2.put(postalCode, valueChange);
								}
								TableColumn<Map.Entry<String, Double>, String> columns1 = new TableColumn<>("Postal Code");
								columns1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p2) {						
										return new SimpleStringProperty(p2.getValue().getKey());
									}
								});
								TableColumn<Map.Entry<String, Double>, String> columns2 = new TableColumn<>("Land Value Change");
								columns2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Double>, String>, ObservableValue<String>>() {
									@Override
									public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Double>, String> p2) {							
										return new SimpleStringProperty(String.valueOf(p2.getValue().getValue()));
									}
								});
								ObservableList<Entry<String, Double>> items2 = FXCollections.observableArrayList(map2.entrySet());
								final TableView<Entry<String, Double>> table2 = new TableView<>(items2);
								table2.getColumns().setAll(columns1, columns2);
								bp.setRight(table2);
							}
						}
					});									
					bp.setLeft(table);

					Stage avaStage = new Stage();
					Scene avaScene = new Scene(bp, 1050, 750);						
					avaStage.setScene(avaScene);
					avaStage.show();
				}
			}			
		});
		return totalHouseValueChange;
	}	

	public void getStreetAndPostalCode(){
		List<String> listOfPostalCode;
		for(int i = 0; i < data.size(); i++){
			String street = data.get(i).getStreetaddress();
			String postalCode = data.get(i).getPostalcode();
			if(arrayOfStreetPostalCodes.containsKey(street)){				
				listOfPostalCode = arrayOfStreetPostalCodes.get(street);
				listOfPostalCode.add(postalCode);
			}
			else{
				listOfPostalCode = new ArrayList<>();
				listOfPostalCode.add(postalCode);
				arrayOfStreetPostalCodes.put(street, listOfPostalCode);
			}
		}
	}	
	public void getPostalCodeAndLandValueChange(){
		for(int i = 0; i < data.size(); i++){
			String postalCode = data.get(i).getPostalcode();
			double valueChange = data.get(i).getTotalLandChange();
			if(postalCodeValueChange.keySet().contains(postalCode)){
				double newValueChange = postalCodeValueChange.get(postalCode);
				valueChange += newValueChange;
				postalCodeValueChange.put(postalCode, valueChange);
			}else{
				postalCodeValueChange.put(postalCode, valueChange);
			}
		}		
	}
	public void getListOfPostalCodeAndAge() {
		HashMap<String, ArrayList<Double>> mapAvgAge = new HashMap<>();
		ArrayList<Double> ages1;
		double counter = 0;
		double ages = 0;
		double avgAge = 0;

		for(int i = 0; i < data.size(); i++){
			String postalCode = data.get(i).getPostalcode();
			double yearBuilt = 2015-data.get(i).getYearBuilt();
			if(mapOfPostalCodeAge.containsKey(postalCode)){
				try{
					ages = mapOfPostalCodeAge.get(postalCode);
					ages += yearBuilt;
					ages1 = mapAvgAge.get(postalCode);
					ages1.add(yearBuilt);
					for(int j = 0; j < ages1.size(); j++){
						counter++;
					}
					avgAge = ages/counter;
					counter = 0;
					mapOfPostalCodeAge.put(postalCode, avgAge);
				}catch(NullPointerException e){

				}
			}else{
				ages1 = new ArrayList();
				ages1.add(yearBuilt);
				mapAvgAge.put(postalCode, ages1);
				mapOfPostalCodeAge.put(postalCode, yearBuilt);
			}
		}	

	}
	public void getListOfPostalCodeAndLandValue(){
		HashMap<String, ArrayList<Double>> mapAvgLandValue = new HashMap<>();
		ArrayList<Double> landValue1;
		double counter = 0;
		double avgLandValue = 0;
		double landValue;
		for(int i = 0; i < data.size(); i++){
			String postalCode = data.get(i).getPostalcode();
			double currentLandValue = data.get(i).getCurrentLandValue();
			if(mapOfPostalCodeLandValue.keySet().contains(postalCode)){
				try{
					landValue = mapOfPostalCodeLandValue.get(postalCode);
					landValue += currentLandValue;
					landValue1 = mapAvgLandValue.get(postalCode);
					landValue1.add(currentLandValue);
					for(int j = 0; j < landValue1.size(); j++){
						counter++;
					}
					avgLandValue = landValue/counter;
					counter = 0;
					mapOfPostalCodeLandValue.put(postalCode, avgLandValue);
				}catch(NullPointerException e){

				}
			}else{
				landValue1 = new ArrayList();
				landValue1.add(currentLandValue);
				mapAvgLandValue.put(postalCode, landValue1);
				mapOfPostalCodeLandValue.put(postalCode, currentLandValue);
			}
		}
	}
	public static void main(String[] args) {
		launch(args);

	}

}
