package de.Abd.core;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;

import javafx.application.Application;

import javafx.stage.Stage;
import de.Abd.core.App;

public class Main extends Application {
    Stage stage;
    static void main(String[] args) {
        launch(args);
    }

    @Override

   public void start(Stage Mainstage)throws Exception{

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        stage = Mainstage;
        App app = new App(Mainstage);
        app.start(Mainstage);




    }

    public Stage getMainstage(){
        return stage;
    }
}
