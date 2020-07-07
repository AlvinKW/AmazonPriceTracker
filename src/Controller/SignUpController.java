package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import DBConnection.DBHandler;

/**
 * Signup Controller: Handles all sign up events and user cannot use the same username twice.
 * @author alvinkwan
 *
 */
public class SignUpController implements Initializable {

	@FXML
	private JFXTextField username; //user enters username

	@FXML
	private JFXPasswordField password; //user enters password

	@FXML
	private JFXButton signup; //sign up after all criteria inputed

	@FXML
	private JFXButton cancel; //go back to login

	@FXML
	private ImageView progress; 
	
	
	//database connection
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
	 * Cancel sign up, go back to login page
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void cancelButtonPressed(ActionEvent event) throws IOException {
		signup.getScene().getWindow().hide();

		Stage signin = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
		Scene scene = new Scene(root);
		signin.setScene(scene);
		signin.show();
		signin.setResizable(false);

	}

	/**
	 * Signup for new users
	 * @param event
	 */
	@FXML
	void signupButtonPressed(ActionEvent event) {
		progress.setVisible(true);

		PauseTransition pause = new PauseTransition();
		pause.setDuration(Duration.seconds(3));
		pause.setOnFinished(e -> {
			System.out.println("signing up");
		});
		pause.play();
		
		//store user data
		String user = "INSERT INTO users(names, password)" + "VALUES(?,?)"; //inset user name and password to database
		
		//connect to database
		connection = handler.getConnection();
		Stage signin = new Stage();
		try {
			preparedStatement = connection.prepareStatement(user);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		try {
			//after successful sign up, bring user back to the login page
			preparedStatement.setString(1,username.getText());
			preparedStatement.setString(2,  password.getText());
			preparedStatement.executeUpdate(); //update tables in database
			signup.getScene().getWindow().hide();
			Parent root = FXMLLoader.load(getClass().getResource("/FXML/Login.fxml"));
			Scene scene = new Scene(root);
			signin.setScene(scene);
			signin.show();
			signin.setResizable(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
