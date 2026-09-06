package de.Abd.Enum;

import java.io.IOException;

public enum Screen {
    LOGIN("/Scenes/Loginscene.fxml"),
    SIGNIN("/Scenes/Signinscene.fxml"),
    EMAILVERIFICATION("/Scenes/EmailVerificationScene.fxml");

    private final String path;

     Screen(String path)  {
        this.path = path;
    }

    public String getPath()  {
        return path;

    }


}
