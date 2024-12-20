package ReversiGame;
import javafx.scene.paint.Color;
/*
 * This class represents all color-related information needed for game rendering if/when
 * the user changes the piece colors mid-game.
 */
public class ColorPackage {
    private String fileName;
    private String colorStr;
    private Color color;

    public ColorPackage(PieceColor pc) {
        setFileName(pc);
    }

    public String[] getColorStrings() {
        String[] info = {this.fileName, this.colorStr};
        return info;
    }

    public Color getColor() {
        return this.color;
    }

    public void setFileName(PieceColor pc) {
        switch (pc) {
            case ALPINE:
                this.fileName = "resources/alpineSpace.jpg";
                this.colorStr = "Alpine";
                this.color = Color.DARKGREEN;
                break;
            case BABYBLUE:
                this.fileName = "resources/babyBlueSpace.jpg";
                this.colorStr = "Baby Blue";
                this.color = Color.LIGHTBLUE;
                break;
            case BLACK:
                this.fileName = "resources/blackSpace.jpg";
                this.colorStr = "Black";
                this.color = Color.BLACK;
                break;
            case BLUE:
                this.fileName = "resources/blueSpace.JPG";
                this.colorStr = "Blue";
                this.color = Color.DODGERBLUE;
                break;
            case BROWN:
                this.fileName = "resources/brownSpace.jpg";
                this.colorStr = "Brown";
                this.color = Color.BROWN;
                break;
            case ORANGE:
                this.fileName = "resources/orangeSpace.jpg";
                this.colorStr = "Orange";
                this.color = Color.ORANGE;
                break;
            case PINK:
                this.fileName = "resources/pinkSpace.jpg";
                this.colorStr = "Pink";
                this.color = Color.MAGENTA;
                break;
            case PURPLE:
                this.fileName = "resources/purpleSpace.jpg";
                this.colorStr = "Purple";
                this.color = Color.PURPLE;
                break;
            case RED:
                this.fileName = "resources/redSpace.JPG";
                this.colorStr = "Red";
                this.color = Color.DARKRED;
                break;
            default:
                this.fileName = "resources/whiteSpace.jpg";
                this.colorStr = "White";
                this.color = Color.ANTIQUEWHITE;
        }
    }
}