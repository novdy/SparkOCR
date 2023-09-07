package novdy.spark;


import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public final class ControlsController {
    Stage stage;
    Robot robot;

    public ControlsController(Stage stage){
        this.stage = stage;
        robot = new Robot();
    }

    public void addCloseOperation(Button button){
        button.setOnAction(actionEvent -> Platform.exit());
    }

    public void addImageCaptureOperation(Button button){
        button.setOnAction(actionEvent -> imageCapture());
    }

    private void imageCapture(){
        WritableImage capture = new WritableImage((int)stage.getWidth(), (int)stage.getHeight());
        Node content = stage.getScene().getRoot().getChildrenUnmodifiable().get(0);
        final double x = stage.getX() + content.getLayoutX(), y = stage.getY() + content.getLayoutY(),
            width = content.getBoundsInParent().getWidth(), height = content.getBoundsInParent().getHeight() - 1;

        capture = robot.getScreenCapture(capture, x, y, width, height);

        try {
            File captureFile = new File(getClass().getResource("/").toURI());
            captureFile = new File(captureFile, "capture.png");

            ImageIO.write(
                    SwingFXUtils.fromFXImage(capture, null),
                    "png",
                    captureFile);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
