package birdFlockBenchmarks;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



/**
 * 
 * @author Andrew Brennan
 * @date   19/3/19
 *
 */


public class Main extends Application{
	
	Stage primaryStage;
	Scene scene1, ImageMod;
	
	
	
	public static void main(String[] args) {
		launch(args);
		
	}
	
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		
		Parent root = FXMLLoader.load(getClass().getResource("/birdFlockBenchmarks/Main.fxml"));
		scene1 = new Scene(root);
		primaryStage.setScene(scene1);
		primaryStage.show();
		
	
	
		
		

		
	}
	
	
	
}
