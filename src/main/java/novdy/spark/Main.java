package novdy.spark;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main extends Application {
    double mouseAnchorX;
    double mouseAnchorY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);

        GridPane root = new TransparentPane(stage);

        Text text = new Text("");
        Button closeButton = new Button("X");
        Button settingsButton = new Button("S");
        Button cropButton = new Button("C");
        closeButton.setMinWidth(30);
        closeButton.setMinHeight(30);
        settingsButton.setMinWidth(30);
        settingsButton.setMinHeight(30);
        cropButton.setMinWidth(30);
        cropButton.setMinHeight(30);
        cropButton.setMaxHeight(Double.MAX_VALUE);


        closeButton.setOnAction(actionEvent -> {
            Platform.exit();
        });

        root.add(text, 0,0,1,3);
        root.add(closeButton, 1, 0, 1, 1);
        root.add(settingsButton, 1,1,1,1);
        root.add(cropButton, 1,2,1,1);

        GridPane.setHgrow(text, Priority.ALWAYS);
        GridPane.setVgrow(cropButton, Priority.ALWAYS);

        Scene scene = new Scene(root, 960, 270);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        ResizeHelper.addResizeListener(stage);

        stage.show();
    }
}