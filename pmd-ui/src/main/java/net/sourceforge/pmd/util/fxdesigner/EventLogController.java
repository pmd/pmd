/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.ResourceBundle;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import net.sourceforge.pmd.util.fxdesigner.model.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;


import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class EventLogController implements Initializable {

    private final DesignerRoot designerRoot;

    private static final Duration PARSE_EXCEPTION_DELAY = Duration.ofMillis(3000);

    @FXML
    private TableView<LogEntry> eventLogTableView;
    @FXML
    private TableColumn<LogEntry, Date> logDateColumn;
    @FXML
    private TableColumn<LogEntry, Category> logCategoryColumn;
    @FXML
    private TableColumn<LogEntry, String> logMessageColumn;
    @FXML
    private TextArea logDetailsTextArea;


    public EventLogController(DesignerRoot owner) {
        this.designerRoot = owner;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        logMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        logDateColumn.setCellValueFactory(
            entry -> new SimpleObjectProperty<>(entry.getValue().getTimestamp()));
        logDateColumn.setCellFactory(column -> new TableCell<LogEntry, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        EventStream<LogEntry> e1 = designerRoot.getLogger().getLog()
                .filter(x -> x.getCategory() == Category.PARSE_EXCEPTION)
                .successionEnds(PARSE_EXCEPTION_DELAY);

        EventStream<LogEntry> e2 = designerRoot.getLogger().getLog()
                .filter(x -> x.getCategory() != Category.PARSE_EXCEPTION);


        EventStream<LogEntry> e3 = designerRoot.getLogger().getLog()
                .filter(x -> x.getCategory() == Category.XPATH_EVALUATION_EXCEPTION)
                .successionEnds(PARSE_EXCEPTION_DELAY);

        EventStreams.merge(e1, e2, e3)
                .subscribe(t -> eventLogTableView.getItems().add(t));


        eventLogTableView
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> logDetailsTextArea.setText(
                newVal == null ? "" : newVal.getStackTrace()));

        eventLogTableView.resizeColumn(logMessageColumn, -1);


        logMessageColumn.prefWidthProperty()
                        .bind(eventLogTableView.widthProperty()
                                               .subtract(logCategoryColumn.getPrefWidth())
                                               .subtract(logDateColumn.getPrefWidth())
                                               .subtract(2)); // makes it work
        logDateColumn.setSortType(SortType.DESCENDING);

    }
}
