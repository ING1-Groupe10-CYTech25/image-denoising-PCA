package gui;

import gui.view.ImageDenoisePCA;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {
        new ImageDenoisePCA();
    }
    public static void main(String args[]) {
		launch(args);
	}
}
