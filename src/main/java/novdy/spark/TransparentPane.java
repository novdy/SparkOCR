package novdy.spark;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TransparentPane extends GridPane {
    private double mouseAnchorX;
    private double mouseAnchorY;

    TransparentPane(Stage stage){
        super();

//        setOnMousePressed(mouseEvent -> {
//            mouseAnchorX = mouseEvent.getSceneX();
//            mouseAnchorY = mouseEvent.getSceneY();
//        });
//
//        setOnMouseDragged(mouseEvent -> {
//            stage.setX(mouseEvent.getScreenX() - mouseAnchorX);
//            stage.setY(mouseEvent.getScreenY() - mouseAnchorY);
//        });

        setStyle("" +
                "-fx-background-color: rgba(255, 255, 255, 0.01);" +
                "-fx-border-color: rgba(0, 0, 0, 0.3);" +
                "-fx-border-width: 3px;"
        );
    }
}
