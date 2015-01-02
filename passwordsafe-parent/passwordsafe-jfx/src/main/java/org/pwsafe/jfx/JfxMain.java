package org.pwsafe.jfx;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The JavaFx Main Window and Application.
 */
public class JfxMain extends Application {

    //@FXML ScrollPane treeTableViewScrollPane;
    /*
    @FXML
    TreeTableView<PwListEntryJfx> treeTableView;
    @FXML
    TreeTableColumn<PwListEntryJfx, String> titleColumn;
    @FXML TreeTableColumn<PwListEntryJfx, String> userColumn;
    @FXML TreeTableColumn<PwListEntryJfx, String> noteColumn;
    @FXML TreeTableColumn<PwListEntryJfx, String> changeDateColumn;
    */

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Demo08 Tree Table View Sample mit Layout aus fxml");
        URL url = getClass().getResource("/fxml/JfxMain.fxml");
        Parent parent = FXMLLoader.load(url);
        stage.setScene(new Scene(parent, 400, 400));

        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}