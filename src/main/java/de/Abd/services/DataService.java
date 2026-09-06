package de.Abd.services;
import java.sql.*;
import java.util.Objects;

import de.Abd.util.UUIDGenerator;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public  class DataService {

    private static final String url = "jdbc:sqlite:USERS.db";


    static public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    static public String CheckUserInput(TextField username, PasswordField password, TextField email) throws SQLException {

        String passwordScores = UserPasswordService.passwordScore(password);
        String usernames = UsernameService.GetUsernameError(username.getText(), false);

        if (Objects.equals(passwordScores, "Very Weak") || Objects.equals(passwordScores, "Weak")) {
            return "Password is to weak";
        } else if (!UserPasswordService.validatePassword(password)) {
            return UserPasswordService.getPasswordError(password);
        } else if (UserEmailService.validateEmail(email.getText()) != null) {
            return "invalid email address";
        } else return usernames;
    }

    static public boolean isUserVerified(String emailOrUsername) throws SQLException {

        try (Connection connection = connect();PreparedStatement statement = connection.prepareStatement("SELECT email_verified FROM Users WHERE email = ? OR username = ?")) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) {
                throw new SQLException();
            }
            System.out.println(resultSet);

            return resultSet.getInt("email_verified") == 1;

        }
    }


    public void recognizePublicID(String public_id) throws SQLException {

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE public_id = ?")) {
            ps.setString(1, public_id);
            ResultSet rs = ps.executeQuery();
            System.out.println(rs);
        }

    }

    static public int findUserId(String EmailOrUsername) throws SQLException {

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("SELECT id FROM USERS WHERE username= ? OR email = ?")) {
            ps.setString(1, EmailOrUsername);
            ps.setString(2, EmailOrUsername);

            ResultSet rs = ps.executeQuery();

            if(!rs.next()) {
                throw new SQLException();
            }

            return rs.getInt("id");

        }

    }

    static public void CheckVerificationCode(String code,String email) throws SQLException {

        int id = findUserId(email);

        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement("SELECT * FROM verification_codes WHERE code = ? AND user_id = ? AND generated_at >= datetime('now','-10 minutes')")){
            ps.setString(1, code);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Verification code not found or expired");
            }


            verifyUser(id);
        }
    }



    static public void deleteVerificationCode(int id) throws SQLException {
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement("DELETE FROM verification_codes WHERE user_id = ?")
        ){
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    static public void verifyUser(int id) throws SQLException {
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET email_verified = 1 WHERE id = ?")) {


            ps.setInt(1, id);
            ps.executeUpdate();
            deleteVerificationCode(id);

        }
    }

    static public boolean SaveVerificationCode(int verificationCode,String email) throws SQLException {


        try (Connection conn = connect(); PreparedStatement  ps2 = conn.prepareStatement("SELECT id FROM users WHERE email = ?")  ; PreparedStatement ps1 = conn.prepareStatement("INSERT INTO verification_codes(user_id,code) VALUES (?,?)")) {


            ps2.setString(1, email);

            ResultSet id = ps2.executeQuery();


            if (!id.next()) {
                throw new SQLException("User not found");
            }

            int userId = id.getInt("id");



            ps1.setInt(1, userId );
            ps1.setInt(2, verificationCode);

            ps1.executeUpdate();


            return true;
        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        }
    }


    static public void saveUser(String username, String password, String email) throws SQLException {

        UserPasswordService userPasswordService = new UserPasswordService();

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username,passwordhash,email,public_id) VALUES (?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, userPasswordService.hashPassword(password));
            ps.setString(3, email);
            ps.setString(4, UUIDGenerator.generateUUID());
            ps.executeUpdate();
        } catch (SQLException e) {


            if (e.getMessage().contains("users.public_id")) {

                System.out.println("public_id already exists");
                saveUser(username, password, email);
                return;

            }

            throw e;
        }
    }


    static void deleteUser(String username) throws SQLException {
        try (Connection conn = DataService.connect(); PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?");) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    public static boolean recognizeUser(String username, String email) throws SQLException {

        String sql = """
                SELECT username,email
                FROM users
                WHERE username = ? OR email = ?
                LIMIT 1
                """;

        try (
                Connection conn = DataService.connect();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet result = ps.executeQuery()) {
                return result.next();
            }
        }
    }

    public String getPasswordhash(String username, String email) throws SQLException {
        String sql = """
                SELECT passwordhash
                FROM users
                WHERE username = ? OR email = ?
                LIMIT 1;
                """;

        try (Connection conn = DataService.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    return result.getString("passwordhash");
                }
            }
        }

        return null;
    }

    public boolean checkPassword(String usernameOrEmail, String password) throws SQLException {

        String passwordHash = getPasswordhash(usernameOrEmail, usernameOrEmail);

        if (passwordHash == null) {
            return false;
        }

        UserPasswordService userPasswordService = new UserPasswordService();

        return userPasswordService.checkPassword(password, passwordHash);
    }



}





