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
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class ControlsController {
    Stage stage;
    Robot robot;
    Instant timeSinceLastCapture;
    String capturedText;
    final long cooldown = 10000;
    // List of all hiragana used in furigana
    public static final String hiraganaList = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわを" +
            "がぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽ" +
            "んゃゅょっ ";

    public ControlsController(Stage stage){
        this.stage = stage;
        robot = new Robot();
        this.timeSinceLastCapture = Instant.now().minusMillis(cooldown);
        capturedText = "";
    }

    public void addCloseOperation(Button button){
        button.setOnAction(actionEvent -> Platform.exit());
    }

    public void addContentCaptureOperation(Button button){
        button.setOnAction(actionEvent -> contentCapture());
    }

    public void addScreenGrabOperation(Button button) {button.setOnAction(actionEvent -> screenGrab());}

    private void contentCapture(){
        // get bounds of content
        WritableImage capture = new WritableImage((int)stage.getWidth(), (int)stage.getHeight());
        Node content = stage.getScene().getRoot().getChildrenUnmodifiable().get(0);
        final double x = stage.getX() + content.getLayoutX(),
                     y = stage.getY() + content.getLayoutY(),
                     width = content.getBoundsInParent().getWidth(),
                     // I use stage.getHeight() since the bounds height has a minimum accounting for the button heights
                     height = stage.getHeight() - 7;

        // capture content
        capture = robot.getScreenCapture(null, x, y, width, height);

        // extract text from content to clipboard
        try{
            File captureFile = writeImage(capture, "capture", "png");
            extractTextFromImage(captureFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void screenGrab(){
        // get bounds of all screens
        int maxX = 0;
        int maxY = 0;
        ObservableList<Screen> screens = Screen.getScreens();
        for (Screen screen : screens){
            if(maxX < screen.getBounds().getMaxX())
                maxX = (int)screen.getBounds().getMaxX();
            if(maxY < screen.getBounds().getMaxY())
                maxY = (int)screen.getBounds().getMaxY();
        }

        // capture all screens; requires all screens to be same resolution and scaling
        WritableImage wholeScreen = robot.getScreenCapture(null, 0, 0, maxX, maxY);

        try{
            File wholeScreenImageFile = writeImage(wholeScreen, "wholeScreenCapture", "png");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    // Basic Furigana Stripping Method
    //   strips all furigana and removes newline characters for easy pasting and flashcard creation
    //   always preserves the last line since it might be short and only have hiragana words
    //   produces an incorrect copy when there is a line of pure hiragana with no punctuation in the passage
    //    or when there is irregular furigana such as katakana for stylization
    private String stripFurigana(String passage){
        String[] lines = passage.split("\n");
        StringBuilder cleanedPassage = new StringBuilder();

        for(int i = 0; i < lines.length - 1; i++){
            String line = lines[i];
            for(int j = 0; j < line.length(); j++){
                if(hiraganaList.indexOf(line.charAt(j)) == -1){
                    cleanedPassage.append(line);
                    break;
                }
            }
        }
        cleanedPassage.append(lines[lines.length - 1]);
        return cleanedPassage.toString();
    }

    private String removeNewlines(String passage){
        String[] lines = passage.split("\n");
        StringBuilder cleanedPassage = new StringBuilder();

        for(String line : lines){
            cleanedPassage.append(line);
        }

        return cleanedPassage.toString();
    }

    private void storeAndAddToClipboard(String passage){
        // adds text in succession to previous capture (possibly multiple times)
        // if triggered within the cooldown
        if(Duration.between(timeSinceLastCapture, Instant.now()).toMillis() < cooldown){
            capturedText += "\n\n" + passage;
        } else {
            capturedText = passage;
        }
        timeSinceLastCapture = Instant.now();

        // adds content to clipboard for easy pasting
        StringSelection selection = new StringSelection(capturedText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    // saves image in format "fileName.formatName" and returns the file
    private File writeImage(WritableImage image, String fileName, String formatName) throws IOException {
        File imageFile = new File(System.getProperty("user.dir"));
        imageFile = new File(imageFile, fileName + "." + formatName);
        ImageIO.write(
                SwingFXUtils.fromFXImage(image, null),
                formatName,
                imageFile);

        return imageFile;
    }

    private void extractTextFromImage(File captureFile){
        try {
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

                // returns text passage with line breaks
                String passage = res.getFullTextAnnotation().getText();

                // removes furigana representation from the passage and removes line breaks
//                passage = stripFurigana(passage);
                passage = removeNewlines(passage);

                // replaces English exclamation point and question mark with Japanese equivalent
                passage = passage.replace("!","！").replace("?","？");

                storeAndAddToClipboard(passage);
            }

            vision.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
