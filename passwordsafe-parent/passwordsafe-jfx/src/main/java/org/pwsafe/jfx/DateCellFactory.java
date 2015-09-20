package org.pwsafe.jfx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.pwsafe.lib.datastore.PwsEntryBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateCellFactory {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat(); // we use by default the format of the locale

    public static Callback<TableColumn<PwsEntryBean, Date>, TableCell<PwsEntryBean, Date>> getDateCellFactory() {
        return new Callback<TableColumn<PwsEntryBean, Date>, TableCell<PwsEntryBean, Date>>() {

            @Override
            public TableCell<PwsEntryBean, Date> call(TableColumn<PwsEntryBean, Date> param) {
                TableCell<PwsEntryBean, Date> cell = new TableCell<PwsEntryBean, Date>() {

                    @Override
                    public void updateItem(final Date item, boolean empty) {
                        super.updateItem(item, empty);
                        String dateText = "";
                        if (item != null) {
                            dateText = dateFormat.format(item);
                        }
                        setText(dateText);
                    }
                };
                return cell;
            }
        };
    }
}
