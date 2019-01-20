/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

import org.reactfx.value.Var;

import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.util.fxdesigner.popups.EditPropertyDialogController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.PropertyDescriptorSpec;
import net.sourceforge.pmd.util.fxdesigner.util.SoftReferenceCache;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;


/**
 * Controls a table view used to inspect and edit the properties of
 * the rule being built. This component is made to be reused in several
 * views.
 * <p>
 * TODO: would be great to make it directly editable without compromising content validation
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class PropertyTableView extends TableView<PropertyDescriptorSpec> {

    private final TableColumn<PropertyDescriptorSpec, String> propertyNameColumn = new TableColumn<>("Name");
    private final TableColumn<PropertyDescriptorSpec, PropertyTypeId> propertyTypeColumn = new TableColumn<>("Type");
    private final TableColumn<PropertyDescriptorSpec, String> propertyValueColumn = new TableColumn<>("Value");

    private final SoftReferenceCache<Stage> editPropertyDialogCache = new SoftReferenceCache<>(this::createEditPropertyDialog);

    private final Var<Consumer<? super PropertyDescriptorSpec>> onEditCommit = Var.newSimpleVar(null);

    public PropertyTableView() {
        initialize();
    }


    private void initialize() {

        this.getColumns().add(propertyNameColumn);
        this.getColumns().add(propertyTypeColumn);
        this.getColumns().add(propertyValueColumn);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setTableMenuButtonVisible(true);

        ObservableList<PropertyTypeId> availableBuilders = FXCollections.observableArrayList(PropertyTypeId.typeIdsToConstants().values());
        Collections.sort(availableBuilders);
        StringConverter<PropertyTypeId> converter = DesignerUtil.stringConverter(PropertyTypeId::getStringId, PropertyTypeId::lookupMnemonic);
        propertyTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(converter, availableBuilders));
        propertyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        propertyValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        propertyTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeId"));

        this.setPlaceholder(new Label("Right-click to add properties"));

        MenuItem editItem = new MenuItem("Edit...");
        editItem.setOnAction(e -> {
            PropertyDescriptorSpec spec = this.getSelectionModel().getSelectedItem();
            if (spec != null) {
                popEditPropertyDialog(spec);
            }
        });

        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(e -> {
            PropertyDescriptorSpec selected = this.getSelectionModel().getSelectedItem();
            if (selected != null) {
                this.getItems().remove(selected);
            }
        });

        MenuItem addItem = new MenuItem("Add property...");
        addItem.setOnAction(e -> onAddPropertyClicked());

        ContextMenu fullMenu = new ContextMenu();
        fullMenu.getItems().addAll(editItem, removeItem, new SeparatorMenuItem(), addItem);

        // Reduced context menu, for when there are no properties or none is selected
        MenuItem addItem2 = new MenuItem("Add property...");
        addItem2.setOnAction(e -> onAddPropertyClicked());

        ContextMenu smallMenu = new ContextMenu();
        smallMenu.getItems().add(addItem2);

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY
                    || t.getButton() == MouseButton.PRIMARY && t.getClickCount() > 1) {
                if (this.getSelectionModel().getSelectedItem() != null) {
                    fullMenu.show(this, t.getScreenX(), t.getScreenY());
                } else {
                    smallMenu.show(this, t.getScreenX(), t.getScreenY());
                }
            }
        });

        propertyNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        propertyValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.setEditable(false);
    }


    private void onAddPropertyClicked() {
        PropertyDescriptorSpec spec = new PropertyDescriptorSpec();
        this.getItems().add(spec);
        popEditPropertyDialog(spec);
    }


    /**
     * Pops an edition dialog for the given descriptor spec. The dialog is cached and
     * reused, so that it's parsed a minimal amount of time.
     *
     * @param edited The edited property descriptor
     */
    private void popEditPropertyDialog(PropertyDescriptorSpec edited) {
        Stage dialog = editPropertyDialogCache.getValue();
        EditPropertyDialogController wizard = (EditPropertyDialogController) dialog.getUserData();
        Platform.runLater(() -> wizard.bindToDescriptor(edited, getRuleProperties()));
        dialog.setOnHiding(e -> {
            edited.unbind();
            onEditCommit.ifPresent(handler -> handler.accept(edited));
        });
        dialog.show();
    }


    private Stage createEditPropertyDialog() {
        EditPropertyDialogController wizard = new EditPropertyDialogController();

        FXMLLoader loader = new FXMLLoader(DesignerUtil.getFxml("edit-property-dialog.fxml"));
        loader.setController(wizard);

        final Stage dialog = new Stage();
        dialog.initOwner(this.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Scene scene = new Scene(root);
        dialog.setTitle("Edit property");
        dialog.setScene(scene);
        dialog.setUserData(wizard);
        return dialog;
    }


    public ObservableList<PropertyDescriptorSpec> getRuleProperties() {
        return this.getItems();
    }


    public void setRuleProperties(ObservableList<PropertyDescriptorSpec> ruleProperties) {
        this.setItems(ruleProperties);
    }


    public ObjectProperty<ObservableList<PropertyDescriptorSpec>> rulePropertiesProperty() {
        return this.itemsProperty();
    }


    public Consumer<? super PropertyDescriptorSpec> getOnEditCommit() {
        return onEditCommit.getValue();
    }


    public Var<Consumer<? super PropertyDescriptorSpec>> onEditCommitProperty() {
        return onEditCommit;
    }


    public void setOnEditCommit(Consumer<? super PropertyDescriptorSpec> onEditCommit) {
        this.onEditCommit.setValue(onEditCommit);
    }


}
