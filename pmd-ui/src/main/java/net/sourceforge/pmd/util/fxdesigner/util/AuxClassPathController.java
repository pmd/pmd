/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner.util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import net.sourceforge.pmd.util.fxdesigner.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AuxClassPathController implements Initializable, SettingsOwner {

    private final DesignerRoot designerRoot;

    @FXML
    private Button removeFiles;

    @FXML
    private TableView fileTable;

    @FXML
    private Button selectFile;

    @FXML
    private TableColumn fileList;

    @FXML
    private TableColumn fileAdd;

    @FXML
    private final MainDesignerController parent;


    public AuxClassPathController(DesignerRoot designerRoot, MainDesignerController parent) {
        this.designerRoot = designerRoot;
        this.parent = parent;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectFile.setOnAction(e -> fileSeleced());

    }


    private void fileSeleced() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load source from file");
        File file = chooser.showOpenDialog(designerRoot.getMainStage());

    }


    public void showAuxPathWizard() throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/aux-controller.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();

    }


}
