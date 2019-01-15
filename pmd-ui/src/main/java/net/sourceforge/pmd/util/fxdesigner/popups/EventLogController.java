/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.popups;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * A presenter over the {@link net.sourceforge.pmd.util.fxdesigner.model.EventLogger}.
 * There's not necessarily one in the app, it can be garbage collected and recreated.
 * Each of these necessarily has a live UI component though.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class EventLogController extends AbstractController {

    private static final PseudoClass NEW_ENTRY = PseudoClass.getPseudoClass("new-entry");

    private final DesignerRoot designerRoot;
    private final MainDesignerController mediator;

    @FXML
    private TableView<LogEntry> eventLogTableView;
    @FXML
    private TableColumn<LogEntry, LogEntry> logDateColumn;
    @FXML
    private TableColumn<LogEntry, Category> logCategoryColumn;
    @FXML
    private TableColumn<LogEntry, String> logMessageColumn;
    @FXML
    private TextArea logDetailsTextArea;

    private final Var<List<Node>> selectedErrorNodes = Var.newSimpleVar(Collections.emptyList());


    private final Stage myPopupStage;

    // subscription allowing to unbind from the popup.
    private Subscription popupBinding = () -> {};

    public EventLogController(DesignerRoot owner, MainDesignerController mediator) {
        this.designerRoot = owner;
        this.mediator = mediator;
        // the FXML fields are injected and initialize is called in createStage
        this.myPopupStage = createStage(owner.getMainStage());
    }


    // this is only called each time a popup is created
    @Override
    protected void beforeParentInit() {

        popupBinding.unsubscribe();

        logCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        logMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        logMessageColumn.setSortable(false);

        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        logDateColumn.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue()));
        logDateColumn.setCellFactory(column -> new TableCell<LogEntry, LogEntry>() {

            Subscription sub = null;


            @Override
            protected void updateItem(LogEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (sub != null) {
                    sub.unsubscribe();
                }
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(dateFormat.format(item.getTimestamp()));
                    sub = item.wasExaminedProperty()
                              .map(wasExamined -> wasExamined ? null : new FontIcon("fas-exclamation-circle"))
                              .values()
                              .subscribe(graphicProperty()::setValue);
                }
            }
        });

        eventLogTableView.resizeColumn(logMessageColumn, -1);

        logMessageColumn.prefWidthProperty()
                        .bind(eventLogTableView.widthProperty()
                                               .subtract(logCategoryColumn.getPrefWidth())
                                               .subtract(logDateColumn.getPrefWidth())
                                               .subtract(2)); // makes it work

        eventLogTableView.setRowFactory(tv -> {
            TableRow<LogEntry> row = new TableRow<>();
            ChangeListener<Boolean> examinedListener = (obs, oldVal, newVal) -> row.pseudoClassStateChanged(NEW_ENTRY, !newVal);
            row.itemProperty().addListener((obs, previousEntry, currentEntry) -> {
                if (previousEntry != null) {
                    previousEntry.wasExaminedProperty().removeListener(examinedListener);
                }
                if (currentEntry != null) {
                    currentEntry.wasExaminedProperty().addListener(examinedListener);
                    row.pseudoClassStateChanged(NEW_ENTRY, !currentEntry.isWasExamined());
                } else {
                    row.pseudoClassStateChanged(NEW_ENTRY, false);
                }
            });
            return row;
        });

    }


    /**
     * Binds the popup to the rest of the app. Necessarily performed after the initialization
     * of the controller (ie @FXML fields are non-null). All bindings must be revocable
     * with the returned subscription, that way no processing is done when the popup is not
     * shown.
     */
    private Subscription bindPopupToThisController() {

        Subscription binding =
            EventStreams.valuesOf(eventLogTableView.getSelectionModel().selectedItemProperty())
                        .distinct()
                        .subscribe(this::onExceptionSelectionChanges);

        binding = binding.and(
            EventStreams.valuesOf(eventLogTableView.focusedProperty())
                        .successionEnds(Duration.ofMillis(100))
                        .subscribe(b -> {
                            if (b) {
                                mediator.handleSelectedNodeInError(selectedErrorNodes.getValue());
                            } else {
                                mediator.resetSelectedErrorNodes();
                            }
                        })
        );

        binding = binding.and(
            selectedErrorNodes.values().subscribe(mediator::handleSelectedNodeInError)
        );

        SortedList<LogEntry> logEntries = new SortedList<>(designerRoot.getLogger().getLog(), Comparator.reverseOrder());
        eventLogTableView.itemsProperty().setValue(logEntries);
        binding = binding.and(
            () -> eventLogTableView.itemsProperty().setValue(FXCollections.emptyObservableList())
        );

        myPopupStage.titleProperty().bind(this.titleProperty());
        binding = binding.and(
            () -> myPopupStage.titleProperty().unbind()
        );

        return binding;
    }


    private void handleSelectedEntry(LogEntry entry) {
        if (entry == null) {
            selectedErrorNodes.setValue(Collections.emptyList());
            return;
        }

        entry.setExamined(true);

        switch (entry.getCategory()) {
        case OTHER:
            break;
        case PARSE_EXCEPTION:
            // TODO
            break;
        case TYPERESOLUTION_EXCEPTION:
        case SYMBOL_FACADE_EXCEPTION:
            DesignerUtil.stackTraceToXPath(entry.getThrown()).map(mediator::runXPathQuery).ifPresent(selectedErrorNodes::setValue);
            break;
        default:
            break;
        }
    }


    public void showPopup() {
        myPopupStage.show();
        popupBinding = bindPopupToThisController();
    }


    public void hidePopup() {
        myPopupStage.hide();
        popupBinding.unsubscribe();
        popupBinding = () -> {};

    }


    private void onExceptionSelectionChanges(LogEntry newVal) {
        logDetailsTextArea.setText(newVal == null ? "" : newVal.getStackTrace());
        handleSelectedEntry(newVal);
    }


    private Val<String> titleProperty() {
        return designerRoot.getLogger().numNewLogEntriesProperty().map(i -> "Exception log (" + (i > 0 ? i : "no") + " new)");
    }


    private Stage createStage(Stage mainStage) {
        FXMLLoader loader = new FXMLLoader(DesignerUtil.getFxml("event-log.fxml"));
        loader.setController(this);

        final Stage dialog = new Stage();
        dialog.initOwner(mainStage.getScene().getWindow());
        dialog.initModality(Modality.NONE);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        return dialog;
    }

}
