/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.reactfx.value.Var;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

public class AuxClassPathController implements Initializable, SettingsOwner {

    private final DesignerRoot designerRoot;

    private final Var<Runnable> onCancel = Var.newSimpleVar(() -> {});
    private final Var<Consumer<List<File>>> onApply = Var.newSimpleVar(l -> {});


    @FXML
    private Button removeFileButton;
    @FXML
    private Button selectFilesButton;
    @FXML
    private ListView<File> fileListView = new ListView<>();
    @FXML
    private Button moveItemUpButton;
    @FXML
    private Button moveItemDownButton;
    @FXML
    private Button setClassPathButton;
    @FXML
    private Button cancelButton;


    public AuxClassPathController(DesignerRoot designerRoot) {
        this.designerRoot = designerRoot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        removeFileButton.disableProperty().bind(fileListView.getSelectionModel().selectedItemProperty().isNull());
        moveItemUpButton.disableProperty().bind(fileListView.getSelectionModel().selectedItemProperty().isNull());
        moveItemDownButton.disableProperty().bind(fileListView.getSelectionModel().selectedItemProperty().isNull());



        selectFilesButton.setOnAction(e -> onSelectFileClicked());
        removeFileButton.setOnAction(e -> onRemoveFileClicked());
        setClassPathButton.setOnAction(e -> onApply.ifPresent(f -> f.accept(fileListView.getItems())));
        moveItemUpButton.setOnAction(e -> moveUp());
        moveItemDownButton.setOnAction(e -> moveDown());
        cancelButton.setOnAction(e -> onCancel.ifPresent(Runnable::run));

    }


    private void onSelectFileClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Files");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Java JARs", "*.jar"),
            new FileChooser.ExtensionFilter("Java WARs", "*.war"),
            new FileChooser.ExtensionFilter("Java EARs", "*.ear"),
            new FileChooser.ExtensionFilter("Java class files", "*.class")
        );
        List<File> files = chooser.showOpenMultipleDialog(designerRoot.getMainStage());
        fileListView.getItems().addAll(files);
    }


    private void onRemoveFileClicked() {
        File f = fileListView.getSelectionModel().getSelectedItem();
        fileListView.getItems().remove(f);
    }


    public void setAuxclasspathFiles(List<File> lst) {
        fileListView.setItems(FXCollections.observableArrayList(lst));
    }


    public void setOnCancel(Runnable run) {
        onCancel.setValue(run);
    }


    public void setOnApply(Consumer<List<File>> onApply) {
        this.onApply.setValue(onApply);
    }


    private void moveUp() {
        moveItem(-1);
    }


    private void moveDown() {
        moveItem(1);
    }


    public void moveItem(int direction) {
        // Checking selected item
        if (fileListView.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        // Calculate new index using move direction
        int newIndex = fileListView.getSelectionModel().getSelectedIndex() + direction;

        if (newIndex < 0 || newIndex >= fileListView.getItems().size()) {
            return;
        }

        File selected = fileListView.getSelectionModel().getSelectedItem();

        // Removing removable element
        fileListView.getItems().remove(selected);
        // Insert it in new position
        fileListView.getItems().add(newIndex, selected);
        //Restore Selection
        fileListView.scrollTo(newIndex);
        fileListView.getSelectionModel().select(newIndex);

    }

}
