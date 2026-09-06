package de.Abd.services;

import java.sql.SQLException;

public class UsernameService {

    static public String GetUsernameError (String username , boolean isDeko) throws SQLException {

        if (username == null || username.isBlank()) {

            if(isDeko) return "username field is empty";
            else return null;

        }

        if (username.length() < 6) {
            return "username is too short";
        }

        if (username.length() > 20) {
            return "username is too long";
        }

        if (username.contains(" ")) {
            return "username contains spaces";
        }

        if(DataService.recognizeUser(username, "")){
            return "username is already in use";
        }

        if (username.contains(" ")) {
            return "Username isn`t allowed to have spaces";
        }



        return null;


    }

    static public boolean validateusername(String username) throws SQLException {
        String Result = GetUsernameError(username,true);

        return Result == null;
    }







}
