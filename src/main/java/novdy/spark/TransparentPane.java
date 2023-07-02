package novdy.spark;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TransparentPane extends GridPane {
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double anchorWidth;
    private double anchorHeight;

    TransparentPane(Stage stage){
        super();

        getStyleClass().add("grid");

        setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getScreenX() - stage.getX();
            mouseAnchorY = mouseEvent.getScreenY() - stage.getY();
            anchorWidth = stage.getWidth();
            anchorHeight = stage.getHeight();
        });

        setOnMouseDragged(mouseEvent -> {
            int border = new ResizeHelper.ResizeListener(stage).getBorder();
            if( mouseAnchorX > border && mouseAnchorX < anchorWidth - border &&
                mouseAnchorY > border && mouseAnchorY < anchorHeight - border)
            {
                stage.setX(mouseEvent.getScreenX() - mouseAnchorX);
                stage.setY(mouseEvent.getScreenY() - mouseAnchorY);
            }
        });
    }
}
