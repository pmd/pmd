/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner.popups;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import net.sourceforge.pmd.util.fxdesigner.app.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AuxclasspathSetupController implements Initializable {

    private final DesignerRoot designerRoot;

    @FXML
    private Button removeFileButton;
    @FXML
    private Button selectFilesButton;
    @FXML
    private ListView<File> fileListView;
    @FXML
    private Button moveItemUpButton;
    @FXML
    private Button moveItemDownButton;
    @FXML
    private Button setClassPathButton;
    @FXML
    private Button cancelButton;


    public AuxclasspathSetupController(DesignerRoot designerRoot) {
        this.designerRoot = designerRoot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BooleanBinding noSelection = fileListView.getSelectionModel().selectedItemProperty().isNull();

        removeFileButton.disableProperty().bind(noSelection);

        moveItemUpButton.disableProperty().bind(noSelection.or(fileListView.getSelectionModel().selectedIndexProperty().isEqualTo(0)));


        // we can't just map the val because we need an ObservableNumberValue
        IntegerBinding lastIndexBinding = Bindings.createIntegerBinding(() -> fileListView.getItems().size() - 1,
                                                                        Val.wrap(fileListView.itemsProperty()).flatMap(LiveList::sizeOf));

        moveItemDownButton.disableProperty().bind(noSelection.or(fileListView.getSelectionModel().selectedIndexProperty().isEqualTo(lastIndexBinding)));

        fileListView.setCellFactory(DesignerUtil.simpleListCellFactory(File::getName, File::getAbsolutePath));

        selectFilesButton.setOnAction(e -> onSelectFileClicked());
        removeFileButton.setOnAction(e -> onRemoveFileClicked());
        moveItemUpButton.setOnAction(e -> moveUp());
        moveItemDownButton.setOnAction(e -> moveDown());

    }


    private void onSelectFileClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add files to the auxilliary classpath");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Java archives", "*.jar", "*.war", "*.ear"),
                new FileChooser.ExtensionFilter("Java class files", "*.class")
        );
        List<File> files = chooser.showOpenMultipleDialog(designerRoot.getMainStage());
        fileListView.getItems().addAll(files);
    }


    private void onRemoveFileClicked() {
        File f = fileListView.getSelectionModel().getSelectedItem();
        fileListView.getItems().remove(f);
    }


    private void moveUp() {
        moveItem(-1);
    }


    private void moveDown() {
        moveItem(1);
    }


    private void moveItem(int direction) {
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


    /** Displays the popup. */
    public void show(Stage parentStage, List<File> currentItems, Consumer<List<File>> onApply) {

        FXMLLoader fxmlLoader = new FXMLLoader(DesignerUtil.getFxml("auxclasspath-setup-popup.fxml"));

        fxmlLoader.setControllerFactory(type -> {
            if (type == AuxclasspathSetupController.class) {
                return this;
            } else {
                throw new IllegalStateException("Wrong controller!");
            }
        });

        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        fileListView.setItems(FXCollections.observableArrayList(currentItems));

        stage.setTitle("Auxilliary classpath setup");
        stage.initOwner(parentStage);
        stage.initModality(Modality.WINDOW_MODAL);

        setClassPathButton.setOnAction(e -> {
            stage.close();
            onApply.accept(fileListView.getItems());
        });

        cancelButton.setOnAction(e -> stage.close());

        stage.show();
    }
}
