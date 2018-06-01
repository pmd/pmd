/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.util.ClasspathClassLoader;
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
    private ClassLoader classLoader = getClass().getClassLoader();



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


    public AuxClassPathController(DesignerRoot designerRoot) {
        this.designerRoot = designerRoot;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectFilesButton.setOnAction(e -> onSelectFileClicked());
        removeFileButton.setOnAction(e -> onRemoveFileClicked());
        setClassPathButton.setOnAction(e -> {
            try {
                setClassPath(classPathGenerator());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        moveItemUpButton.setOnAction(e -> moveUp());
        moveItemDownButton.setOnAction(e -> moveDown());
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
        List<File> file = chooser.showOpenMultipleDialog(designerRoot.getMainStage());
        for (File f : file) {
            fileListView.getItems().add(f);
        }
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


    private String classPathGenerator() throws IOException {

        String classPath = "";

        for (File f : fileListView.getItems()) {
            classPath = classPath + ";" + f.getAbsolutePath();
        }

        setClassPath(classPath);
        return classPath;
    }


    public void setClassPath(String classPath) throws IOException {

        if (classLoader == null) {
            classLoader = PMDConfiguration.class.getClassLoader();
        }
        if (classPath != null) {
            classLoader = new ClasspathClassLoader(classPath, classLoader);
        }

        closePopup();
    }


    private void closePopup() {
        Stage stage = (Stage) setClassPathButton.getScene().getWindow();
        stage.close();
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
