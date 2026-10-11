package de.Abd.core;
import de.Abd.services.DataService;
import de.Abd.util.Hotkeys;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import de.Abd.services.SceneManager;
import de.Abd.Enum.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class App {

    static SceneManager sceneManager;

    public void launchSceneManager(Stage Mainstage) throws Exception {
        SceneManager sceneManager = new SceneManager(Mainstage);
        App.sceneManager = sceneManager;
        sceneManager.setScene(Screen.SMTPINFO);
    }

    public void launchHotkeys(Stage Mainstage) throws Exception {
        Hotkeys hotkeys = new Hotkeys();
        hotkeys.hotkeysInit(Mainstage, sceneManager);
    }

    App(Stage stage) throws Exception {
        launchSceneManager(stage);
        launchHotkeys(stage);
        DataService.createTables();

    }




    public void alertWindowOnClose(Stage Mainstage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("You're about to close the tab");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
            System.out.println("You successfully logged out!");
            Mainstage.close();

        }


    }

    void start(Stage Mainstage) throws IOException {

        try {
            Mainstage.setTitle("LoginTest");
            Mainstage.setResizable(false);
            Mainstage.setOnCloseRequest(Event -> {
                Event.consume();
                alertWindowOnClose(Mainstage);
            });
        }catch(Exception e){
            System.out.println("Error in start");
        }
    }



    public static SceneManager getSceneManager() {
        return  sceneManager;
    }





}
