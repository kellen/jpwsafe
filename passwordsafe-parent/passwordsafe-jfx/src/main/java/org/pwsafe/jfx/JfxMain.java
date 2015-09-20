package org.pwsafe.jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.pwsafe.lib.datastore.PwsEntryStore;
import org.pwsafe.util.UserPreferences;

/**
 * The JavaFx Main Window and Application.
 */
public class JfxMain extends Application {

    public static final int DEFAULT_WIDTH = 240;
    public static final int DEFAULT_HEIGHT = 320;

    private static JfxMain application;
    private static Stage stage;

    private File passwordSafeFile;
    private PwsEntryStore pwsEntryStore;

    private UserPreferences userPreferences;

    @Override
    public void start(Stage initialStage) throws Exception {
        this.stage = initialStage;
        stage.setTitle("JPwSafe");
        Image icon = new Image("/img/iconPwSafeJfx.png");
        stage.getIcons().add(icon);
        setScene("/fxml/basic/openSafe.fxml", DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.show();
    }

    public void setScene(String url, int width, int height) throws IOException {
        URL myUrl = getClass().getResource(url);
        Parent parent = FXMLLoader.load(myUrl);
        Scene scene = new Scene(parent, width, height);
        stage.setScene(scene);
    }

    public File getPasswordSafeFile(){ return this.passwordSafeFile; }
    public void setPasswordSafeFile(File file){ this.passwordSafeFile = file; }

    public PwsEntryStore getPwsEntryStore(){ return this.pwsEntryStore; }
    public void setPwsEntryStore(PwsEntryStore pwsEntryStore){ this.pwsEntryStore = pwsEntryStore; }

    public UserPreferences getUserPreferences() { return this.userPreferences; }
    public void setUserPreferences(UserPreferences userPreferences){ this.userPreferences = userPreferences; }

    public static final JfxMain getApplication(){
        return application;
    }

    public static void main(String[] args){
        application = new JfxMain();
        application.setUserPreferences(UserPreferences.getInstance());
        launch(args);
    }

}