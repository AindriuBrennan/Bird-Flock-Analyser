package birdFlockBenchmarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
	int[] birdDisjointSet;
	int[] findNeighbouringBlacks;
	int[][] coords;
	int root;
	String str = "";
	static int count;
	static ArrayList<Integer> parentRoot = new ArrayList<>();

	@FXML
	AnchorPane anchor;

	@FXML
	Button countBirds;

	Label sequentialNumbering;

	@FXML
	Button newImage;

	@FXML
	TextField birdNumbers;

	@FXML
	StackPane stack;

	@FXML
	ImageView imageDisplay;

	@FXML
	WritableImage image;

	@FXML
	Image ogImage;

	@FXML
	Button changeToImageMod;

	@FXML
	Text invalidFile;

	@FXML
	TextFlow textFlow;

	@FXML
	FileChooser fileChooser = new FileChooser();

	@FXML
	File file;

	@FXML
	Button BirdBoxer;

	@FXML
	Slider redSlider;

	@FXML
	Slider greenSlider;

	@FXML
	Slider blueSlider;

	/**
	 * 
	 * @param newImage method for opening the FileleChooser to select and image and
	 *                 display it in the GUI
	 */

	// method for choosing an image that will be operated on
	public void newImage(ActionEvent event) {

		file = fileChooser.showOpenDialog(null);

		if (file != null) {
			ogImage = new Image(file.toURI().toString(), imageDisplay.getFitWidth(), imageDisplay.getFitHeight(), true,
					false);
			image = new WritableImage(ogImage.getPixelReader(), (int) ogImage.getWidth(), (int) ogImage.getHeight());
			imageDisplay.setImage(image);

		} else {
			invalidFile = new Text("Invalid File Chosen");

		}

	}

	public void OGImage() {
		imageDisplay.setImage(image);
	}

	/**
	 * @blackAndWhiteImage javaFX method to display the black and white image on the
	 *                     imageView
	 */
	public void blackAndWhiteImage() {
		imageDisplay.setImage(blackAndWhite(image));
	}

	/**
	 * @blackAndWhite method for converting the pixels of an image to black and
	 *                white.
	 * 
	 * @param thisImage
	 * @return returns a black and white image
	 */
	public static Image blackAndWhite(Image thisImage) {

		WritableImage blackWhiteImage = new WritableImage(thisImage.getPixelReader(), (int) thisImage.getWidth(),
				(int) thisImage.getHeight());
		PixelReader pxReader = thisImage.getPixelReader();
		PixelWriter pxWriter = blackWhiteImage.getPixelWriter();

		for (int h = 0; h < blackWhiteImage.getHeight(); h++) {
			for (int w = 0; w < blackWhiteImage.getWidth(); w++) {
				Color pxColor = pxReader.getColor(w, h);
				if (pxColor.getRed() + pxColor.getGreen() + pxColor.getBlue() > 1) {
					pxWriter.setColor(w, h, Color.WHITE);
				}
				if (pxColor.getRed() + pxColor.getGreen() + pxColor.getBlue() < 1) {
					pxWriter.setColor(w, h, Color.BLACK);
				}
			}
		}
		return blackWhiteImage;
	}

	public void sliderChange() {
		imageDisplay.setImage(colorSliders(image));

	}

	public Image colorSliders(Image changeColors) {

		WritableImage normalImage = new WritableImage(changeColors.getPixelReader(), (int) changeColors.getWidth(),
				(int) changeColors.getHeight());
		PixelReader pxReader = changeColors.getPixelReader();
		PixelWriter pxWriter = normalImage.getPixelWriter();

		for (int y = 0; y < normalImage.getHeight(); y++) {
			for (int x = 0; x < normalImage.getWidth(); x++) {
				Color pxColor = pxReader.getColor(x, y);
				double redValue = pxColor.getRed();
				double greenValue = pxColor.getGreen();
				double blueValue = pxColor.getBlue();

				redValue += redSlider.getValue();
				greenValue += greenSlider.getValue();
				blueValue += blueSlider.getValue();

				if (redValue <= 0) {
					redValue = 0;
				} else if (redValue >= 1) {
					redValue = 1;
				}
				if (greenValue <= 0) {
					greenValue = 0;
				} else if (greenValue >= 1) {
					greenValue = 1;
				}
				if (blueValue <= 0) {
					blueValue = 0;
				} else if (blueValue >= 1) {
					blueValue = 1;
				}

				pxWriter.setColor(x, y, new Color(redValue, greenValue, blueValue, 1));

			}

		}
		return normalImage;

	}

	/**
	 * @makeBirdSet this method creates a 1D array containing disjoint sets of each
	 *              pixel in the image
	 */

	public int[] makeBirdSet(Image birdImage) {
		PixelReader pxReader = birdImage.getPixelReader();
		int width = (int) birdImage.getWidth();
		int height = (int) birdImage.getHeight();

		birdDisjointSet = new int[width * height];
		for (int p = 0; p < birdDisjointSet.length; p++) {
			birdDisjointSet[p] = p;
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int birdSetPlace = y * width + x;
				if ((pxReader.getArgb(x, y) & 1) == 1) {
					birdDisjointSet[birdSetPlace] = 0;
				} else {
					birdDisjointSet[birdSetPlace] = birdSetPlace;

				}
			}
		}
		return birdDisjointSet;
	}

	/**
	 * @find union find method for finding the root
	 */

	public static int find(int[] birdDisjointSet, int root) {
		return birdDisjointSet[root] == root ? root : find(birdDisjointSet, birdDisjointSet[root]);
	}

	/**
	 * 
	 * @birdUnion quick union method for making joining black pixels together
	 */

	public static void birdUnion(int[] birdDisjointSet, int a, int b) {

		birdDisjointSet[find(birdDisjointSet, b)] = find(birdDisjointSet, a);

	}

	/**
	 * 
	 * @param blackWhite takes in a black and white image
	 * @checkForAdjacentBlackPixels starts in the top of the image and checks left
	 *                              and down wards for any black pixels, if it finds
	 *                              them it adds them together
	 * 
	 * @return
	 */
	public int[] checkForAdjacentBlackPixels2(Image blackWhite) {
		PixelReader pxReader = blackWhite.getPixelReader();
		int width = (int) blackWhite.getWidth() - 1;
		int height = (int) blackWhite.getHeight() - 1;
		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {

				int thisIsBlack = y * width + x;
				int rightPixel = y * width + x + 1;
				int downOnePixel = (y + 1) * width + x;
				int diagonalPixel = (y + 1) * width + (x + 1);

				if (birdDisjointSet[thisIsBlack] != 0) {
					if (birdDisjointSet[rightPixel] != 0 && (x <= width)) {
						birdUnion(birdDisjointSet, (int) thisIsBlack, (int) rightPixel);
					}
					if (birdDisjointSet[downOnePixel] != 0 && (y <= height)) {
						birdUnion(birdDisjointSet, (int) thisIsBlack, (int) downOnePixel);
					}
					if (birdDisjointSet[diagonalPixel] != 0 && (x <= width) && (y <= height)) {
						birdUnion(birdDisjointSet, (int) thisIsBlack, (int) diagonalPixel);
					}
					parentRoot.add(birdDisjointSet[thisIsBlack]);

				}
			}
		}

		return birdDisjointSet;

	}

	/**
	 * @birdCounter this counts the disjoint sets created after union the adjacent
	 *              black pixels to the parent.
	 * 
	 */

	public int birdCounter(int[] birdDisjointSet) {
		int counter = 0;
		for (int i = 0; i < birdDisjointSet.length; i++) {
			if (birdDisjointSet[i] == i && i != 0)
				counter++;
		}
		return counter;
	}

	/** ********************METHOD NO LONGER NEEDED********
	 * @addSequentialNumbers iterate through the birdDisjointSet and count the
	 *                       birds, then add the count to the label
	 * 
	 */
/*
	public void RedundantsetLabelCoordinate(Image blackWhite) {
		HashSet<Integer> birdIds = getBirdIds();
		int sequentialBirds = 0;

		for (int bid : birdIds) {
			sequentialBirds++;
			double top = -1, bottom = -1, left = -1, right = -1;

			for (int i = 0; i < birdDisjointSet.length; i++) {
				int x = i % (int) blackWhite.getWidth();
				int y = i / (int) blackWhite.getWidth();

				if (birdDisjointSet[i] != 0 && find(birdDisjointSet, i) == bid) {

					if (top == -1) {
						top = bottom = y;
						left = right = x;
					} else {
						if (x < left)
							left = x;
						if (x > right)
							right = x;
						if (y > bottom)
							bottom = y;
					}
				}
			

			}
			
			Label sequentialNumbering = new Label();
			((Pane) imageDisplay.getParent()).getChildren().addAll(sequentialNumbering);
			sequentialNumbering.setLayoutX(right+1);
			sequentialNumbering.setLayoutY(top);
			sequentialNumbering.setText(String.valueOf(sequentialBirds));

		}
	}
	*/

	/** *********************METHOD NO LONGER NEEDED
	 * @addSequentialNumbers iterate through the birdDisjoint set and count birds
	 *                       and add the bird number to a label
	 */
	/*
	public void RedundantaddSequentialNumbers() {

		int sequentialBirds = 0;
		for (int i = 0; i < birdDisjointSet.length; i++) {
			if (birdDisjointSet[i] == i && i != 0)
				sequentialBirds++;
			sequentialNumbering.setText(String.valueOf(i));

		}
	}
	*/

	/**
	 * 
	 * @getBirdIds create a HashSet of the roots of the BirdDisjointSet
	 */

	public HashSet<Integer> getBirdIds() {
		HashSet<Integer> birdIds = new HashSet<>();
		for (int i = 0; i < birdDisjointSet.length; i++)
			if (birdDisjointSet[i] != 0)
				birdIds.add(find(birdDisjointSet, i)); // add root id
		return birdIds;
	}

	/**
	 * 
	 * @addBirdBoundaries find the boundaries of a bird cluster and then draw a
	 *                    rectangle and label it.
	 */

	public void addBirdBoundariesAndLabel(Image blackWhite) {
		HashSet<Integer> birdIds = getBirdIds();
		int sequentialBirds=0;

		System.out.println("Bird count: " + birdIds.size());

		for (int bid : birdIds) {
			sequentialBirds++;
			double top = -1, bottom = -1, left = -1, right = -1;

			for (int i = 0; i < birdDisjointSet.length; i++) {
				int x = i % (int) blackWhite.getWidth();
				int y = i / (int) blackWhite.getWidth();

				if (birdDisjointSet[i] != 0 && find(birdDisjointSet, i) == bid) {
					if (top == -1) {
						top = bottom = y;
						left = right = x;
					} else {
						if (x < left)
							left = x;
						if (x > right)
							right = x;
						if (y > bottom)
							bottom = y;
					}
				}
			}

			Rectangle birdBox = new Rectangle(left, top, right - left, bottom - top);
			((Pane) imageDisplay.getParent()).getChildren().add(birdBox);
			birdBox.setStroke(Paint.valueOf("#FF0000"));
			birdBox.setFill(Color.TRANSPARENT);
			Label sequentialNumbering = new Label();
			((Pane) imageDisplay.getParent()).getChildren().addAll(sequentialNumbering);
			sequentialNumbering.setLayoutX(right+1);
			sequentialNumbering.setLayoutY(top);
			sequentialNumbering.setText(String.valueOf(sequentialBirds));

			// System.out.println("Bird Id "+bid+" bounds: "+left+", "+top+", "+right+",
			// "+bottom);
		}

	}

	/**
	 * @clearPane clear the imageView of rectangles.
	 */

	public void clearPane() {
		List<Rectangle> rectList = new ArrayList<>();
		for (Node x : ((Pane) imageDisplay.getParent()).getChildren()) {
			if (x instanceof Rectangle)
				rectList.add((Rectangle) x);
		}
		((Pane) imageDisplay.getParent()).getChildren().removeAll(rectList);
	}
	
	public void clearLabels() {
		List<Label> labelList = new ArrayList<>();
		for(Node y: ((Pane) imageDisplay.getParent()).getChildren()) {
			if(y instanceof Label)
				labelList.add((Label) y);
		}
		((Pane) imageDisplay.getParent()).getChildren().removeAll(labelList);
	}

	/**
	 * **** THIS METHOD IS HAS BEEN REPLACED BY addBirdBoundaries*****
	 * 
	 * 
	 * @param blackWhite take in a black and white image
	 * @drawBox iterate through an image and find the blacks pixels and draw a box
	 *          around them
	 */
	/*
	public AnchorPane drawBox(Image blackWhite) {
		int top = 0;
		int right = 0;
		int bottom = 0;
		int left = 0;
		PixelReader pxReader = blackWhite.getPixelReader();
		int width = (int) blackWhite.getWidth() - 1;
		int height = (int) blackWhite.getHeight() - 1;
		AnchorPane aPane = new AnchorPane();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int topPixel = y * width + x;
				int rightPixel = y * width + x + 1;
				int downPixel = (y + 1) * width + x;
				int leftPixel = (y + 1) * width + (x + 1);
				if (birdDisjointSet[topPixel] != 0 && x >= top) {
					top = topPixel;
				} else {
					if ((birdDisjointSet[rightPixel] != 0 && (x <= width) && ((x + 1) >= right))) {
						right = rightPixel;
					}
					if ((birdDisjointSet[downPixel] != 0 && (y <= height) && ((y + 1) >= bottom))) {
						bottom = downPixel;
					}
					if ((birdDisjointSet[leftPixel] != 0 && (x <= width) && (y <= height) && ((x + 1) >= left))) {
						left = leftPixel;
					}

				}

			}
			Rectangle birdBox = new Rectangle(left, top, right - left, bottom - top);
			birdBox.setStroke(Paint.valueOf("#FF0000"));
			birdBox.setFill(Color.TRANSPARENT);
			birdBox.setVisible(true);

			aPane.getChildren().add(birdBox);
		}
		return aPane;
	}
	*/

	/**
	 * @find this is find method with path compression that would not work correctly
	 * 
	 *       public static int find(int[] birdDisjointSet, int root) { while
	 *       (birdDisjointSet[root] != root) { birdDisjointSet[root] =
	 *       birdDisjointSet[birdDisjointSet[root]]; root = birdDisjointSet[root]; }
	 *       return root;
	 * 
	 *       }
	 */

	/**
	 * @birdUnionByHeight this is a union method that would union elements based on
	 *                    height, this would not work due to the above find method
	 *                    not functioning correctly.
	 * 
	 * 
	 *                    public void birdUnionByHeight(int[] birdDisjointSet, int
	 *                    x, int y) { int xRoot = find(birdDisjointSet, x); int
	 *                    yRoot = find(birdDisjointSet, y);
	 * 
	 *                    int deepRoot = birdDisjointSet[xRoot] <
	 *                    birdDisjointSet[yRoot] ? xRoot : yRoot; int shallowRoot =
	 *                    deepRoot == yRoot ? yRoot : xRoot;
	 * 
	 *                    int temp = birdDisjointSet[shallowRoot];
	 *                    birdDisjointSet[shallowRoot] = deepRoot; if
	 *                    (birdDisjointSet[deepRoot] == temp)
	 *                    birdDisjointSet[deepRoot]--;
	 * 
	 *                    }
	 */ 

	/**
	 * @doTheThings this creates the black and white image, checks for adjacent black
	 *              pixels around it unions the adjacent black pixels and counts the
	 *              total "birds" created from the union and outputs that data to a
	 *              TextField. this method also contains a commented out loop for
	 *              printing the union process to console, for analysis during
	 *              debugging.
	 * 
	 */

	public void doTheThings() {
		makeBirdSet(blackAndWhite(image));
		checkForAdjacentBlackPixels2(blackAndWhite(image));
		Integer rootResult = birdCounter(birdDisjointSet);
		birdNumbers.setText(rootResult.toString());
		/*
		 * System.out.println(birdCounter(birdDisjointSet));
		 * 
		 * for (int y = 0; y < birdDisjointSet.length; y++) { System.out.println(
		 * " the root of element " + y + " is " + find(result, y) + " (element value: "
		 * + result[y] + ")"); }
		 */

	}

	/**
	 * @boxingBirds  method for drawing rectangle around birds and labeling the bird.
	 * 				 it contains two commented out methods that did the labeling seperated
	 * 				 before I combined the process.
	 */

	public void boxingBirds() {
		addBirdBoundariesAndLabel(image);
	//	setLabelCoordinate(image);
		//addSequentialNumbers();
	}
	
	public void clearThePane() {
		clearPane();
		clearLabels();
	}

}
