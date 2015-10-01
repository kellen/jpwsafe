package org.pwsafe.jfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pwsafe.lib.datastore.PwsEntryStore;
import org.pwsafe.lib.file.PwsFileFactory;
import org.pwsafe.util.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** Controller of the screen to open a password safe. It manages the selection of a file and the input of the
 * password.
 *
 */
public class SafeOpeningController {

    private static Log log = LogFactory.getLog(SafeOpeningController.class);

    // TODO add to mru erst nach erfolgreichem öffnen

    /** We use this box to place the password field component, as scenebuilder does not know our JpwsPasswordField
     * component.
     */
    @FXML
    private VBox jPasswordFieldBox;

    /** We use an own password field to avoid to create a String containing the password in plain text.
     *
     */
    private JpwsPasswordField jpwsPasswordField;

    /*
    @FXML
    private Button selectFileButton;
    */
    @FXML
    private ChoiceBox<String> selectFileChoice;

    @FXML
    private Button openSafeButton;

    private ObservableList<String> selectFileChoices;
    private String openOtherOption;
    private String createNewOption;

    public SafeOpeningController(){
    }

    @FXML
    private void initialize() {
        jpwsPasswordField = new JpwsPasswordField();
        jPasswordFieldBox.getChildren().addAll(jpwsPasswordField);
        initSelectFileChoices();
        selectFileChoice.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        handleSelectFileChoiceChange(oldValue, newValue);
                    }
                }
        );
        /*
        selectFileButton.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                selectFile();
            }
        });
        */
        openSafeButton.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openSafe();
            }
        });
        jpwsPasswordField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openSafe();
            }
        });
        jpwsPasswordField.requestFocus();
    }

    private void initSelectFileChoices(){
        // init selectFile Choice
        UserPreferences userPreferences = JfxMain.getApplication().getUserPreferences();
        List<String> mruFiles = userPreferences.getMRUFiles();
        selectFileChoices = FXCollections.observableArrayList(mruFiles);
        String openOtherOptionValue = "open OTHER";
        // TODO hier später localization
        openOtherOption = openOtherOptionValue;
        String createNewOptionValue = "create NEW";
        // TODO hier später localization
        createNewOption = createNewOptionValue;
        selectFileChoices.add(openOtherOption);
        selectFileChoices.add(createNewOption);
        selectFileChoice.setItems(selectFileChoices);
        selectFileChoice.setValue(userPreferences.getMRUFile());
        File passwordSafeFile = new File(userPreferences.getMRUFile());
        JfxMain.getApplication().setPasswordSafeFile(passwordSafeFile);
        /* jetzt default focus auf Eingabefeld
        if(selectFileChoices.size() > 2){ // wenn mehr als nur create New und openOther
            jpwsPasswordField.requestFocus();
        }
        */
    }

    private void handleSelectFileChoiceChange(Number oldValue, Number newValue){
        try {
            String selectedValue = selectFileChoices.get(newValue.intValue());
            UserPreferences userPreferences = JfxMain.getApplication().getUserPreferences();
            if (openOtherOption.equals(selectedValue)) {
                selectFile();
                File selectedFile = JfxMain.getApplication().getPasswordSafeFile();
                if (selectedFile != null) {
                    userPreferences.setMostRecentFilename(selectedFile.getCanonicalPath());
                    selectFileChoice.setValue(selectedFile.getName());
                    jpwsPasswordField.requestFocus();
                }
            } else if (createNewOption.equals(selectedValue)) {
                selectFile();
                // TODO implementieren
                // TODO setzte neuen MRU file
            } else {
                File passwordSafeFile = new File(selectedValue);
                JfxMain.getApplication().setPasswordSafeFile(passwordSafeFile);
                userPreferences.setMostRecentFilename(passwordSafeFile.getCanonicalPath());
                jpwsPasswordField.requestFocus();
            }
        }
        catch (IOException ioe){
            if(log.isDebugEnabled()){
                log.debug(ioe);
            }
        }
    }

    public void openSafe(){
        try {
            File passwordSafeFile = JfxMain.getApplication().getPasswordSafeFile();
            openFile(passwordSafeFile, jpwsPasswordField.getPassword(), true);
        }
        catch(Exception e){
            if (log.isDebugEnabled()){
                log.debug(e);
            }
        }
        finally{
            jpwsPasswordField.erasePassword();
        }
    }

    public void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Password Safe File");
        String initialDirectoryName = System.getProperty("user.home");
        File initialDirectory = new File(initialDirectoryName);
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPwSafe Dateien", "*.psafe3", "*.psafe"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            JfxMain.getApplication().setPasswordSafeFile(selectedFile);
            // selectFileButton.setText(selectedFile.getName());
            selectFileChoice.setValue(selectedFile.getName());
            jpwsPasswordField.requestFocus();
        }
    }

    /**
     * Opens a password safe from the file system.
     *
     * @param file
     * @param password
     * @param forReadOnly
     * @throws Exception if bad things happen during open
     */
    public void openFile(final File file, final StringBuilder password,
                         final boolean forReadOnly) throws Exception {

        JfxMain.getApplication().setPwsEntryStore(null);
        String fileName = file.getCanonicalPath();
        PwsEntryStore pwsEntryStore = null;
        try {
            pwsEntryStore = PwsFileFactory.loadStore(fileName, password);
            JfxMain.getApplication().setPwsEntryStore(pwsEntryStore);
        }
        catch(Exception e){
            if (log.isDebugEnabled()){
                log.debug(e);
            }
        }
        if (pwsEntryStore != null){
            //JfxMain.getApplication().setScene("/fxml/basic/list.fxml", JfxMain.DEFAULT_WIDTH, JfxMain.DEFAULT_HEIGHT);
            JfxMain.getApplication().setScene("/fxml/basic/table.fxml", 800, 600);
        }

    }

}
