package novdy.spark;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        ControlsController controlsController = new ControlsController(stage);

        GridPane root = new TransparentPane(stage);
        FlowPane content = new FlowPane();
        VBox controls = new Controls(controlsController);

        root.add(content, 0,0,1,1);
        root.add(controls,1,0,1,1);
//        root.setGridLinesVisible(true);

        GridPane.setHgrow(content, Priority.SOMETIMES);
        GridPane.setVgrow(controls, Priority.ALWAYS);

        Scene scene = new Scene(root, 916, 138);

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