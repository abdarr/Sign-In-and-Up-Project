package de.Abd.ui.ScenesControllers;

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
import de.Abd.ui.ScenesControllers.EmailVerificationScene;
import static de.Abd.services.DataService.CheckVerificationCode;

public class EmailVerificationScene {
    static String emailorusername;

    @FXML
    private Button sumbitButton;
    @FXML
    private TextField CodeField;
    @FXML
    private Hyperlink resendLink;

    @FXML

    private void CheckCode() throws SQLException {
        String CVC = CheckVerificationCode(CodeField.getText(),emailorusername);

        if (CVC == null){
            System.out.println("user verification successful");
        }else  {
            System.out.println(CVC);
            System.out.println("user verification failed");
        }


    }

    @FXML
    private void resendCode() throws SQLException {
        EmailVerificationService emailVerificationservice = new EmailVerificationService ();
        emailVerificationservice.sendVerificationCode(DataService.getEmailFromUsername(emailorusername));

    }





}
