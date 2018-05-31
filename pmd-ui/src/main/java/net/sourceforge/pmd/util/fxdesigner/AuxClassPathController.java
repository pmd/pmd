/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AuxClassPathController implements Initializable, SettingsOwner {

    private final DesignerRoot designerRoot;

    @FXML
    private Button removeFiles;
    @FXML
    private Button selectFile;
    @FXML
    private ListView<File> fileList;
    @FXML
    private final MainDesignerController parent;


    public AuxClassPathController(DesignerRoot designerRoot, MainDesignerController parent) {
        this.designerRoot = designerRoot;
        this.parent = parent;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectFile.setOnAction(e -> onSelectFileClicked());
        removeFiles.setOnAction(e -> onRemoveFileClicked());

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
        List<File> file = chooser.showOpenMultipleDialog((designerRoot.getMainStage()));
        for (File f : file) {
            fileList.getItems().add(f);
        }
    }


    private void onRemoveFileClicked() {
        File f = fileList.getSelectionModel().getSelectedItem();
        fileList.getItems().remove(f);
    }


    public void showAuxPathWizard() throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(DesignerUtil.getFxml("auxclasspath-setup-popup.fxml"));

        fxmlLoader.setControllerFactory(type -> {
            if (type == AuxClassPathController.class) {
                return this;
            } else {
                throw new IllegalStateException("Wrong controller!");
            }
        });

        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initOwner(designerRoot.getMainStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root1));
        stage.show();
    }


}
