package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import DBConnection.DBHandler;

/**
 * Login Controller: Handles all log in and retrieves user data from MySql database to
 * authenticate users. 
 * 
 * @author alvinkwan
 *
 */
public class LoginController implements Initializable {

	@FXML
	private JFXTextField username; //user enters username

	@FXML
	private JFXPasswordField password; //user enters password

	@FXML
	private JFXButton signup; //sign up page

	@FXML
	private JFXButton signin; //signin

	@FXML
	private ImageView progress; 

	
	/**
	 * Database connection
	 */
	private Connection connection;
	private DBHandler handler;
	private PreparedStatement preparedStatement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		progress.setVisible(false);
		username.setStyle("-fx-text-inner-color: white;" + "-fx-prompt-text-fill: white;");
		password.setStyle("-fx-text-inner-color: white;" + "-fx-prompt-text-fill: white;");
		
		handler = new DBHandler(); //database handler
		
	}

	/**
	 * Authenticate user login with database based on user inputs for username and password
	 * 
	 * @param event
	 */
	@FXML
	void signinPressed(ActionEvent event) {
		//animation
		progress.setVisible(true);
		PauseTransition pause = new PauseTransition();
		pause.setDuration(Duration.seconds(3));
		pause.setOnFinished(e -> {
			System.out.println("Logged in");
		});
		pause.play();
		
		//retrieve data from database
		connection = handler.getConnection();
		String query = "SELECT * from users where names=? and password=?"; //select from username column and password column
		
		try {
			preparedStatement = connection.prepareStatement(query); 
			preparedStatement.setString(1, username.getText()); 
			preparedStatement.setString(2, password.getText());
			
			ResultSet result = preparedStatement.executeQuery(); //execute query
			int count = 0;
			
			while(result.next()) {
				count = count + 1; 
			}
			if(count == 1) { //login is successful
				//hide login and switch to home page after successful login
				System.out.println("Login Successful");
		    	signin.getScene().getWindow().hide(); //go to main page

				Stage tracker = new Stage();
		  
				try {
					Parent root = FXMLLoader.load(getClass().getResource("/FXML/Tracker.fxml"));
					Scene scene = new Scene(root);
					tracker.setScene(scene);
					tracker.show();
					tracker.setResizable(false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	
				
				
			}else { //login unsuccessful
				System.out.println("Username or Password is not correct");
				Alert alert = new Alert(Alert.AlertType.ERROR); //Alert user incorrect password or username
				alert.setHeaderText(null);
				alert.setContentText("Username or Password is not correct");
				alert.show();
				progress.setVisible(false);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			try {
				connection.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Signup page button. Move to signup page.
	 * @param event
	 * @throws IOException
	 */
    @FXML
    void signupPressed(ActionEvent event) throws IOException {
    	signin.getScene().getWindow().hide(); //hide current scene
    	
    	Stage signup = new Stage();
    	Parent root = FXMLLoader.load(getClass().getResource("/FXML/SignUp.fxml"));
    	Scene scene = new Scene(root);
    	signup.setScene(scene);
    	signup.show();
    	signup.setResizable(false);
    }

}
