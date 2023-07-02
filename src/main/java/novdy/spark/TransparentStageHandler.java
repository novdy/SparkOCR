package novdy.spark;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TransparentStageHandler {
    private static double mouseAnchorX;
    private static double mouseAnchorY;

    public static void applyTransparency(GridPane pane){
        pane.setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getSceneX();
            mouseAnchorY = mouseEvent.getSceneY();
        });
    }

}
