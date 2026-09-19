package de.Abd.ui.ScenesControllers;

import de.Abd.services.*;
import de.Abd.Enum.Screen;
import de.Abd.core.App;
import de.Abd.services.SceneManager;
import de.Abd.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


import java.io.IOException;
import java.sql.SQLException;

import static de.Abd.util.Ui.setValidationStyle;
import de.Abd.services.EmailVerificationService;


public class SignUpScene {



    private void setPasswordStrength(
            String text,
            String colorCode,
            double width
    ) {
        Color color = Color.web(colorCode);

        passwordstrengthtext.setText(text);
        passwordstrengthtext.setFill(color);

        ColorInput colorInput =
                (ColorInput) progressBar.getEffect();

        if (colorInput != null) {
            colorInput.setWidth(width);
            colorInput.setPaint(color);
        }
    }




    private SceneManager sceneManager;

    @FXML

    private Button SignUpButton;

    @FXML

    private Text passwordstrengthtext;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button logInButton;

    @FXML
    private TextField emailfield;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label errorLabel;
    @FXML
    private void initialize() {
        sceneManager = App.getSceneManager();
    }



    @FXML
    private void logInButtonAction() {
        System.out.println("button pressed");
        try {
            sceneManager.setScene(Screen.LOGIN);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Login-Szene konnte nicht geladen werden",
                    e
            );
        }
    }

    @FXML

    private void signUpAction() throws SQLException, IOException {
       String result = DataService.CheckUserInput(usernameField,passwordField,emailfield);

        if (result == null) {
            DataService.saveUser(usernameField.getText(),passwordField.getText(),emailfield.getText());
            EmailVerificationService ss = new EmailVerificationService();
            ss.sendVerificationCode(emailfield.getText());

            EmailVerificationScene.emailorusername = usernameField.getText();
            sceneManager.setScene(Screen.EMAILVERIFICATION);
        } else {
            System.out.println(result);
        }

    }

    @FXML
    private void emailfieldKeyReleased() throws Exception {
            if(UserEmailService.validateEmail(emailfield.getText()) == null){
                emailfield.setStyle("-fx-border-color: green;");
                Ui.setToolTipButton("#E53935",0,emailfield,"");

            }else{
                emailfield.setStyle("-fx-border-color: red;");
                Ui.setToolTipButton("#E53935",200,emailfield, UserEmailService.validateEmail(emailfield.getText()));
            }

        if(emailfield.getText().isBlank()){
            emailfield.setStyle("");
            Ui.setToolTipButton("#E53935",0,emailfield,"");
        }
    }

    @FXML
    private void usernamekeyreleased() throws Exception {
        if(UsernameService.GetUsernameError(usernameField.getText(),false) == null){ usernameField.setStyle("-fx-border-color: green;");
        }else usernameField.setStyle("-fx-border-color: red;");

        if(usernameField.getText().isBlank()){
            usernameField.setStyle("");
        }

        boolean validUsername =
                UsernameService.validateusername(usernameField.getText());

        if (!validUsername) {
            Ui.setToolTipButton("#E53935",200,usernameField,UsernameService.GetUsernameError(usernameField.getText(),false));
        } else { Ui.setToolTipButton("#E53935",0,usernameField,UsernameService.GetUsernameError(usernameField.getText(),true)); }

    }




    @FXML

    private void passwordFieldAction() throws SQLException, IOException {
        signUpAction();
    }

    @FXML

    private void passwordkr() throws Exception {

        String password = passwordField.getText();

        if (password.isBlank()) {
            passwordField.setStyle("");
            progressBar.setVisible(false);
            passwordstrengthtext.setVisible(false);
            return;
        }

        progressBar.setVisible(true);
        passwordstrengthtext.setVisible(true);

        String result = UserPasswordService.passwordScore(passwordField);

        switch (result) {
            case "Very Weak" ->
                    setPasswordStrength("Very Weak", "#E53935", 30);

            case "Weak" ->
                    setPasswordStrength("Weak", "#FB8C00", 50);

            case "Fair" ->
                    setPasswordStrength("Fair", "#FDD835", 80);

            case "Strong" ->
                    setPasswordStrength("Strong", "#8BC34A", 130);

            case "Very Strong" ->
                    setPasswordStrength("Very Strong", "#2E7D32", 173);

            case null -> {}

            default ->
                    System.out.println("Unbekannte Passwortstärke: " + result);
        }




        boolean validPassword =
                UserPasswordService.validatePassword(passwordField);

        setValidationStyle(passwordField, validPassword);

        if (!validPassword) {
            Ui.setToolTipButton("#E53935",200,passwordField, UserPasswordService.getPasswordError(passwordField));
        } else { Ui.setToolTipButton("#E53935",0,passwordField, UserPasswordService.getPasswordError(passwordField));}


    }
    }




