package de.Abd.ui.ScenesControllers;

import de.Abd.Enum.Screen;
import de.Abd.core.App;
import de.Abd.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

import static de.Abd.Enum.Screen.EMAILVERIFICATION;

public class LogInScene {

    private SceneManager sceneManager;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Hyperlink SignInButton;

    @FXML

    private Button signUpButton;

    @FXML

    private Text errortext;


    @FXML



    private void SignInButtonAction() throws SQLException, IOException {

        String username = usernameField.getText().trim().toLowerCase(); // email can be also used to log in
        String password = passwordField.getText();

        DataService dataService = new DataService();

        if(dataService.checkPassword(DataService.getUserNameFrommEmail(username),password)){
            if(!DataService.isUserVerified(username)){
                EmailVerificationScene.emailorusername = username.trim().toLowerCase();
                EmailVerificationService.SendVerificationCode(DataService.getEmailFromUsername(username));
                sceneManager.setScene(EMAILVERIFICATION);

                return;
            }

            System.out.println("Logged in Successfully");
        }else{
            System.out.println("Password or Email Incorrect");
            signUpButton.setStyle("-fx-border-color: red;");
            errortext.setVisible(true);
        }

    }


    @FXML
    private void initialize() {
        sceneManager = App.getSceneManager();
    }

    @FXML
    private void passwordFieldAction() throws SQLException, IOException {
        SignInButtonAction();
    }

    @FXML
    private void SignButtonAction() {
        System.out.println("button pressed");

        try {
            sceneManager.setScene(Screen.SIGNIN);
        } catch (Exception e) {
            throw new RuntimeException(
                    "SignIn-Szene konnte nicht geladen werden",
                    e
            );
        }
    }

    @FXML

    private void usernameFieldAction() throws SQLException, IOException {

        SignInButtonAction();

    }






}