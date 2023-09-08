package novdy.spark;


import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
            // create and save file
            File captureFile = new File(getClass().getResource("/").toURI());
            captureFile = new File(captureFile, "capture.png");
            ImageIO.write(
                    SwingFXUtils.fromFXImage(capture, null),
                    "png",
                    captureFile);

            // create Google Vision client
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();

            // read image data into memory
            byte[] data = Files.readAllBytes(captureFile.toPath());
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }

//                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                    System.out.format("Text: %s%n", annotation.getDescription());
//                    System.out.format("Position : %s%n", annotation.getBoundingPoly());
//                }

                String text = res.getFullTextAnnotation().getText();
                StringSelection selection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                System.out.format("Text:%n%s%n", text);
            }

            vision.close();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
