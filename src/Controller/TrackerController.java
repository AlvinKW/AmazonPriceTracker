package Controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.ItemTableModel;
import DBConnection.DBHandler;

/**
 * Tracker Controller: Handles all data retrieving from the web and database, as well as 
 * inserting and deleting data from the database
 * @author alvinkwan
 *
 */
public class TrackerController implements Initializable {

	@FXML
	private TableView<ItemTableModel> table; //table view to store item name and price

	@FXML
	private TableColumn<ItemTableModel, String> itemColumn; //item column

	@FXML
	private TableColumn<ItemTableModel, String> priceColumn; //price column

	@FXML
	private TextField linkTextBox; //text box for website link

	// ItemTableModel item = new ItemTableModel();

	
	/**
	 * Database Connection
	 */
	private Connection connection;
	private DBHandler handler;
	private PreparedStatement preparedStatement;
	private ResultSet result = null;
	private ObservableList<ItemTableModel> data; //observable list object for table view

	//observable arraylist to store list of items from database to populate tableview
	ObservableList<ItemTableModel> observableList = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		handler = new DBHandler();
		connection = handler.getConnection();
		data = FXCollections.observableArrayList(); 
		setCellTable(); //set cell tables in table view 
		loadDataFromDatabase(); //load product data from database

	}

	/**
	 * Add item to database and update UI in tableview
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void addButtonPressed(ActionEvent event) throws IOException {
		String name = getItem().getItem(); //item name
		String price = getItem().getPrice(); //price
		String link = getItem().getLink(); //link
		System.out.println(name);
		String item = "INSERT INTO item(items,prices,links)" + "VALUES(?,?,?)"; //Insert to MySql

		connection = handler.getConnection();

		try {
			preparedStatement = connection.prepareStatement(item);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			table.getItems().clear(); //clear tableview
			preparedStatement.setString(1, name); //add name
			preparedStatement.setString(2, price); //add price
			preparedStatement.setString(3, link); //add link
			preparedStatement.executeUpdate(); //update table in database
			loadDataFromDatabase(); //load data from database into the tableview

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Delete button: delete the currently selected item from table view
	 * @param event
	 */
	@FXML
	void deleteButtonPressed(ActionEvent event) {
		String selected = table.getSelectionModel().getSelectedItem().getPrice(); //get the price of selected item
		//System.out.println(selected);
		connection = handler.getConnection();

		String query = "DELETE FROM item WHERE prices=" + "'"+ selected + "'"; //delete in table from database where prices matches
		if (selected != null) { //make sure it is not null
			try {
				preparedStatement = connection.prepareStatement(query); 
				preparedStatement.executeUpdate(); //update table in database
				table.getItems().clear(); //clear current table
				loadDataFromDatabase(); //re-populate the table with new data
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Opens the currently selected item in default web browser
	 * @param event
	 */
	@FXML
	void goButtonPressed(ActionEvent event) {
		
		String selected = table.getSelectionModel().getSelectedItem().getPrice(); //get price of currently selected item
		try {
			preparedStatement = connection.prepareStatement("Select * from item");
			result = preparedStatement.executeQuery();
			while(result.next()) {
				if(selected.equals(result.getString(3))) { //if prices matches with the selected price...
					//System.out.println("Selected: " + selected);
					//System.out.println(result.getString(3));
					selected = result.getString(4).toString();
					openUrlInBrowser(selected); //open url with the selected price's link
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(selected);
		//openUrlInBrowser(selected);
	}

	/**
	 * goButtonPressed helper function for opening url in default browser
	 * @param url
	 */
	private void openUrlInBrowser(String url) {
	   try {
		   Desktop d = java.awt.Desktop.getDesktop();
		   URI uri = new URI(url);
		   d.browse(uri);
	   }catch (Exception e){
		   e.printStackTrace();
	   }
	}
	
	/**
	 * Get item by scraping the website for their css selectors
	 * 
	 * @return item and price as a ItemTableModel object
	 * @throws IOException
	 */
	public ItemTableModel getItem() throws IOException {
		ItemTableModel node;
		String item, price;
		String webtitle = "title"; //css selector for name of product
		String webprice = "#priceblock_ourprice"; //css selector for price
		String url; 
		if (linkTextBox != null) { //while text box is not empty...
			url = linkTextBox.getText();
			Document page = Jsoup.connect(url).get(); //using jsoup to connect to url
			Elements titleElement = page.select(webtitle); //select title of product
			Elements priceElement = page.select(webprice); //select price of product

			item = titleElement.text(); //get item name from selection on website
			price = priceElement.text(); //get price from selection

			node = new ItemTableModel(item, price, url); //create a new ItemTableModel object
			return node;
		} else {
			return null;
		}

	}

	/**
	 * Set item cells item and price
	 */
	public void setCellTable() {
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
	}

	/**
	 * Populate the table view with product data from database
	 */
	private void loadDataFromDatabase() {
		try {
			preparedStatement = connection.prepareStatement("Select * from item"); //Select all from item table
			result = preparedStatement.executeQuery(); //execute query
			while (result.next()) {
				//System.out.println(result.getString(4)); 
				data.add(new ItemTableModel(result.getString(2), result.getString(3))); //add to Observable object the item name and price

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setItems(data); //set table view

	}
}
