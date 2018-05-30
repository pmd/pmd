/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
    private List<File> list = new ArrayList<>();
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

        selectFile.setOnAction(e -> fileSelected());
        removeFiles.setOnAction(e -> setRemoveFiles());

    }


    private void fileSelected() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Files");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Java JARs", "*.jar"),
            new FileChooser.ExtensionFilter("Java WARs", "*.war"),
            new FileChooser.ExtensionFilter("Java EARs", "*.ear"),
            new FileChooser.ExtensionFilter("Java class files", "*.class")
        );
        File file = chooser.showOpenDialog((designerRoot.getMainStage()));
        list.add(file);
        displayFiles();
    }


    private void displayFiles() {

        for (int i = 0; i < list.size(); i++) {
            fileList.refresh();
            fileList.getItems().add(new File(String.valueOf(list.get(i))));
        }
    }


    private void setRemoveFiles() {

        File f = fileList.getSelectionModel().getSelectedItem();
        list.remove(f);
        int selectedId = fileList.getSelectionModel().getSelectedIndex();
        fileList.getItems().remove(selectedId);

    }


    public void showAuxPathWizard() throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(DesignerUtil.getFxml("auxclasspath-setup-popup.fxml.fxml"));

        fxmlLoader.setControllerFactory(type -> {
            if (type == AuxClassPathController.class) {
                return this;
            } else {
                throw new IllegalStateException("Wrong controller!");
            }
        });

        Parent root1 = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root1));
        stage.show();
    }


}
