package birdFlockBenchmarks;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class birdDisjointSetTest extends ApplicationTest {

    private Button button;
    private int [] testDisjointSet = {3,5,7,99,3,2,6,2,687,0,54,7,2,3,6};
    
    

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     */
    
    @Override
    public void start(Stage stage) {
    	
    	Image testImage = new Image((new File("./src/main/imageFolder/smallbird.png").toURI().toString()));
    	MainController.blackAndWhite(testImage);
    	MainController.makeBirdSet(testImage);
    	System.out.println(MainController.makeBirdSet(testImage).length);
    	MainController.checkForAdjacentBlackPixels2(MainController.blackAndWhite(testImage));
        stage.setScene(new Scene(new StackPane(), 100, 100));
        stage.show();
    }

    
    
    @Test
    public void testMakeBirdSet() {
    	Image testImage = new Image((new File("./src/main/imageFolder/smallbird.png").toURI().toString()));
    	assertEquals(true, MainController.makeBirdSet(testImage).length == 216);
    	
    }
    
    @Test
    public void testFind() {
    	
    	
    	
    }
    
    @Test 
    public void testUnion() {
    	MainController.birdUnion(testDisjointSet, 3,7);
    	
    	
    	
    }
    
    
}