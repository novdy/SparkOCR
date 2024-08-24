package novdy.spark;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Controls extends VBox {
    ControlsController controller;
    public Controls(ControlsController controller) {
        super();
        this.controller = controller;

        setPrefWidth(30);
        setId("controls");

        Button closeButton = new Button("X");
        Button settingsButton = new Button("_S");
        Button captureButton = new Button("_C");
        Button grabButton = new Button("_G");

        closeButton.setMaxWidth(Double.MAX_VALUE);
        closeButton.getStyleClass().add("bottom-border");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.getStyleClass().add("bottom-border");
        captureButton.setMaxWidth(Double.MAX_VALUE);
        captureButton.getStyleClass().add("bottom-border");
        grabButton.setMaxWidth(Double.MAX_VALUE);
        //grabButton.setPrefHeight(10000);

        controller.addCloseOperation(closeButton);
        controller.addContentCaptureOperation(captureButton);
        controller.addScreenGrabOperation(grabButton);

//        for(int i = 0; i < 40; i++){
//            content.getChildren().add(new Button("テスト"));
//        }

        getChildren().add(closeButton);
        getChildren().add(settingsButton);
        getChildren().add(captureButton);
        getChildren().add(grabButton);
    }
}
