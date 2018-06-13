package it.polimi.se2018.client.view.gui;

import it.polimi.se2018.client.view.gui.button.ButtonSquare;
import it.polimi.se2018.mvc.model.Square;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowEnemysSceneController implements Initializable{
    private Square[][] playerWindow;

    @FXML
    private GridPane gridPane;

    @FXML
    private BorderPane borderPane;

    WindowEnemysSceneController(Square[][] playerWindow){
        this.playerWindow = playerWindow;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(int row=0; row < playerWindow.length; row++) {
            for (int col = 0; col < playerWindow[0].length; col++) {
                Image image = new Image(playerWindow[row][col].getConstraintPath());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(35);
                imageView.setFitWidth(35);
                gridPane.add(imageView, col, row);
            }
        }

        borderPane.setStyle("-fx-background-image: url('otherimages/window_no_level.png')");
    }
}
