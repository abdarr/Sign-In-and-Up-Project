package de.Abd.Enum;

public enum Screen {
    LOGIN("/Scenes/LogInScene.fxml"),
    SIGNIN("/Scenes/SignInScene.fxml"),
    EMAILVERIFICATION("/Scenes/EmailVerificationScene.fxml"),
    SMTPINFO("/Scenes/smtpExample.fxml");

    private final String path;

     Screen(String path)  {
        this.path = path;
    }

    public String getPath()  {
        return path;

    }


}
