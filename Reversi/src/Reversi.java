

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Random;
import java.lang.Runnable;
import javafx.application.Platform;
import java.util.Optional;

/**
 * This Reversi class represents a
 * Reversi game based on the rules of Othello.
 */

public class Reversi extends Stage {

	EventHandler<ActionEvent> exit;
	HBox logoBar;
    HBox topMenu;
    HBox turnTracker;
    Button exitButton;
    HBox bottom;
    VBox root;
    

    Image potenS = new Image("file:resources/potentialSpace.JPG");
    Image emptyS = new Image("file:resources/emptySpace.JPG");
    Image blueS = new Image("file:resources/blueSpace.JPG");
    Image redS = new Image("file:resources/redSpace.JPG");
    Image reversiLogo = new Image("file:resources/reversiLogo.JPG");
    ButtonType exitDialog = new ButtonType("OK");
    ButtonType yesAI = new ButtonType("Yes");
    ButtonType noAI = new ButtonType("No");
    Alert aiQuery = new Alert(AlertType.CONFIRMATION, "Would You Like to Enable the AI?", yesAI, noAI);
    Alert redWins = new Alert(AlertType.INFORMATION, "Red Player Wins!", exitDialog);
    Alert blueWins = new Alert(AlertType.INFORMATION, "Blue Player Wins!", exitDialog);
    Alert draw = new Alert(AlertType.INFORMATION, "It's a Draw!", exitDialog);
    Alert insuffMovesRed = new Alert(AlertType.NONE, "No valid moves remain for Red\n GAME OVER",
                                     exitDialog);
    Alert insuffMovesBlue = new Alert(AlertType.NONE,
                                       "No valid moves remain for Blue\n GAME OVER", exitDialog);
    GridPane mainPane = new GridPane();
    
    int blueCount = 2;
    int redCount = 2;
    int emptyCount = 0;
    
    boolean keepPlaying = true;
    boolean aiEnabled = false;
    boolean clicked = false;
    
    Text redTracker = new Text("Red: " + redCount);
    Text blueTracker = new Text("Blue: " + blueCount);
    Text currentColor = new Text("red");	//red player always goes first
    Text colorIndicator = new Text("Current Turn: " + currentColor.getText());
    Text locationIndicator = new Text("Coordinates: ");
    Text status = new Text("Player's turn");
    ImageView[][] imgGrid = new ImageView[8][8];
    ImageView blueEX = new ImageView(blueS);
    ImageView redEX = new ImageView(redS);
    ImageView emptyEX = new ImageView(emptyS);
    ImageView potEX = new ImageView(potenS);
    ImageView theLogo = new ImageView(reversiLogo);
    
    Reversi anotherGame;
    Scene mainScene;

    /**
     * This constructor initializes the scene graph and
     * all necessary nodes and instance variables.
     */
    
    public Reversi() {
        
        setUpOther();
        Optional<ButtonType> r = aiQuery.showAndWait();
       if (r.toString().equals("Optional[ButtonType [text=Yes, buttonData=OTHER]]")) {
    	   aiEnabled = true;
       } //if user clicked to enable the AI
       else aiEnabled = false;
       
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                ImageView tempView = new ImageView(emptyS);
                tempView.setPreserveRatio(true);
                tempView.setFitWidth(80.0);
                tempView.setFitHeight(80.0);
                tempView.setOnMouseClicked(event -> {
                	showPotentials();
                	playerAction(tempView);
                });
                
                mainPane.add(tempView, i, i2);
                imgGrid[i][i2] = tempView;
                tempView.setOnMouseMoved(event -> {
                	locationIndicator.setText("Coordinates: " +
                			GridPane.getRowIndex(tempView) + ", " +
                			GridPane.getColumnIndex(tempView));
                });
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 3) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 3)) {
                    imgGrid[i][i2].setImage(redS);
                } //if top-left of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 4) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 4)) {
                    imgGrid[i][i2].setImage(redS);
                } //if bottom-right of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 4) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 3)) {
                    imgGrid[i][i2].setImage(blueS);
                } // if top-right of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 3) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 4)) {
                    imgGrid[i][i2].setImage(blueS);
                } //if bottom-left of initial setup
            } //for
        } //for loop assigning ImageViews to GridPane
        root.getChildren().addAll(logoBar, topMenu, mainPane, turnTracker, bottom);
        setUpScene();
        showPotentials();
    } //absolute unit constructor
    
    /**
     * This method sets the actions for an ImageView upon being clicked.
     * It is primarily intended to reduce the bloat of the event handler
     * introduced in the constructor.
     * @param iv the ImageView on which the actions will be carried out
     */
    
    private void playerAction(ImageView iv) {
    	 if (checkValidPlacement(iv) == true) {
    		 clicked = true;
             reverseColors(iv);
             setImage(iv);
             if (currentColor.getText().equals("blue")) {
                 countReds();
                 countBlues();
             } //if red played
             
             if (currentColor.getText().equals("red")) {
                 countBlues();
                 countReds();
             } //if blue played
             
             numOfValidMoves();
             
             if (countEmpties() == 0) {
                 determineEnd();
             } //if all spaces used
         } //if user-clicked space is valid per color
         hidePotentials();
         if (clicked && aiEnabled) {
        	 status.setText("AI's turn");
        	 Runnable r = () -> {
        		 try {
            		 Thread.sleep(1700);
            	 } catch (InterruptedException e) {
            		 System.out.println(e);
            	 } //try-catch
        		 botPlay();
        		 }; //runnable representing AI's play
        	 Platform.runLater(r);
         } //if valid space clicked by user
    } //playerAction
    
    /**
     * This method is repsonsible for the setting of the 
     * {@code Image} property of whichever {@code ImageView}
     * is clicked by the user, so long as said ImageView 
     * is valid as determined by the {@code checkValidPlacement}
     * method.
     * @param iv the ImageView whose Image property is to be set
     * @param isPlayer true if it is player's turn, false for comp
     */
    
    private void setImage(ImageView iv) {
    	
        if (currentColor.getText().equals("red")) {
            iv.setImage(redS);
            currentColor.setText("blue");
            colorIndicator.setText("Current turn: " + currentColor.getText());
            colorIndicator.setFill(Color.DODGERBLUE);
            return;
        } //if red when called, changing to blue
        if (currentColor.getText().equals("blue")) {
            iv.setImage(blueS);
            currentColor.setText("red");
            colorIndicator.setText("Current turn: " + currentColor.getText());
            colorIndicator.setFill(Color.DARKRED);
            return;
        } //if blue when called, changing to red
    } //setImage

    /**
     * This method searches through the 2D array of ImageViews (instance var)
     * in search of ImageViews that have an {@code Image} of RED and counts them.
     */
    
    private void countReds() {
        redCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (imgGrid[i][i2].getImage().equals(redEX.getImage())) {
                    redCount++;
                } //if
            } //for
        } //for
        redTracker.setText("Red: " + redCount);
    } //countReds

    /**
     * This method searches thorugh the 2D array of ImageViews (instance var)
     * in search of ImageViews that have an {@code Image} of BLUE and counts them.
     */
    
    private void countBlues() {
        blueCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (imgGrid[i][i2].getImage().equals(blueEX.getImage())) {
                    blueCount++;
                } //if
            } //for
        } //for
        blueTracker.setText("Blue: " + blueCount);
    } //countBlues

    /**
     * This method searches through the 2D array of ImageViews (instance var)
     * in search of ImageViews that have an {@code Image} of EMPTY  or POTENTIAL and counts them.
     * @return the number of ImageViews on the board that do not contain a piece
     */
    
    private int countEmpties() {
        int emptyCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (imgGrid[i][i2].getImage().equals(emptyEX.getImage()) ||
                		imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                    emptyCount++;
                } //if
            } //for
        } //for
        return emptyCount;
    } //countEmpties

    /**
     * This method determines whether a user-selected ImageView is
     * 'valid' for the current player's turn, as based on the 
     * return value of necessary helper methods.
     * @param iv the ImageView whose validity is to be checked
     * @return true if the user-selected space is valid, false otherwise
     */
    
    private boolean checkValidPlacement(ImageView iv) {
        int colNum = GridPane.getColumnIndex(iv);
        int rowNum = GridPane.getRowIndex(iv);
        boolean placeValid = false;
        if (iv.getImage().equals(emptyEX.getImage()) == false && iv.getImage().equals(potEX.getImage()) == false) {
            return false;
        } //if space already occupied
        if (colNum >= 1 && colNum <= 6 && rowNum >= 1 && rowNum <= 6) {
            placeValid = checkUp(iv) || checkDown(iv) || checkLeft(iv) 
                || checkRight(iv) || checkUpRight(iv) || checkDownRight(iv)
                || checkUpLeft(iv) || checkDownLeft(iv);
            
            return placeValid;
        } //if optimal placement (8 directions)
        if (colNum == 0 && rowNum == 0) {
            placeValid = checkDown(iv) || checkDownRight(iv) || checkRight(iv);
            
            return placeValid;
        } //if top left corner (3 directions)
        if (colNum == 0 && rowNum == 7) {
            placeValid = checkUp(iv) || checkUpRight(iv) || checkRight(iv);
            
            return placeValid;
        } //if bottom left corner (3 directions)
        if (colNum == 7 && rowNum == 7) {
            placeValid = checkLeft(iv) || checkUpLeft(iv) || checkUp(iv);
            
            return placeValid;
        } //if bottom right corner (3 directions)
        if (colNum == 7 && rowNum == 0) {
            placeValid = checkLeft(iv) || checkDownLeft(iv) || checkDown(iv);
            
            return placeValid;
        } //if top right corner (3 directions)
        if (colNum >= 1 && colNum <= 6 && rowNum == 0) {
            placeValid = checkLeft(iv) || checkDownLeft(iv) || checkDown(iv)
                || checkDownRight(iv) || checkRight(iv);
            
            return placeValid;
        } //if top row (5 directions)
        if (colNum >= 1 && colNum <= 6 && rowNum == 7) {
            placeValid = checkLeft(iv) || checkUpLeft(iv) || checkUp(iv)
                || checkUpRight(iv) || checkRight(iv);
           
            return placeValid;
        } //if bottom row (5 directions)
        if (colNum == 0 && rowNum >= 1 && rowNum <= 6) {
            placeValid = checkUp(iv) || checkUpRight(iv) || checkRight(iv)
                || checkDownRight(iv) || checkDown(iv);
           
            return placeValid;
        } //if left most column (5 directions)
        if (colNum == 7 && rowNum >= 1 && rowNum <= 6) {
            placeValid = checkUp(iv) || checkUpLeft(iv) || checkLeft(iv)
                || checkDownLeft(iv) || checkDown(iv);
            
        } //if right most column (5 directions)
        return placeValid;
    } //checkValidPlacement

    /**
     * This method checks for an adjacent (up) space containing
     * a piece of the color opposite the player's, as well as
     * searching vertically for a piece of the same color.
     * @param iv the ImageView whose vertical (up) validity is to be tested
     * @return true if opposite piece is found adjacent and same piece is found with no
     * empty spaces between, false otherwise
     */
    
    private boolean checkUp(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean oppFound = false;
        boolean sameFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col][row - 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another red to confirm placement
            } //if a blue is found adjacent
        } //if current color is red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col][row - 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for loop checking for another blue to confirm placement
            } //if a red is found adjacent
        } //if current color is blue (check for red than another blue)
        return sameFound && oppFound;
    } //checkUp

    /**
     * This method checks for an adjacent (down) space containing
     * a piece of the color opposite the player's, as well as
     * searching vertically for a piece of the same color with no empty spaces between.
     * @param iv the ImageView whose vertical (down) validity is to be tested
     * @return true if opposite color found directly beneath and same color
     * found somewhere beneath with no empty spaces between, false otherwise
     */
    
    private boolean checkDown(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean sameFound = false;
        boolean oppFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col][row + 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another red to confirm placement
            } //if a blue is found adjacent
        } //if current color is red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col][row + 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another blue to confirm placement
            } //if a red is found adjacent
        } //if current color is blue (check for red then another blue)
        return sameFound && oppFound;
    } //checkDown

    /**
     * This method checks for an adjacent (left) space containing
     * a piece of the color opposite the player's, as well as
     * searching horizontally for a piece of the same color with no
     * empty spaces between. 
     * @param iv the ImageView whose horizontal (left) validity is to be tested
     * @return true if opposite color found directly left and same color found
     * somewhere to the left with no empty spaces between, false otherwise
     */
    
    private boolean checkLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean sameFound = false;
        boolean oppFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col - 1][row].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row; i >= 0; i--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if empty space is found
                } //for searching for another red to confirm placement
            } //if a blue is found adjacent
        } //if current color is red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col - 1][row].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row; i >= 0; i--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another blue to confirm placement
            } //if red found adjacent
        } //if current color is blue (check for red then another blue)
        return sameFound && oppFound;
    } //checkLeft

    /**
     * This method checks for an adjacent (right) space containing
     * a piece of the color opposite the player's, as well as searching
     * horizontally for a piece of the same color with no empty spaces between.
     * @param iv the ImageView whose horizontal (right) validity is to be tested
     * @return true if opposite color found directly right and same color
     * found somewhere to the right with no empty spaces between, false otherwise
     */
    
    private boolean checkRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean sameFound = false;
        boolean oppFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col + 1][row].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row; i <= 7; i++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another red to confirm placement
            } //if a blue is found adjacent
        } //if current color is red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col + 1][row].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row; i <= 7; i++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another blue to confirm pacement
            } //if a red is found adjacent
        } //if current color is blue (check for red then another blue)
        return oppFound && sameFound;
    } //checkRight

    /**
     * This method checks for an adjacent (diagonal up-right) space containing
     * a piece of the color opposite the player's, as well as
     * searching diagonally for a piece of the same colr with no empty spaces between.
     * @param iv the ImageView whose diagonal (up-right) validity is to be tested
     * @return true if opposite color found directly to the up-right and same color
     * found somewhere up-right with no empty spaces between, false otherwise
     */
    
    private boolean checkUpRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean oppFound = false;
        boolean sameFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col + 1][row - 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another red to confirm placement
            } //if blue found adjacent
        } //if current color is red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col + 1][row - 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another blue to confirm placement
            } //if red found adjacent
        } //if current color is blue (check for red then another blue)
        return oppFound && sameFound;
    } //checkUpRight

    /**
     * This method checks for an adjacent (diagonal down-right) space containing
     * a color opposite the player's as well as
     * searching diagonally for a piece of the same color with no empty spaces between.
     * @param iv the ImageView whose diagonal (down-right) validity is to be tested
     * @return true if piece of opposite color found directly down-right and piece
     * of same color found somewhere down-right with no empty spaces between,
     * false otherwise
     */
    
    private boolean checkDownRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean sameFound = false;
        boolean oppFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col + 1][row + 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second red to confirm placement
            } //if blue found adjacent
        } //if current color red (check for blue then another red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col + 1][row + 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second blue to confirm placement
            } //if red found adjacent
        } //if current color is blue (check for red then another blue)
        return sameFound && oppFound;
    } //checkDownRight

    /**
     * This method checks for an adjacent (diagonal up-left) space containing a 
     * piece of the color opposite the player's, as well as
     * searching diagonally for a piece of the same color with no empty spaces between.
     * @param iv the ImageView whose diagonal (up-left) validity is to be tested
     * @return true if piece of opposite color found directly up-left and piece of
     * same color found further up-left with no interceding emty spaces, false otherwise
     */
    
    private boolean checkUpLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean sameFound = false;
        boolean oppFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col - 1][row - 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second red to confirm placement
            } //if blue found adjacent
        } //if current turn is red (check for blue then red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col - 1][row - 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second blue to confirm placement
            } //if red found adjacent
        } //if current turn is blue (check for red then blue)
        return sameFound && oppFound;
    } //checkUpLeft

    /**
     * This method checks for an adjacent (diagonal down-left) space containing
     * a piece of the color opposite the player's, as well as searching diagonally
     * for a piece of the same color with no empty spaces between.
     * @param iv the ImageView whose diagonal (down-left) validity is to be tested
     * @return true if opposite color found directly down-left and same color found
     * further down-left with no interceding empty spaces, false otherwise
     */
    
    private boolean checkDownLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        boolean oppFound = false;
        boolean sameFound = false;
        if (currentColor.getText().equals("red")) {
            if (imgGrid[col - 1][row + 1].getImage()
                .equals(blueEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(redEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing red found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second red to confirm placement
            } //if blue found adjacent
        } //if current turn red (check for blue then red)
        if (currentColor.getText().equals("blue")) {
            if (imgGrid[col - 1][row + 1].getImage()
                .equals(redEX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(blueEX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing blue found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second blue to confirm placement
            } //if red found adjacent
        } //if current turn blue (check for red then blue)
        return sameFound && oppFound;
    } //checkDownLeft

    /**
     * This method is the 'master' method for the Image-reversal aspect of the game.
     * The method calls necessary helper methods based on the coordinates of the 
     * {@code ImageView}.
     *
     * @param iv the ImageView that is to begin the Image-reversal process
     */
    
    private void reverseColors(ImageView iv) {
        int colNum = GridPane.getColumnIndex(iv);
        int rowNum = GridPane.getRowIndex(iv);
        if (colNum >= 1 && colNum <= 6 && rowNum >= 1 && rowNum <= 6) {
            reverseOptimal(iv);
        } //if 'optimal' placement
        if (colNum == 0 && rowNum == 0) {
            reverseTopLeft(iv);
        } //if top left corner
        if (colNum == 0 && rowNum == 7) {
            reverseBottomLeft(iv);
        } //if bottom left corner
        if (colNum == 7 && rowNum == 7) {
            reverseBottomRight(iv);
        } //if bottom right corner
        if (colNum == 7 && rowNum == 0) {
            reverseTopRight(iv);
        } //if top right
        if (colNum >= 1 && colNum <= 6 && rowNum == 0) {
            reverseTopRow(iv);
        } //if top row (not corner)
        if (colNum >= 1 && colNum <= 6 && rowNum == 7) {
            reverseBottomRow(iv);
        } //if bottom row (not corner)
        if (colNum == 0 && rowNum >= 1 && rowNum <= 6) {
            reverseLeftCol(iv);
        } //if left most column
        if (colNum == 7 && rowNum >= 1 && rowNum <= 6) {
            reverseRightCol(iv);
        } //if right most column
    } //reverseColors

    /**
     * This method is responsible for reversing ImageView objects in a vertically 
     * ascending manner, beginning with the space directly above the argument
     * ImageView.
     * @param iv the ImageView that is to begin the vertical reversal
     */
    
    private void reverseUp(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue is found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
        
    } //reverseUp

    /**
     * This method is responsible for reversing ImageView objects in a vertically
     * descending manner, beginning with the space directly below the argument
     * ImageView.
     * @param iv the ImageView that is to begin the vertical reversal
     */
    
    private void reverseDown(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } // if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseDown

    /**
     * This method is responsible for reversing ImageView objects in a horizontally
     * 'descending' manner, beginning with the space directly left of the argument
     * ImageView.
     * @param iv the ImageView that is to begin the horizontal reversal
     */
    
    private void reverseLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col - 1, i2 = row; i >= 0; i--) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col - 1, i2 = row; i >= 0; i--) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if whtie found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseLeft

    /**
     * This method is responsible for reversing ImageView objects in a horizontally
     * 'ascending' manner, beginning with the space directly right of the argument
     * ImageView.
     * @param iv the ImageView that is to begin the horizontal reversal
     */
    
    private void reverseRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col + 1, i2 = row; i <= 7; i++) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col + 1, i2 = row; i <= 7; i++) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseRight

    /**
     * This method is responsible for reversing ImageView objects in a diagonal
     * (up-right) manner, beginning witht he space directly above and right of the
     * argument ImageView.
     * @param iv the ImageView that is to begin the diagonal reversal
     */
    
    private void reverseUpRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseUpRight

    /**
     * This method is responsible for reversing ImageView objects in a diagonal
     * (down-right) manner, beginning with the space directly below and right of
     * the argument ImageView.
     * @param iv the ImageView that is to begin the diagonal reversal
     */
    
    private void reverseDownRight(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseDownRight

    /**
     * This method is responsible for reversing ImageView objects in a diagonal
     * (up-left) manner, beginning with the space directly above and left of the
     * argument ImageView.
     * @param iv the ImageView that is to begin the diagonal reversal
     */
    
    private void reverseUpLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
    } //reverseUpLeft

    /**
     * This method is responsible for reversing ImageView objects in a diagonal
     * (down-left) manner, beginning with the space directly below and left of the
     * argument ImageView.
     * @param iv the ImageView that is to begin the diagonal reversal
     */
    
    private void reverseDownLeft(ImageView iv) {
        int col = GridPane.getColumnIndex(iv);
        int row = GridPane.getRowIndex(iv);
        if (currentColor.getText().equals("red")) {
            for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    imgGrid[i][i2].setImage(redS);
                    continue;
                } //if blue found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    break;
                } //if red found to cease reversal
            } //for reversing blues to reds
        } //if red's turn
        if (currentColor.getText().equals("blue")) {
            for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(redEX.getImage())) {
                    imgGrid[i][i2].setImage(blueS);
                    continue;
                } //if red found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(blueEX.getImage())) {
                    break;
                } //if blue found to cease reversal
            } //for reversing reds to blues
        } //if blue's turn
        
    } //reverseDownLeft

    /**
     * This method is called by {@code reverseColors} for the sole purpose
     * of directly calling appropriate directional reverse methods for ImageView
     * reversal. It is only called for ImageViews found to be in 'optimal'
     * placement, meaning they may result in a reversal in any of the 8 directions.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseOptimal(ImageView iv) {
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //if needs to be reversed up
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //if needs to be reversed down
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //if needs to be reversed left
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //if needs to be reversed right
        if (checkUpRight(iv) == true) {
            reverseUpRight(iv);
        } //if needs to be reversed up right
        if (checkDownRight(iv) == true) {
            reverseDownRight(iv);
        } //if needs to be reversed down right
        if (checkDownLeft(iv) == true) {
            reverseDownLeft(iv);
        } //if needs to be reversed down left
        if (checkUpLeft(iv) == true) {
            reverseUpLeft(iv);
        } //if needs to be reversed up left
    } //reverseOptimal

    /**
     * This method is called by {@code reverseColors} for the sole purpose
     * of directly calling appropriate directional reverse methods for ImageView
     * reversal. It is only called for the ImageView that is in the top-left
     * corner of the board, as only 3 directions are available there.
     * @param iv the ImageView that is to begin all necessary direcional reversals
     */
    
    private void reverseTopLeft(ImageView iv) {
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //if needs to be reversed down
        if (checkDownRight(iv) == true) {
            reverseDownRight(iv);
        } //if needs to be reversed down right
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //if needs to be reversed right
    } //reverseTopLeft

    /**
     * This method is called by {@code reverseColors} for the sole purpose
     * of directly calling appropriate directional reverse methods for ImageView
     * reversal. It is only called for the ImageView that is in the bottom-left of the
     * board, as only 3 directions are available there.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseBottomLeft(ImageView iv) {
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //if needs to be reversed up
        if (checkUpRight(iv) == true) {
            reverseUpRight(iv);
        } //if needs to be reversed up right
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //if needs to be reversed right
    } //reverseBottomLeft

    /**
     * This method is called by {@code reverseColors} for the sole purpose 
     * of directly calling appropriate directional reverse methods for ImageView
     * reversal. It is only called for the ImageView that is in the bottom-right
     * of the board, as only 3 directions are available there.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseBottomRight(ImageView iv) {
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //if needs to be reversed left
        if (checkUpLeft(iv) == true) {
            reverseUpLeft(iv);
        } //if needs to be reversed up left
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //if needs to be reversed up
    } //reverseBottomRight

    /**
     * This method is caled by {@code reverseColors} for the sole purpose 
     * of directly calling appropriate directional reverse methods for ImageView
     * reversal. It is only called for the ImageView that is in the top-right corner
     * of the board, as only 3 directions are available there.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseTopRight(ImageView iv) {
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //if needs to be reversed left
        if (checkDownLeft(iv) == true) {
            reverseDownLeft(iv);
        } //if needs to be reversed down left
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //if needs to be reversed down
    } //reverseTopRight

    /**
     * This method directly calls all appropriate directional reverse methods for ImageView
     * reversal. This method is specific to ImageView objects located along the top most
     * row of the board, excluding the first and last elements of said row.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseTopRow(ImageView iv) {
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //left
        if (checkDownLeft(iv) == true) {
            reverseDownLeft(iv);
        } //up left
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //down
        if (checkDownRight(iv) == true) {
            reverseDownRight(iv);
        } //down right
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //right
    } //reverseTopRow

    /**
     * This method directly calls all appropriate directional reverse methods for ImageView
     * reversal. This method is specific to ImageView objects located along the bottom most row
     * of the board, excluding the first and last elements of said row.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseBottomRow(ImageView iv) {
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //left
        if (checkUpLeft(iv) == true) {
            reverseUpLeft(iv);
        } //up left
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //up
        if (checkUpRight(iv) == true) {
            reverseUpRight(iv);
        } //up right
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //right
    } //reverseBottomRow

    /**
     * This method directly calls all appropriate directional reverse methods for ImageView
     * reversal. This method is specific to ImageView objects located in the left most column
     * of the board, excluding the top and bottom elements of said column.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseLeftCol(ImageView iv) {
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //up
        if (checkUpRight(iv) == true) {
            reverseUpRight(iv);
        } //upRight
        if (checkRight(iv) == true) {
            reverseRight(iv);
        } //right
        if (checkDownRight(iv) == true) {
            reverseDownRight(iv);
        } //down right
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //down
    } //reverseLeftCol

    /**
     * This method calls all appropriate directional reverse methods for ImageView
     * reversal. This method is specific to ImageView objects located in the right most
     * column of the board, excluding the top and bottom elements of said column.
     * @param iv the ImageView that is to begin all necessary directional reversals
     */
    
    private void reverseRightCol(ImageView iv) {
        if (checkUp(iv) == true) {
            reverseUp(iv);
        } //up
        if (checkUpLeft(iv) == true) {
            reverseUpLeft(iv);
        } //up left
        if (checkLeft(iv) == true) {
            reverseLeft(iv);
        } //left
        if (checkDownLeft(iv) == true) {
            reverseDownLeft(iv);
        } //down left
        if (checkDown(iv) == true) {
            reverseDown(iv);
        } //down
    } //reverseRightCol

    /**
     * This method determines the number of valid moves that are available to
     * the current player. A return of zero valid moves is grounds for the game
     * to end and the result to be determined
     * @return the number of valid moves for the current player
     */
    
    private void numOfValidMoves() {
        
        if (currentColor.getText().equals("red")) {
            int validReds = 0;
            for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 7; j++) {
                    if (checkValidPlacement(imgGrid[i][j]) == true) {
                        validReds++;
                    } //if a given space is valid for red player
                } //inner for
            } //outer for
            if (validReds == 0) {
                insuffMovesRed.showAndWait();
                System.out.println("insuffMovesRed");
                determineEnd();
            } //if no valid moves for red

        } //if it is red's turn
        if (currentColor.getText().equals("blue")) {
            int validBlues = 0;
            for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 7; j++) {
                    if (checkValidPlacement(imgGrid[i][j]) == true) {
                        validBlues++;
                    } //if a given space is valid for blue player
                } //inner for
            } //outer for
            if (validBlues == 0) {
                insuffMovesBlue.showAndWait();
                System.out.println("insuffMovesBlue");
                determineEnd();
            } //if no valid moves for blue

        } //if it is blue's turn
        
    } //numOfValidMoves

    /**
     * This method determines the end result of the game based on 
     * the score of both players. Based on the determined result,
     * an appropriate {@code Alert} will be displayed concerning 
     * the end result of the game, whereupon the stage will close.
     */
    
    private void determineEnd() {

        if (redCount > blueCount) {

            redWins.showAndWait();
            this.close();
            System.exit(0);
        } //if red has higher score
        if (redCount < blueCount) {
            blueWins.showAndWait();
            this.close();
            System.exit(0);
        } //if blue has higher score
        if (redCount == blueCount) {
            draw.showAndWait();
            this.close();
            System.exit(0);
        } //if draw

    } //determineEnd

    /**
     * This method sets up necessary components of
     * the {@code Scene} graph, and reduces the bulk of the
     * constructor.
     */
    
    private void setUpOther() {
    	logoBar = new HBox(100);
    	Background filler = new Background(new BackgroundFill(Color.BLACK, null, null));
    	logoBar.setBackground(filler);
    	theLogo.setFitWidth(140);
    	theLogo.setFitHeight(80);
    	logoBar.getChildren().add(theLogo);
    	logoBar.setAlignment(Pos.BASELINE_CENTER);
    	
        topMenu = new HBox(12);
        topMenu.setBackground(filler);
        exit = event -> this.close();
        exitButton = new Button("EXIT");
        exitButton.setOnAction(exit);
        exitButton.setTextFill(Color.ANTIQUEWHITE);
        //exitButton.setStyle("-fx-background-color: #000000");
        exitButton.setStyle("-fx-background-color: #000000; -fx-border-color: #ff0000");
        redTracker.setFill(Color.DARKRED);
        blueTracker.setFill(Color.DODGERBLUE);
        topMenu.getChildren().addAll(exitButton, redTracker, blueTracker);
        
        turnTracker = new HBox();
        turnTracker.setBackground(filler);
        status.setFill(Color.ANTIQUEWHITE);
        turnTracker.getChildren().add(status);
        turnTracker.setAlignment(Pos.BASELINE_CENTER);

        bottom = new HBox(405);
        bottom.setBackground(filler);
        colorIndicator.setFill(Color.ANTIQUEWHITE);
        locationIndicator.setFill(Color.ANTIQUEWHITE);
        colorIndicator.setUnderline(true);
        colorIndicator.setFill(Color.DARKRED);
        bottom.getChildren().add(colorIndicator);
        bottom.getChildren().add(locationIndicator);

        root = new VBox();
    } //setUpOther

    /**
     * This method sets up necessary components of the Scene graph,
     * namely the Scene itself, and calls necessary methods to size the
     * stage to the scene, show the stage, and set the title of the Stage.
     */
    
    private void setUpScene() {
        mainScene = new Scene(root);
        this.setTitle("Reversi App");
        this.setScene(mainScene);
        this.show();
        this.sizeToScene();
    } //setUpScene
    
    /**
     * This method iterates through the game board and changes
     * the Image property of any {@code ImageView} that is a valid
     * move for the current player. Each such ImageView will be set
     * to the Image of a potential move space.
     */
    
    private void showPotentials() {
    	for (int i = 0; i < 8; i++) {
    		for (int j = 0; j < 8; j++) {
    			if (checkValidPlacement(imgGrid[i][j])) {
    				imgGrid[i][j].setImage(potenS);
    			} //if a space is valid for current player
    		} //inner for loop
    	} //outer for loop
    } //showPotentials
    
    /**
     * This method is called after {@code showPotentials} and iterates
     * through the game board to "hide" all potential spaces by reverting
     * their Image property back to that of an empty space.
     * This method's primary purpose is to allow for the resetting of potential
     * spaces as the specific ImageViews that are potential moves are different for
     * each turn.
     */
    
    private void hidePotentials() {
    	for (int i = 0; i < 8; i++) {
    		for (int j = 0; j < 8; j++) {
    			if (imgGrid[i][j].getImage() == potEX.getImage()) {
    				imgGrid[i][j].setImage(emptyS);
    			} //if a space is a potential image
    		} //inner for loop
    	} //outer for loop
    	try {
        	Thread.sleep(200);
        	} catch (InterruptedException e) {
        		System.out.println(e);
        	} //try-catch for delay
    	showPotentials();
    } //hidePotentials
    
    /**
     * This method simulates play by a second player (blue) by using a random-select AI
     * to play an {@code ImageView} on the game board, just like when a user selects a space.
     * If the AI chooses an invalid space, the method continues execution until a valid space is
     * chosen and gameplay can continue.
     */
    
    private void botPlay() {
    		Random rando = new Random();
    		boolean validChosen = false;
    		while (!validChosen) {
    			int row = rando.nextInt(8);
    			int col = rando.nextInt(8);
    			if (checkValidPlacement(imgGrid[row][col])) {
    				 validChosen = true;
    				 clicked = false;
    				 reverseColors(imgGrid[row][col]);
    	             setImage(imgGrid[row][col]);
    	             if (currentColor.getText().equals("blue")) {
    	                 countReds();
    	                 countBlues();
    	             } //if red played
    	             
    	             if (currentColor.getText().equals("red")) {
    	                 countBlues();
    	                 countReds();
    	             } //if blue played
    	             
    	             numOfValidMoves();
    	             
    	             if (countEmpties() == 0) {
    	                 determineEnd();
    	             } //if all spaces used
    			} //if randomly-selected space is a valid move
    			else continue;
    		} //while
    	hidePotentials();
    	status.setText("Player's turn");
    } //botPlay
    
} //Reversi
