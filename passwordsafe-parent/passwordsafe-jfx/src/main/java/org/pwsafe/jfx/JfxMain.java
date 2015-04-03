package org.pwsafe.jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.pwsafe.lib.datastore.PwsEntryBean;
import org.pwsafe.lib.datastore.PwsEntryStore;

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

    @Override
    public void start(Stage initialStage) throws Exception {
        this.stage = initialStage;
        stage.setTitle("JPwSafe");
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

    public static final JfxMain getApplication(){
        return application;
    }

    public static void main(String[] args){
        application = new JfxMain();
        launch(args);
    }

}