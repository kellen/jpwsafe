package org.pwsafe.jfx.basic;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import org.pwsafe.jfx.JfxMain;
import org.pwsafe.lib.datastore.PwsEntryBean;
import org.pwsafe.lib.datastore.PwsEntryStore;
import org.pwsafe.lib.file.PwsRecord;

/**
 */
public class TableController {

    @FXML
    private TextField filterTextField;

    @FXML
    private TableView<PwsEntryBean> entryTable;

    @FXML
    private TableColumn<PwsEntryBean, String> groupColumn;
    @FXML
    private TableColumn<PwsEntryBean, String> titleColumn;
    @FXML
    private TableColumn<PwsEntryBean, String> usernameColumn;
    @FXML
    private TableColumn<PwsEntryBean, String> descriptionColumn;
    @FXML
    private TableColumn<PwsEntryBean, String> changeColumn;

    private ObservableList<PwsEntryBean> pwEntries;

    @FXML
    private void initialize(){
        PwsEntryStore pwsEntryStore = JfxMain.getApplication().getPwsEntryStore();
        pwEntries = FXCollections.observableArrayList((pwsEntryStore.getSparseEntries()));

        // 0. Initialize the columns.
        groupColumn.setCellValueFactory(cellData    ->     new ReadOnlyObjectWrapper(cellData.getValue().getGroup()));
        titleColumn.setCellValueFactory(cellData    ->     new ReadOnlyObjectWrapper(cellData.getValue().getTitle()));
        usernameColumn.setCellValueFactory(cellData ->     new ReadOnlyObjectWrapper(cellData.getValue().getUsername()));
        descriptionColumn.setCellValueFactory(cellData ->  new ReadOnlyObjectWrapper(cellData.getValue().getNotes()));
        changeColumn.setCellValueFactory(cellData ->       new ReadOnlyObjectWrapper(cellData.getValue().getLastChange()));

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<PwsEntryBean> filteredData = new FilteredList<>(pwEntries, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pwsEntryBean -> {
                // If filter text is empty, display all password entries.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // TODO improve filter: 1.) search not only in title, 2.) avoid toLowerCase() every time (improve algorithm)
                // Compare title with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (pwsEntryBean.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                }
                /* TODO check other properties of the entry for matching
                else if (pwsEntryBean.get...().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                }
                */
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<PwsEntryBean> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(entryTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        entryTable.setItems(sortedData);

        // 6. if there is only one entry left, it is selected automatically
        entryTable.getItems().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("list invalidated");
                if(entryTable.getItems().size() == 1){
                    System.out.println("now a list with 1 items");
                    /* open question, how to style selected row like focused row
                    entryTable.getSelectionModel().selectFirst();
                    ObservableList<TablePosition> selectedCells = null;
                    selectedCells = entryTable.getSelectionModel().getSelectedCells();
                    for (TablePosition tablePosition : selectedCells){
                        TableView<PwsEntryBean> tableView = tablePosition.getTableView();
                        tablePosition.getTableColumn();
                        tablePosition.getTableColumn();
                    }
                    */
                    //entryTable.requestFocus(); // damit verliert Textfeld foucs und man muss wieder
                    // zurücknavigieren
                }
                else{
                    entryTable.getSelectionModel().clearSelection();
                    System.out.println("now a list with " + entryTable.getItems().size() + " entries");
                }
            }
        });
        if (entryTable.getItems().size() == 1){
            entryTable.getSelectionModel().focus(0);
            entryTable.getSelectionModel().select(0);
        }

        // add key listener to table
        entryTable.setOnKeyReleased(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                copyPassword();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.O) {
                copyPasswordAndOpenBrowser();
            }
        });

        /* i don't like it, by now i will stay to use TAB to go to list and then use CTRL+C or CTRL+O */
        filterTextField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                if (entryTable.getItems().size() == 1) {
                    entryTable.getSelectionModel().selectFirst(); // be sure it is selected
                    copyPasswordAndOpenBrowser();
                }
            }
        });
    }

    private void copyPassword(){
        PwsEntryBean entry = getPwsEntryBean();
        copyPassword(entry);
    }

    private void copyPasswordAndOpenBrowser(){
        PwsEntryBean entry = getPwsEntryBean();
        copyPassword(entry);
        openBrowser(entry);
    }

    private PwsEntryBean getPwsEntryBean(){
        PwsEntryStore pwsEntryStore = JfxMain.getApplication().getPwsEntryStore();
        PwsEntryBean pwsEntryBean = entryTable.getSelectionModel().selectedItemProperty().get();
        return pwsEntryStore.getEntry(pwsEntryBean.getStoreIndex());
    }

    private void copyPassword(PwsEntryBean entry) {
        // the password cannot be retrieved from the clipboard if java fx did not store it as string
        // so we are forced to use the string
        String password = entry.getPassword().toString();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.put(DataFormat.PLAIN_TEXT, password);
        boolean success = Clipboard.getSystemClipboard().setContent(clipboardContent);
        System.out.println("Copied password to clipboard");
    }

    private void openBrowser(PwsEntryBean entry){
        String url = entry.getUrl();
        if(url != null) {
            JfxMain.getApplication().getHostServices().showDocument(url);
        }
    }

}
