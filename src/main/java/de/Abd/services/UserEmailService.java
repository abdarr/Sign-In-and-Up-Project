package de.Abd.services;

import org.apache.commons.validator.routines.EmailValidator;

import java.sql.SQLException;


public class UserEmailService {


    public static String validateEmail(String email) throws SQLException {

        if (email == null || email.isBlank()) {
            return "invalid email address";
        }

        if (email.contains(" ")) {
            return "Email isn`t allowed to have spaces";
        }

        email = email.trim();

        if (!EmailValidator.getInstance().isValid(email)) {
            return "invalid email address";
        }

        if(DataService.recognizeUser("", email)){
            return "username is already in use";
        }

        return null;
    }




}