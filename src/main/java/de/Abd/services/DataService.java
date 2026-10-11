package de.Abd.services;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;

import de.Abd.util.UUIDGenerator;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public  class DataService {

    private static final Path database = Path.of(
            System.getProperty("user.home"),
            "JavaLoginProjekt",
            "USERS.db"
    );

    static public Connection connect() throws SQLException {

        try {
            Files.createDirectories(database.getParent());
        } catch (IOException e) {
            throw new SQLException("Datenbankordner konnte nicht erstellt werden", e);
        }

        Connection conn = DriverManager.getConnection(
                "jdbc:sqlite:" + database
        );
        try (Statement statement = conn.createStatement()){

            statement.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {
            try {
                conn.close();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }
            System.out.println("Error connecting to database" + e.getMessage());

            throw e;
        }
        return conn;
    }

    static public String CheckUserInput(TextField username, PasswordField password, TextField email) throws SQLException {

        String passwordScores = UserPasswordService.passwordScore(password);
        String usernames = UsernameService.GetUsernameError(username.getText().trim().toLowerCase(), false);

        if (Objects.equals(passwordScores, "Very Weak") || Objects.equals(passwordScores, "Weak")) {
            return "Password is to weak";
        } else if (!UserPasswordService.validatePassword(password)) {
            return UserPasswordService.getPasswordError(password);
        } else if (UserEmailService.validateEmail(email.getText().trim().toLowerCase()) != null) {
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

    static public String getUserNameFrommEmail(String EmailOrUsername) throws SQLException {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("SELECT username FROM USERS WHERE username= ? OR email = ?")){
        ps.setString(1, EmailOrUsername);
        ps.setString(2, EmailOrUsername);

        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {

            throw new SQLException();
        }

        return rs.getString("username");

    }catch (SQLException e){
            return "username not found/"+e.getMessage();
        }

}

    static public String getEmailFromUsername(String EmailOrUsername) throws SQLException {
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("SELECT email FROM USERS WHERE username= ? OR email = ?")){
            ps.setString(1, EmailOrUsername);
            ps.setString(2, EmailOrUsername);

            ResultSet rs = ps.executeQuery();

            if(!rs.next()) {

                throw new SQLException();
            }

            return rs.getString("email");

        }catch (SQLException e){
            return "email not found/"+e.getMessage();
        }
    }

    static public String CheckVerificationCode(String code,String email) throws SQLException {

        int id = findUserId(email);

        if(DataService.isUserVerified(email)) {
            return "user already verified";
        }

        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement("SELECT * FROM verification_codes WHERE code = ? AND user_id = ? AND generated_at >= datetime('now','-10 minutes')")){
            ps.setString(1, code);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Verification code not found or expired");
            }

            conn.close();
            verifyUser(id);
            return null;
        }catch (SQLException e){
            return e.getMessage();
        }
    }

    public static void createTables() throws SQLException {
        try(Connection conn = connect(); Statement ps =  conn.createStatement();Statement ps2 =  conn.createStatement()) {


            ps.executeUpdate("""
            CREATE TABLE IF NOT EXISTS USERS (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            public_id TEXT NOT NULL UNIQUE,
            username TEXT NOT NULL UNIQUE,
            passwordhash TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            email_verified INTEGER NOT NULL DEFAULT 0
            CHECK(email_verified IN (0, 1)) )
            """);

            ps2.executeUpdate("""
	        CREATE TABLE IF NOT EXISTS verification_codes (
	        "user_id"	INTEGER NOT NULL UNIQUE,
	        "code"	TEXT NOT NULL UNIQUE,
	        "generated_at"	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	        PRIMARY KEY("user_id" ),
	        FOREIGN KEY("user_id") REFERENCES "USERS"("id"))"""
            );


        }
    }



    static public void verifyUser(int id) throws SQLException {
        try (Connection conn = connect();PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET email_verified = 1 WHERE id = ?");PreparedStatement ps2 = conn.prepareStatement("DELETE FROM verification_codes WHERE user_id = ?")){


            ps.setInt(1, id);
            ps.executeUpdate();
            ps2.setInt(1, id);
            ps2.executeUpdate();

        }
    }



    static public void SaveVerificationCode(int verificationCode,String email) throws SQLException {


        try (Connection conn = connect(); PreparedStatement  psId = conn.prepareStatement("SELECT id FROM users WHERE email = ?"); // gets the id by using the email address
             PreparedStatement psCheckCode = conn.prepareStatement("SELECT code FROM verification_codes WHERE user_id = ?"); // Checks if a verification code already exists
             PreparedStatement psAddCode = conn.prepareStatement("INSERT INTO verification_codes(user_id,code) VALUES (?,?)"); // Adds the verification code into to the database
             PreparedStatement psUpdateCode = conn.prepareStatement("UPDATE verification_codes SET code = ?, generated_at = CURRENT_TIMESTAMP WHERE user_id = ?"); // Updates the code and the timestamp if the code already exists
        ) {

            psId.setString(1, email);
            ResultSet id = psId.executeQuery();


            if (!id.next()) {
                throw new SQLException("User not found");
            }

            int userId = id.getInt("id");

            psCheckCode.setInt(1, userId);
            ResultSet checkCode = psCheckCode.executeQuery();

            if (!checkCode.next()) {
                psAddCode.setInt(1, userId );
                psAddCode.setInt(2, verificationCode);

                psAddCode.executeUpdate();
                return;
            }

            psUpdateCode.setInt(1, verificationCode);
            psUpdateCode.setInt(2, userId);
            psUpdateCode.executeUpdate();





        }catch (SQLException e){
            throw new SQLException(e.getMessage());
        }
    }




    static public void saveUser(String username, String password, String email) throws SQLException {

        UserPasswordService userPasswordService = new UserPasswordService();

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username,passwordhash,email,public_id) VALUES (?,?,?,?)")) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, userPasswordService.hashPassword(password));
            ps.setString(3, email.trim().toLowerCase());
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





