package de.Abd.services;

import javafx.scene.control.PasswordField;
import org.mindrot.jbcrypt.BCrypt;
import me.gosimple.nbvcxz.*;
import me.gosimple.nbvcxz.scoring.Result;

public class UserPasswordService {

    private static final Nbvcxz nbvcxz = new Nbvcxz();

    /*
     * Reihenfolge des Arrays:
     * 0 = Kleinbuchstaben
     * 1 = Großbuchstaben
     * 2 = Zahlen
     * 3 = Sonderzeichen
     */
    static int[] countCharacterTypes(String password) {

        int lowerCaseCount = 0;
        int upperCaseCount = 0;
        int digitCount = 0;
        int specialCharacterCount = 0;

        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                upperCaseCount++;
            } else if (Character.isLowerCase(currentChar)) {
                lowerCaseCount++;
            } else if (Character.isDigit(currentChar)) {
                digitCount++;
            } else {
                specialCharacterCount++;
            }
        }

        return new int[]{
                lowerCaseCount,
                upperCaseCount,
                digitCount,
                specialCharacterCount
        };
    }

    static String validatePasswordRules(String password) {

        if (password == null || password.isBlank()) {
            return "Password field is empty!";
        }

        if (password.length() < 8 && password.length() > 1) {
            return "Password is too short!";
        }

        if (password.length() > 64) {
            return "Password is too long!";
        }

        int[] characterTypes = countCharacterTypes(password);

        int lowerCaseCount = characterTypes[0];
        int upperCaseCount = characterTypes[1];
        int digitCount = characterTypes[2];
        int specialCharacterCount = characterTypes[3];

        if (upperCaseCount == 0) {
            return "Password needs at least one uppercase letter!";
        }

        if (lowerCaseCount == 0) {
            return "Password needs at least one lowercase letter!";
        }

        if (digitCount == 0) {
            return "Password needs at least one digit!";
        }

        if (password.contains(" ")) {
            return "Password isn`t allowed to have spaces";
        }


        Nbvcxz nbvcxz = new Nbvcxz();
        Result result = nbvcxz.estimate(password);

        if (!(result.getBasicScore() >= 2)){
            return "Password too Weak!";
        }



        return null;
    }

    static public boolean validatePassword(PasswordField passwordField) {
        if (passwordField == null) {
            return false;
        }

        return validatePasswordRules(passwordField.getText()) == null;
    }

    static public String getPasswordError(PasswordField passwordField) {
        return validatePasswordRules(passwordField.getText());
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String passwordHash) {
        if (password == null || passwordHash == null) {
            return false;
        }

        return BCrypt.checkpw(password, passwordHash);
    }

    public static String passwordScore(PasswordField passwordField) {
        Result result = nbvcxz.estimate(passwordField.getText().trim().toLowerCase());
        if(result.getBasicScore() == 0) {
            return "Very Weak";
        } else if(result.getBasicScore() == 1) {
            return "Weak";
        } else if(result.getBasicScore() == 2) {
            return "Fair";
        } else if(result.getBasicScore() == 3) {
            return "Strong";
        } else if(result.getBasicScore() == 4) {
            return "Very Strong";
        } else return null;
    }


}