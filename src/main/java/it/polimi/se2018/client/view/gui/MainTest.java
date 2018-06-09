package it.polimi.se2018.client.view.gui;

import it.polimi.se2018.mvc.model.Color;
import it.polimi.se2018.mvc.model.Die;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

/**
 * This Class will be deleted, i use it to test specific scenes
 * @author Emilio Imperiali
 * @deprecated
 */
public class MainTest extends Application{

    /**
     * @deprecated
     * @param args args of main
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @deprecated
     * @param primaryStage the stage
     * @throws Exception an exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
       List<List<Die>> roundTracker = Arrays.asList(Arrays.asList(new Die(2, Color.BLUE), new Die(5,Color.PURPLE)),Arrays.asList(new Die(4,Color.GREEN)));
        FXMLLoader loader = new FXMLLoader((getClass().getResource("/scenes/RoundTrackerScene.fxml")));
        RoundTrackerSceneController roundTrackerSceneController = new RoundTrackerSceneController(roundTracker);
        loader.setController(roundTrackerSceneController);
        Parent root = loader.load();
        primaryStage.setTitle("Sagrada Online");
        primaryStage.setScene(new Scene(root, 1892, 396));
        primaryStage.show();


        /*List<String> sortedPlayersNames = Arrays.asList("Emilio", "Cristiano", "Francesco Lorenzo");
        List<Integer> sortedPlayersScores = Arrays.asList(69,42,7);
        FXMLLoader loader = new FXMLLoader((getClass().getResource("/scenes/scoreBoardScene.fxml")));
        ScoreBoardSceneController scoreBoardSceneController = new ScoreBoardSceneController(sortedPlayersNames,sortedPlayersScores);
        scoreBoardSceneController.setGuiClient(new GUIClient());
        scoreBoardSceneController.setStage(primaryStage);
        loader.setController(scoreBoardSceneController);
        Parent root = loader.load();
        primaryStage.setTitle("Sagrada Online");
        primaryStage.setScene(new Scene(root, 600, 623));
        primaryStage.show(); */

    }
}
