package de.Abd.ui.ScenesControllers;

import de.Abd.Enum.Screen;
import de.Abd.core.App;
import de.Abd.services.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import de.Abd.services.EmailVerificationService;

import java.io.IOException;

import static de.Abd.Enum.Screen.SIGNIN;
import static de.Abd.Enum.Screen.SMTPINFO;



public class SmtpinfoScene {



    @FXML
    private TextField port;

    @FXML
    private TextField smtpserver;

    @FXML
    private TextField smtpemail;

    @FXML
    private TextField smtpkey;

    @FXML
    private Button submitButton;

    @FXML
    private void submit() throws IOException {


        String path = Screen.SMTPINFO.getPath();

        System.out.println("Gesuchter Pfad: [" + path + "]");
        System.out.println("Gefunden: " + getClass().getResource(path));


            EmailVerificationService.port = port.getText();
            EmailVerificationService.smptserver = smtpserver.getText();
            EmailVerificationService.appUser = smtpemail.getText();
            EmailVerificationService.appPassword = smtpkey.getText();




        System.out.println("Port: " + port.getText());
        System.out.println("SMTP Server: " + smtpserver.getText());
        System.out.println("SMTP Email: " + smtpemail.getText());
        System.out.println("SMTP Key: " + smtpkey.getText());

        SceneManager sceneManager = App.getSceneManager();
        sceneManager.setScene(SIGNIN);
    }
}