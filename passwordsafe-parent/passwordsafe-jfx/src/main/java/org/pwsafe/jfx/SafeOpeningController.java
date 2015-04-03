package org.pwsafe.jfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.pwsafe.lib.datastore.PwsEntryStore;
import org.pwsafe.lib.file.PwsFileFactory;

import java.io.File;

/** Controller of the screen to open a password safe. It manages the selection of a file and the input of the
 * password.
 *
 */
public class SafeOpeningController {

    /** We use this box to place the password field component, as scenebuilder does not know our JpwsPasswordField
     * component.
     */
    @FXML
    private VBox jPasswordFieldBox;

    /** We use an own password field to avoid to create a String containing the password in plain text.
     *
     */
    private JpwsPasswordField jpwsPasswordField;

    @FXML
    private Button selectFileButton;

    public SafeOpeningController(){
    }

    @FXML
    private void initialize(){
        jpwsPasswordField = new JpwsPasswordField();
        jPasswordFieldBox.getChildren().addAll(jpwsPasswordField);
    }

    public void openSafe() throws Exception{
        File passwordSafeFile = JfxMain.getApplication().getPasswordSafeFile();
        openFile(passwordSafeFile, jpwsPasswordField.getPassword(), true);
        jpwsPasswordField.erasePassword();
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
            selectFileButton.setText(selectedFile.getName());
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
            // TODO exception handling - make it better
            e.printStackTrace();
        }
        if (pwsEntryStore != null){
            JfxMain.getApplication().setScene("/fxml/basic/list.fxml", JfxMain.DEFAULT_WIDTH, JfxMain.DEFAULT_HEIGHT);
        }

    }

}
