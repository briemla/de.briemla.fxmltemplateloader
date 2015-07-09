package de.briemla.fxmltemplateloader;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MethodHandleController {

    @FXML
    private VBox testId;
    private boolean handlerInvoked;

    @FXML
    public void handleMouseClick(MouseEvent event) {
        handlerInvoked = true;
    }

    public boolean isHandlerInvoked() {
        return handlerInvoked;
    }
}
