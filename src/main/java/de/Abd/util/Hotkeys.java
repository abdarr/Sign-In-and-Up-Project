package de.Abd.util;
import de.Abd.Enum.Screen;
import de.Abd.services.SceneManager;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class Hotkeys {

    public void addSceneHotkey(Stage stage, SceneManager  sceneManager, Screen screen, KeyCode keyCode) {

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == keyCode) {
                try {
                    sceneManager.setScene(screen);
                    event.consume();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }


    public void hotkeysInit(Stage stage, SceneManager sceneManager) {
        addSceneHotkey(stage, sceneManager,Screen.SMTPINFO,KeyCode.F3);
    }


}
