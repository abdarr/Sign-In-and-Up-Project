package de.Abd.services;
import java.io.IOException;
import java.util.Objects;
import javafx.scene.Parent;
import de.Abd.Enum.Screen;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import javafx.stage.Stage;

public class SceneManager {
    Parent root;
    final Stage Mainstage;
    Screen currentScene;

    public SceneManager(Stage Mainstage){
        this.Mainstage = Mainstage ;
    }

    public void setScene(Screen state) throws IOException{
        if(state == currentScene){return;}
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(state.getPath())));
        Mainstage.setScene(new Scene(root));
        Mainstage.show();
        currentScene = state;



    }







}
