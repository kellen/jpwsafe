package org.pwsafe.jfx.basic;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
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
 *  Shows the titles of the entries of a password safe as list. As soon as you type in a character, the list
 *  is filtered to the titles containing the text of the filter field on top of the screen. When you go to the
 *  table and select an item, you can copy the password into the clipboard typing CTRL+C and when you type
 *  CTRL+O you password is copied and the URL of the password entry is opened in the system's default web browser.
 */
public class ListController {

    @FXML
    private TextField filterTextField;

    @FXML
    private TableView<PwsEntryBean> entryTable;

    @FXML
    private TableColumn<PwsEntryBean, String> titleColumn;

    private ObservableList<PwsEntryBean> pwEntries;

    @FXML
    private void initialize(){
        PwsEntryStore pwsEntryStore = JfxMain.getApplication().getPwsEntryStore();
        pwEntries = FXCollections.observableArrayList((pwsEntryStore.getSparseEntries()));

        // 0. Initialize the columns.
        titleColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTitle()));

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<PwsEntryBean> filteredData = new FilteredList<>(pwEntries, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pwsEntryBean -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // TODO Filter verbessern, mehr als nur title und algorithmus schneller (z.B. durch to lower vorher
                // Compare title with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (pwsEntryBean.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                }
                /* TODO check other properties of the entry for matching
                else if (pwsEntryBean.get...().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                }
                */
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<PwsEntryBean> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(entryTable.comparatorProperty());

        /*
        doesn+t work may be later another ty
        // 5. if there is only one entry left, it is selected automatically
        sortedData.addListener(new ListChangeListener() {
            public void onChanged(Change c) {
                if(sortedData.size() == 1) {
                    entryTable.getSelectionModel().select(0);
                }
            }
        });
        */

        // 6. Add sorted (and filtered) data to the table.
        entryTable.setItems(sortedData);

        // add key listener to table
        entryTable.setOnKeyReleased(event -> {
            int action = 0; // 0 = nichts, 1 = copy password, 2 copy password and open web browser
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                action = 1;
            }
            if (event.isControlDown() && event.getCode() == KeyCode.O) {
                action = 2;
            }
            if(action > 0){
                PwsEntryBean pwsEntryBean = entryTable.getSelectionModel().selectedItemProperty().get();
                PwsEntryBean newBean = pwsEntryStore.getEntry(pwsEntryBean.getStoreIndex());
                // the password cannot be retrieved from the clipboard if java fx did not store it as string
                // so we are forced to use the string
                String password = newBean.getPassword().toString();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.put(DataFormat.PLAIN_TEXT, password);
                boolean success = Clipboard.getSystemClipboard().setContent(clipboardContent);
                if (action == 2){
                    String url = newBean.getUrl();
                    if(url != null) {
                        JfxMain.getApplication().getHostServices().showDocument(url);
                    }
                }
            }
        });

    }

    }
