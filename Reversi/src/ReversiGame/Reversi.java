package ReversiGame;

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
import javafx.scene.text.Font;
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
import javafx.application.Application;
import java.util.Vector;
import javafx.scene.control.ComboBox;

/**
 * This Reversi class represents a
 * Reversi game based on the rules of Othello.
 */

public class Reversi extends Application {
    ColorPackage cpkg;
	EventHandler<ActionEvent> exit;
    EventHandler<ActionEvent> potentialToggle;
    EventHandler<ActionEvent> newColorSelectionEvent;
	HBox logoBar;
    HBox exitBar;
    HBox topMenu;
    HBox colorPickerBox;
    HBox turnTracker;
    Button exitButton;
    Button potentialSpaceToggleButton;
    HBox bottom;
    VBox root;
    ComboBox<String> p1ColorSelect;
    ComboBox<String> p2ColorSelect;
    Vector<String> validColorStrings = new Vector<>();
    
    PieceColor p1Color = PieceColor.WHITE;
    PieceColor p2Color = PieceColor.BLACK;
    ColorPackage p1ColorPackage = new ColorPackage(p1Color); // contains image filename & color string
    ColorPackage p2ColorPackage = new ColorPackage(p2Color); // contains image filename & color string
    
    Image potenS;
    Image emptyS;

    Image player1S;
    Image player2S;
    Image reversiLogo;

    ButtonType exitDialog = new ButtonType("OK");
    ButtonType yesAI = new ButtonType("Yes");
    ButtonType noAI = new ButtonType("No");
    Alert aiQuery;
    Alert player1Wins;
    Alert player2Wins;
    Alert draw;
    Alert insuffMovesP1;
    Alert insuffMovesP2;

    GridPane mainPane = new GridPane();
    
    int player2Count = 2;
    int player1Count = 2;
    int emptyCount = 0;
    
    boolean keepPlaying = true;
    boolean aiEnabled = false;
    boolean clicked = false;
    boolean potentialSpaceVisible = true;
    
    Text player1Tracker, player2Tracker, currentTurn;
    Text colorIndicator, status;
    Text player1Color, player2Color;

    ImageView[][] imgGrid = new ImageView[8][8];
    ImageView player2EX;
    ImageView player1EX;
    ImageView emptyEX;
    ImageView potEX;
    ImageView theLogo;
    ImageView pSpaceToggle;
    
    Reversi anotherGame;
    Scene mainScene;
    Stage theStage;

    /**
     * This method overrides the start method and initializes
     * variables/values to begin the game.
     */
    
   @Override public void start(Stage theStage) {
        reversiLogo = new Image(getClass().getResource("/resources/newReversiLogo.png").toExternalForm());
        potenS = new Image("resources/potentialSpace.JPG");
        emptyS = new Image("resources/emptySpace.JPG");
        player1S = new Image(p1ColorPackage.getColorStrings()[0]);
        player2S = new Image(p2ColorPackage.getColorStrings()[0]);
        //reversiLogo = new Image("resources/newReversiLogo.png");

        player2EX = new ImageView(player2S);
        player1EX = new ImageView(player1S);
        emptyEX = new ImageView(emptyS);
        potEX = new ImageView(potenS);
        theLogo = new ImageView(reversiLogo);
        pSpaceToggle = new ImageView(potenS);

        aiQuery = new Alert(AlertType.CONFIRMATION, "Would You Like to Enable the AI?", yesAI, noAI);
        player1Wins = new Alert(AlertType.INFORMATION, "Player 1 Wins!", exitDialog);
        player2Wins = new Alert(AlertType.INFORMATION, "Player 2 Wins!", exitDialog);
        draw = new Alert(AlertType.INFORMATION, "It's a Draw!", exitDialog);
        insuffMovesP1 = new Alert(AlertType.NONE, "No valid moves remain for Player 1\n GAME OVER", exitDialog);
        insuffMovesP2 = new Alert(AlertType.NONE, "No valid moves remain for Player 2\n GAME OVER", exitDialog);

        player1Tracker = new Text("P1 Score: " + player1Count);
        player2Tracker = new Text("P2 Score: " + player2Count);
        currentTurn = new Text("Player 1");	//player1 player always goes first
        colorIndicator = new Text("Current Turn: " + currentTurn.getText());
        status = new Text("Player's turn");
        player1Color = new Text("Player 1 Color:");
        player2Color = new Text("Player 2 Color:");

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
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 3) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 3)) {
                    imgGrid[i][i2].setImage(player1S);
                } //if top-left of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 4) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 4)) {
                    imgGrid[i][i2].setImage(player1S);
                } //if bottom-right of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 4) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 3)) {
                    imgGrid[i][i2].setImage(player2S);
                } // if top-right of initial setup
                if ((GridPane.getColumnIndex(imgGrid[i][i2]) == 3) &&
                    (GridPane.getRowIndex(imgGrid[i][i2]) == 4)) {
                    imgGrid[i][i2].setImage(player2S);
                } //if bottom-left of initial setup
            } //for
        } //for loop assigning ImageViews to GridPane
        root.getChildren().addAll(logoBar, exitBar, topMenu, colorPickerBox, mainPane, turnTracker, bottom);
        setUpScene();
        showPotentials();
    } //start method 
    
    /**
     * This method sets the actions for an ImageView upon being clicked.
     * It is primarily intended to player1uce the bloat of the event handler
     * introduced in the constructor.
     * @param iv the ImageView on which the actions will be carried out
     */
    
    private void playerAction(ImageView iv) {
    	 if (checkValidPlacement(iv) == true) {
    		 clicked = true;
             reverseColors(iv);
             setImage(iv);
             if (currentTurn.getText().equals("Player 2")) {
                 countPlayer1();
                 countPlayer2();
             } //if player1 played
             
             if (currentTurn.getText().equals("Player 1")) {
                 countPlayer2();
                 countPlayer1();
             } //if player2 played
             
             numOfValidMoves();
             
             if (countEmpties() == 0) {
                 determineEnd();
             } //if all spaces used
         } //if user-clicked space is valid per color
         hidePotentials(potentialSpaceVisible);
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
    	
        if (currentTurn.getText().equals("Player 1")) {
            iv.setImage(player1S);
            currentTurn.setText("Player 2");
            colorIndicator.setText("Current turn: " + currentTurn.getText());
            colorIndicator.setFill(p2ColorPackage.getColor());
            return;
        } //if player1 when called, changing to player2
        if (currentTurn.getText().equals("Player 2")) {
            iv.setImage(player2S);
            currentTurn.setText("Player 1");
            colorIndicator.setText("Current turn: " + currentTurn.getText());
            colorIndicator.setFill(p1ColorPackage.getColor());
            return;
        } //if player2 when called, changing to player1
    } //setImage

    /**
     * This method searches through the 2D array of ImageViews (instance var)
     * in search of ImageViews that have an {@code Image} of Player 1 and counts them.
     */
    
    private void countPlayer1() {
        player1Count = 0;
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (imgGrid[i][i2].getImage().equals(player1EX.getImage())) {
                    player1Count++;
                } //if
            } //for
        } //for
        player1Tracker.setText("P1 Score: " + player1Count);
    } //countPlayer1

    /**
     * This method searches thorugh the 2D array of ImageViews (instance var)
     * in search of ImageViews that have an {@code Image} of Player 2 and counts them.
     */
    
    private void countPlayer2() {
        player2Count = 0;
        for (int i = 0; i < 8; i++) {
            for (int i2 = 0; i2 < 8; i2++) {
                if (imgGrid[i][i2].getImage().equals(player2EX.getImage())) {
                    player2Count++;
                } //if
            } //for
        } //for
        player2Tracker.setText("P2 Score: " + player2Count);
    } //countPlayer2

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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col][row - 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player1 to confirm placement
            } //if a player2 is found adjacent
        } //if current color is player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col][row - 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for loop checking for another player2 to confirm placement
            } //if a player1 is found adjacent
        } //if current color is player2 (check for player1 than another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col][row + 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player1 to confirm placement
            } //if a player2 is found adjacent
        } //if current color is player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col][row + 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player2 to confirm placement
            } //if a player1 is found adjacent
        } //if current color is player2 (check for player1 then another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col - 1][row].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row; i >= 0; i--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if empty space is found
                } //for searching for another player1 to confirm placement
            } //if a player2 is found adjacent
        } //if current color is player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col - 1][row].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row; i >= 0; i--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player2 to confirm placement
            } //if player1 found adjacent
        } //if current color is player2 (check for player1 then another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col + 1][row].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row; i <= 7; i++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player1 to confirm placement
            } //if a player2 is found adjacent
        } //if current color is player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col + 1][row].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row; i <= 7; i++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player2 to confirm pacement
            } //if a player1 is found adjacent
        } //if current color is player2 (check for player1 then another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col + 1][row - 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player1 to confirm placement
            } //if player2 found adjacent
        } //if current color is player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col + 1][row - 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for another player2 to confirm placement
            } //if player1 found adjacent
        } //if current color is player2 (check for player1 then another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col + 1][row + 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player1 to confirm placement
            } //if player2 found adjacent
        } //if current color player1 (check for player2 then another player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col + 1][row + 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player2 to confirm placement
            } //if player1 found adjacent
        } //if current color is player2 (check for player1 then another player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col - 1][row - 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player1 to confirm placement
            } //if player2 found adjacent
        } //if current turn is player1 (check for player2 then player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col - 1][row - 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player2 to confirm placement
            } //if player1 found adjacent
        } //if current turn is player2 (check for player1 then player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            if (imgGrid[col - 1][row + 1].getImage()
                .equals(player2EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player1EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player1 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player1 to confirm placement
            } //if player2 found adjacent
        } //if current turn player1 (check for player2 then player1)
        if (currentTurn.getText().equals("Player 2")) {
            if (imgGrid[col - 1][row + 1].getImage()
                .equals(player1EX.getImage())) {
                oppFound = true;
                for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                    if (imgGrid[i][i2].getImage()
                        .equals(player2EX.getImage())) {
                        sameFound = true;
                        break;
                    } //if closing player2 found
                    if (imgGrid[i][i2].getImage()
                        .equals(emptyEX.getImage()) ||
                        imgGrid[i][i2].getImage().equals(potEX.getImage())) {
                        sameFound = false;
                        break;
                    } //if delimiter space found
                } //for searching for second player2 to confirm placement
            } //if player1 found adjacent
        } //if current turn player2 (check for player1 then player2)
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 is found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col, i2 = row - 1; i2 >= 0; i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
        
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } // if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col, i2 = row + 1; i2 <= 7; i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col - 1, i2 = row; i >= 0; i--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col - 1, i2 = row; i >= 0; i--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if whtie found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col + 1, i2 = row; i <= 7; i++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col + 1, i2 = row; i <= 7; i++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col + 1, i2 = row - 1; i <= 7 && i2 >= 0; i++, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col + 1, i2 = row + 1; i <= 7 && i2 <= 7; i++, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col - 1, i2 = row - 1; i >= 0 && i2 >= 0; i--, i2--) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
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
        if (currentTurn.getText().equals("Player 1")) {
            for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    imgGrid[i][i2].setImage(player1S);
                    continue;
                } //if player2 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    break;
                } //if player1 found to cease reversal
            } //for reversing player2s to player1s
        } //if player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            for (int i = col - 1, i2 = row + 1; i >= 0 && i2 <= 7; i--, i2++) {
                if (imgGrid[i][i2].getImage()
                    .equals(player1EX.getImage())) {
                    imgGrid[i][i2].setImage(player2S);
                    continue;
                } //if player1 found for reversal
                if (imgGrid[i][i2].getImage()
                    .equals(player2EX.getImage())) {
                    break;
                } //if player2 found to cease reversal
            } //for reversing player1s to player2s
        } //if player2's turn
        
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
        
        if (currentTurn.getText().equals("Player 1")) {
            int validP1s = 0;
            for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 7; j++) {
                    if (checkValidPlacement(imgGrid[i][j]) == true) {
                        validP1s++;
                    } //if a given space is valid for player1 player
                } //inner for
            } //outer for
            if (validP1s == 0) {
                insuffMovesP1.showAndWait();
                //System.out.println("insuffMovesP1");
                determineEnd();
            } //if no valid moves for player1
        } //if it is player1's turn
        if (currentTurn.getText().equals("Player 2")) {
            int validP2s = 0;
            for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 7; j++) {
                    if (checkValidPlacement(imgGrid[i][j]) == true) {
                        validP2s++;
                    } //if a given space is valid for player2 player
                } //inner for
            } //outer for
            if (validP2s == 0) {
                insuffMovesP2.showAndWait();
               // System.out.println("insuffMovesP2");
                determineEnd();
            } //if no valid moves for player2
        } //if it is player2's turn
    } //numOfValidMoves

    /**
     * This method determines the end result of the game based on 
     * the score of both players. Based on the determined result,
     * an appropriate {@code Alert} will be displayed concerning 
     * the end result of the game, whereupon the stage will close.
     */
    
    private void determineEnd() {

        if (player1Count > player2Count) {
            player1Wins.showAndWait();
            theStage.close();
            System.exit(0);
        } //if player1 has higher score
        if (player1Count < player2Count) {
            player2Wins.showAndWait();
            theStage.close();
            System.exit(0);
        } //if player2 has higher score
        if (player1Count == player2Count) {
            draw.showAndWait();
            theStage.close();
            System.exit(0);
        } //if draw
    } //determineEnd

    /**
     * This method sets up necessary components of
     * the {@code Scene} graph, and player1uces the bulk of the
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

        exit = event -> theStage.close();
        exitButton = new Button("EXIT");
        exitButton.setOnAction(exit);
        exitButton.setTextFill(Color.ANTIQUEWHITE);
        //exitButton.setStyle("-fx-background-color: #000000");
        exitButton.setStyle("-fx-background-color: #000000; -fx-border-color: #ff0000");
        exitBar = new HBox(50);
        exitBar.setBackground(filler);
        exitBar.setAlignment(Pos.BASELINE_LEFT);
        exitBar.getChildren().add(exitButton);

        colorPickerBox = new HBox(50);
        colorPickerBox.setAlignment(Pos.CENTER);
        colorPickerBox.setBackground(filler);
        validColorStrings = getValidColorStrings();
        p1ColorSelect = new ComboBox<>();
        p2ColorSelect = new ComboBox<>();
        p1ColorSelect.getItems().addAll(validColorStrings);
        p2ColorSelect.getItems().addAll(validColorStrings);
        player1Color.setFill(Color.ANTIQUEWHITE);
        player2Color.setFill(Color.ANTIQUEWHITE);

        // set event for color selection
        p1ColorSelect.setOnAction(event -> {
            String selectedColor = p1ColorSelect.getSelectionModel().getSelectedItem();
            if (selectedColor != null) {
                p1ColorPackage.setFileName(fetchPieceColor(selectedColor));
                changeColors(PlayerID.PLAYER1, p1ColorPackage);
                validColorStrings = getValidColorStrings();
                Platform.runLater(() -> { // IndexOutOfBounds if we don't use runLater
                    p1ColorSelect.getItems().clear();
                    p2ColorSelect.getItems().clear();
                    p1ColorSelect.getItems().addAll(validColorStrings);
                    p2ColorSelect.getItems().addAll(validColorStrings);
                });
            }
        });
        p2ColorSelect.setOnAction(event -> {
            String selectedColor = p2ColorSelect.getSelectionModel().getSelectedItem();
            if (selectedColor != null) {
                p2ColorPackage.setFileName(fetchPieceColor(selectedColor));
                changeColors(PlayerID.PLAYER2, p2ColorPackage);
                validColorStrings = getValidColorStrings();
                Platform.runLater(() -> { // IndexOutOfBounds if we don't use runLater
                    p1ColorSelect.getItems().clear();
                    p2ColorSelect.getItems().clear();
                    p1ColorSelect.getItems().addAll(validColorStrings);
                    p2ColorSelect.getItems().addAll(validColorStrings);
                });
            }
        });

        colorPickerBox.getChildren().addAll(player1Color, p1ColorSelect, player2Color, p2ColorSelect);
    	
        topMenu = new HBox(12);
        topMenu.setBackground(filler);
        topMenu.setAlignment(Pos.BASELINE_CENTER);
        player1Tracker.setFill(Color.ANTIQUEWHITE);
        player2Tracker.setFill(Color.ANTIQUEWHITE);
        topMenu.getChildren().addAll(player1Tracker, player2Tracker);

        potentialToggle = event -> togglePotentials();
        
        turnTracker = new HBox();
        turnTracker.setBackground(filler);
        status.setFill(Color.ANTIQUEWHITE);
        turnTracker.getChildren().add(status);
        turnTracker.setAlignment(Pos.BASELINE_CENTER);

        pSpaceToggle.setFitWidth(50);
        pSpaceToggle.setFitHeight(50);
        pSpaceToggle.setOnMouseClicked(event -> {
            togglePotentials();
        });

        bottom = new HBox(450);
        bottom.setBackground(filler);
        colorIndicator.setFont(new Font(12));
        colorIndicator.setFill(Color.ANTIQUEWHITE);
        colorIndicator.setUnderline(true);
        colorIndicator.setFill(Color.DARKRED);
        colorIndicator.setStroke(Color.ANTIQUEWHITE);
        colorIndicator.setStrokeWidth(0.25);
        bottom.getChildren().addAll(colorIndicator, pSpaceToggle);
        root = new VBox();
    } //setUpOther

    /**
     * This method sets up necessary components of the Scene graph,
     * namely the Scene itself, and calls necessary methods to size the
     * stage to the scene, show the stage, and set the title of the Stage.
     */
    
    private void setUpScene() {
    	theStage = new Stage();
        mainScene = new Scene(root);
        theStage.setTitle("Reversi App");
        theStage.setScene(mainScene);
        theStage.show();
        theStage.sizeToScene();
    } //setUpScene

    private void togglePotentials() {
        if (potentialSpaceVisible) {
            potentialSpaceVisible = false;
            hidePotentials(potentialSpaceVisible);
            pSpaceToggle.setImage(emptyS);
        } else {
            potentialSpaceVisible = true;
            showPotentials();
            pSpaceToggle.setImage(potenS);
        }
    }
    
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
     * The {@code boolean} parameter determines whether the method calls {@code showPotentials}
     * after it has hidden all potentials.
     */
    
    private void hidePotentials(boolean visible) {
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
        if (visible) {showPotentials();}
    } //hidePotentials
    
    /**
     * This method simulates play by a second player (player2) by using a random-select AI
     * to play an {@code ImageView} on the game board, just like when a user selects a space.
     * If the AI chooses an invalid space, the method continues execution until a valid space is
     * chosen and gameplay can continue.
     */
    
    private void botPlay() {
    		Random rando = new Random();
    		boolean validChosen = false;
            int[] cornerSpace = collectCorners(rando);
    		while (!validChosen) {
    			int row = rando.nextInt(8);
    			int col = rando.nextInt(8);
                if (cornerSpace.length == 2) {
                    row = cornerSpace[0];
                    col = cornerSpace[1];
                }
    			if (checkValidPlacement(imgGrid[row][col])) {
    				 validChosen = true;
    				 clicked = false;
    				 reverseColors(imgGrid[row][col]);
    	             setImage(imgGrid[row][col]);
    	             if (currentTurn.getText().equals("Player 2")) {
    	                 countPlayer1();
    	                 countPlayer2();
    	             } //if player1 played
    	             
    	             if (currentTurn.getText().equals("Player 1")) {
    	                 countPlayer2();
    	                 countPlayer1();
    	             } //if player2 played
    	             
    	             numOfValidMoves();
    	             
    	             if (countEmpties() == 0) {
    	                 determineEnd();
    	             } //if all spaces used
    			} //if randomly-selected space is a valid move
    			else continue;
    		} //while
    	hidePotentials(potentialSpaceVisible);
    	status.setText("Player's turn");
    } //botPlay

    /**
     * This method is called by the AI's random select to grab a random corner space's coordinates,
     * so long as that corner space is a valid move for the AI. This is because in Reversi/Othello,
     * corner spaces cannot be reversed, so if the AI can target them specifically it will make the game
     * more challenging for the player.
     * @param rando a Random object for choosing a random corner space (if applicable)
     * @return a two-element int array containing corner space coordinates (if applicable)
     */
    private int[] collectCorners(Random rando) {
        Vector<int[]> resultCoordinates = new Vector<>();
        if (checkValidPlacement(imgGrid[0][0])) { // valid placement in top-left corner
            resultCoordinates.add(new int[]{0, 0});
        }
        if (checkValidPlacement(imgGrid[0][7])) { // valid placement in top-right corner
            resultCoordinates.add(new int[]{0, 7});
        }
        if (checkValidPlacement(imgGrid[7][0])) { // valid placement in bottom-left corner
            resultCoordinates.add(new int[]{7, 0});
        }
        if (checkValidPlacement(imgGrid[7][7])) { // valid placement in bottom-right corner
            resultCoordinates.add(new int[]{7, 7});
        }
        if (resultCoordinates.size() > 0) {
            return resultCoordinates.elementAt(rando.nextInt(resultCoordinates.size()));
        }
        return new int[]{};
    }

    /**
     * Given the current color of both player 1 and player 2, determine which colors are available
     * to switch to from the color selection dialogs.
     * @return a Vector<String> of valid color strings
     */
    private Vector<String> getValidColorStrings() {
        Vector<String> validStrings = new Vector<>();
        String p1String = p1ColorPackage.getColorStrings()[1];
        String p2String = p2ColorPackage.getColorStrings()[1];
        for (PieceColor pc : PieceColor.values()) {
            ColorPackage tmp = new ColorPackage(pc);
            String str = tmp.getColorStrings()[1];
            if ((!str.equals(p1String)) && (!str.equals(p2String))) {
                validStrings.add(str);
            }
        }
        return validStrings;
    }

    /**
     * Take a color name as a string and match it to an instance of PieceColor.
     * @param colorName string representing chosen color
     * @return a {@code PieceColor} that represents this chosen color
     */
    private PieceColor fetchPieceColor(String colorName) {
        PieceColor result = PieceColor.WHITE;
        switch (colorName) {
            case "Alpine":
                result = PieceColor.ALPINE;
                break;
            case "Baby Blue":
                result = PieceColor.BABYBLUE;
                break;
            case "Black":
                result = PieceColor.BLACK;
                break;
            case "Blue":
                result = PieceColor.BLUE;
                break;
            case "Brown":
                result = PieceColor.BROWN;
                break;
            case "Orange":
                result = PieceColor.ORANGE;
                break;
            case "Pink":
                result = PieceColor.PINK;
                break;
            case "Purple":
                result = PieceColor.PURPLE;
                break;
            case "Red":
                result = PieceColor.RED;
                break;
        }
        return result;
    }

    /**
     * Upon selecting a new color for player 1 or player 2, use the respective
     * {@code ColorPackage} object and PlayerID to change the color of all relevant game pieces
     * and the turn indicator.
     */
    private void changeColors(PlayerID pid, ColorPackage cpkg) {
        Image newImage = new Image(cpkg.getColorStrings()[0]);
        switch (pid) {
            case PLAYER1: // update all p1 pieces and the turn tracker
                for (int i = 0; i < 8; i++) {
                    for (int ii = 0; ii < 8; ii++) {
                        if (imgGrid[i][ii].getImage() == player1S) {
                            imgGrid[i][ii].setImage(newImage);
                        }
                    }
                }
                player1S = newImage; // update player 1's global image value AFTER updating all pieces
                player1EX.setImage(player1S);
                break;
            case PLAYER2: // update all p2 pieces and the turn tracker
                for (int i = 0; i < 8; i++) {
                    for (int ii = 0; ii < 8; ii++) {
                        if (imgGrid[i][ii].getImage() == player2S) {
                            imgGrid[i][ii].setImage(newImage);
                        }
                    }
                }
                player2S = newImage; // update player 1's global image value AFTER updating all pieces
                player2EX.setImage(player2S);
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

} //Reversi
