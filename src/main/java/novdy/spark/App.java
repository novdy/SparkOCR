package novdy.spark;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.nio.file.Path;
import java.nio.file.Paths;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class App extends Application {
    double mouseAnchorX;
    double mouseAnchorY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);

        GridPane root = new TransparentPane(stage);
        FlowPane content = new FlowPane();
        VBox buttons = new VBox();
        buttons.setPrefWidth(30);
        buttons.setId("buttons");

        Button closeButton = new Button("X");
        Button settingsButton = new Button("S");
        Button cropButton = new Button("C");
        closeButton.setMaxWidth(Double.MAX_VALUE);;
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.getStyleClass().add("mid-vert");
        cropButton.setMaxWidth(Double.MAX_VALUE);
        cropButton.setPrefHeight(10000);

        closeButton.setOnAction(actionEvent -> {
            Platform.exit();
        });

        for(int i = 0; i < 40; i++){
            content.getChildren().add(new Button("テスト"));
        }

        buttons.getChildren().add(closeButton);
        buttons.getChildren().add(settingsButton);
        buttons.getChildren().add(cropButton);

        root.add(content, 0,0,1,1);
        root.add(buttons,1,0,1,1);
//        root.setGridLinesVisible(true);

        GridPane.setHgrow(content, Priority.SOMETIMES);
        GridPane.setVgrow(buttons, Priority.ALWAYS);

        Scene scene = new Scene(root, 916, 200);

//        scene.getStylesheets().add(Paths.get(resourcePath.toString(), "general.css").toUri().toString());
        scene.getStylesheets().add(getClass().getResource("/general.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setTitle("Spark OCR");
        stage.setAlwaysOnTop(true);
        ResizeHelper.addResizeListener(stage);

        stage.show();
    }
}