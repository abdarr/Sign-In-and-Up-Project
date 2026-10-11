package de.Abd.ui.ScenesControllers;

import de.Abd.core.App;
import de.Abd.services.*;
import de.Abd.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

import static de.Abd.Enum.Screen.EMAILVERIFICATION;
import de.Abd.ui.ScenesControllers.EmailVerificationScene;

import static de.Abd.Enum.Screen.LOGIN;
import static de.Abd.services.DataService.CheckVerificationCode;

public class EmailVerificationScene {
    static String emailorusername;
    SceneManager sceneManager;


    @FXML
    private Button sumbitButton;
    @FXML
    private TextField CodeField;
    @FXML
    private Hyperlink resendLink;

    @FXML

    private void CheckCode() throws SQLException, IOException {
        String CVC = CheckVerificationCode(CodeField.getText(),emailorusername);

        if (CVC == null){
            System.out.println("user verification successful");
            sceneManager.setScene(LOGIN);

        }else  {
            System.out.println("user verification failed");
            Ui.setValidationStyle(CodeField,false);
            Ui.setToolTipButton("#E53935",200,CodeField, "Code not valid");
        }


    }

    @FXML
    private void resendCode() throws SQLException, InterruptedException {
        EmailVerificationService.SendVerificationCode(DataService.getEmailFromUsername(emailorusername));
    }

    @FXML

    private void initialize() {
        sceneManager = App.getSceneManager();
    }




}
