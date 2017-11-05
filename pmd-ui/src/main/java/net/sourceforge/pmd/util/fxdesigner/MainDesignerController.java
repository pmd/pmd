/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.LimitedSizeStack;
import net.sourceforge.pmd.util.fxdesigner.util.settings.AppSetting;
import net.sourceforge.pmd.util.fxdesigner.util.settings.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.settings.XMLSettingsLoader;
import net.sourceforge.pmd.util.fxdesigner.util.settings.XMLSettingsSaver;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;


/**
 * Main controller of the app. Mediator for subdivisions of the UI.
 *
 * @author Clément Fournier
 * @see NodeInfoPanelController
 * @see SourceEditorController
 * @see EventLogController
 * @see XPathPanelController
 * @since 6.0.0
 */
public class MainDesignerController implements Initializable, SettingsOwner {


    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
                                                     + System.getProperty("file.separator") + ".pmd_new_designer.xml";


    /**
     * Callback to the owner.
     */
    private final DesignerApp designerApp;

    /* Menu bar */
    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem licenseMenuItem;
    @FXML
    private Menu openRecentMenu;
    @FXML
    private MenuItem exportToTestCodeMenuItem;
    @FXML
    private MenuItem exportXPathMenuItem;
    @FXML
    private Menu exportMenu;
    @FXML
    private Menu fileMenu;
    /* Center toolbar */
    @FXML
    private Button refreshASTButton;
    @FXML
    private ChoiceBox<LanguageVersion> languageChoiceBox;
    @FXML
    private ChoiceBox<String> xpathVersionChoiceBox;
    @FXML
    private ToggleButton bottomTabsToggle;
    /* Bottom panel */
    @FXML
    private TabPane bottomTabPane;
    @FXML
    private Tab eventLogTab;
    @FXML
    private Tab xpathEditorTab;
    @FXML
    private SplitPane mainHorizontalSplitPane;
    /* Children */
    @FXML
    private NodeInfoPanelController nodeInfoPanelController;
    @FXML
    private XPathPanelController xpathPanelController;
    @FXML
    private SourceEditorController sourceEditorController;
    @FXML
    private EventLogController eventLogPanelController;
    private Stack<File> recentFiles = new LimitedSizeStack<>(5);


    public MainDesignerController(DesignerApp owner) {
        this.designerApp = owner;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            XMLSettingsLoader loader = new XMLSettingsLoader(SETTINGS_FILE_NAME);
            loadSettings(loader.getSettings());
        } catch (IOException ioe) {
            // no big deal
        }


        initializeLanguageVersionMenu();
        initializeViewAnimation();

        xpathPanelController.initialiseVersionChoiceBox(xpathVersionChoiceBox);

        sourceEditorController.languageVersionProperty().bind(languageChoiceBox.getSelectionModel().selectedItemProperty());
        xpathPanelController.xpathVersionProperty().bind(xpathVersionChoiceBox.getSelectionModel().selectedItemProperty());

        refreshASTButton.setOnAction(e -> onRefreshASTClicked());
        licenseMenuItem.setOnAction(this::showLicensePopup);
        openFileMenuItem.setOnAction(this::onOpenFileClicked);
        openRecentMenu.setOnAction(e -> updateRecentFilesMenu());
        openRecentMenu.setOnShowing(e -> updateRecentFilesMenu());
        exportXPathMenuItem.setOnAction(event -> {
            try {
                onExportXPathToRuleClicked(event);
            } catch (IOException e) {
                // pretend it didn't happen
            }
        });
        fileMenu.setOnShowing(this::onFileMenuShowing);

        sourceEditorController.refreshAST();
        Platform.runLater(() -> sourceEditorController.moveCaret(0, 0));
    }


    private void initializeLanguageVersionMenu() {
        List<LanguageVersion> supported = Arrays.asList(DesignerUtil.getSupportedLanguageVersions());
        supported.sort(LanguageVersion::compareTo);
        languageChoiceBox.getItems().addAll(supported);


        languageChoiceBox.setConverter(new StringConverter<LanguageVersion>() {
            @Override
            public String toString(LanguageVersion object) {
                return object.getShortName();
            }


            @Override
            public LanguageVersion fromString(String string) {
                return LanguageRegistry.findLanguageVersionByTerseName(string.toLowerCase());
            }
        });

        LanguageVersion defaultLangVersion = LanguageRegistry.getLanguage("Java").getDefaultVersion();
        languageChoiceBox.getSelectionModel().select(defaultLangVersion);
        languageChoiceBox.show();

    }


    private void initializeViewAnimation() {

        // gets captured in the closure
        final double defaultMainHorizontalSplitPaneDividerPosition
            = mainHorizontalSplitPane.getDividerPositions()[0];


        // show/ hide bottom pane
        bottomTabsToggle.selectedProperty().addListener((observable, wasExpanded, isNowExpanded) -> {
            KeyValue keyValue = null;
            DoubleProperty divPosition = mainHorizontalSplitPane.getDividers().get(0).positionProperty();
            if (wasExpanded && !isNowExpanded) {
                keyValue = new KeyValue(divPosition, 1);
            } else if (!wasExpanded && isNowExpanded) {
                keyValue = new KeyValue(divPosition, defaultMainHorizontalSplitPaneDividerPosition);
            }

            if (keyValue != null) {
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), keyValue));
                timeline.play();
            }
        });
    }


    public void shutdown() {
        try {
            XMLSettingsSaver saver = XMLSettingsSaver.forFile(SETTINGS_FILE_NAME);
            this.saveSettings(saver);
            saver.save();
        } catch (IOException ioe) {
            // nevermind
        }

        sourceEditorController.shutdown(); // shutdown syntax highlighting
        xpathPanelController.shutdown();
    }


    private void onRefreshASTClicked() {
        sourceEditorController.refreshAST();
        xpathPanelController.evaluateXPath(sourceEditorController.getCompilationUnit(),
            sourceEditorController.getLanguageVersion());
    }


    /**
     * Executed when the user selects a node in a treeView or listView.
     */
    public void onNodeItemSelected(Node selectedValue) {
        nodeInfoPanelController.displayInfo(selectedValue);
        sourceEditorController.clearNodeHighlight();
        sourceEditorController.highlightNodePrimary(selectedValue);
        sourceEditorController.focusNodeInTreeView(selectedValue);
    }


    public void onNameDeclarationSelected(NameDeclaration declaration) {
        sourceEditorController.clearNodeHighlight();
        sourceEditorController.highlightNodePrimary(declaration.getNode());
        sourceEditorController.highlightNodesSecondary(declaration.getNode().getScope()
                                                                  .getDeclarations()
                                                                  .get(declaration)
                                                                  .stream()
                                                                  .map(NameOccurrence::getLocation)
                                                                  .collect(Collectors.toList()));
    }


    private void showLicensePopup(ActionEvent event) {
        Alert licenseAlert = new Alert(AlertType.INFORMATION);
        licenseAlert.setWidth(500);
        licenseAlert.setHeaderText("License");

        ScrollPane scroll = new ScrollPane();
        try {
            scroll.setContent(new TextArea(IOUtils.toString(getClass().getResourceAsStream("LICENSE"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        licenseAlert.getDialogPane().setContent(scroll);
        licenseAlert.showAndWait();
    }


    private void onExportXPathToRuleClicked(Event event) throws IOException {
        // doesn't work for some reason
        ExportXPathWizardController wizard
            = new ExportXPathWizardController(xpathPanelController.xpathExpressionProperty());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/xpath-export-wizard.fxml"));
        loader.setControllerFactory(type -> {
            if (type == ExportXPathWizardController.class) {
                return wizard;
            } else {
                // default behavior for controllerFactory:
                try {
                    return type.newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }
        });

        final Stage dialog = new Stage();
        dialog.initOwner(designerApp.getMainStage());
        dialog.setOnCloseRequest(e -> wizard.shutdown());
        dialog.initModality(Modality.WINDOW_MODAL);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        //stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        dialog.setScene(scene);
        dialog.show();
    }


    private void onFileMenuShowing(Event event) {
        openRecentMenu.setDisable(recentFiles.size() == 0);
    }


    private void onOpenFileClicked(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load source from file");
        File file = chooser.showOpenDialog(designerApp.getMainStage());
        loadSourceFromFile(file);
        sourceEditorController.clearStyleLayers();
    }


    private void loadSourceFromFile(File file) {
        if (file != null) {
            try {
                String source = IOUtils.toString(new FileInputStream(file));
                sourceEditorController.replaceText(source);
                LanguageVersion guess = DesignerUtil.getLanguageVersionFromExtension(file.getName());
                if (guess != null) { // guess the language from the extension
                    languageChoiceBox.getSelectionModel().select(guess);
                }

                recentFiles.push(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateRecentFilesMenu() {
        List<MenuItem> items = new ArrayList<>();

        for (final File f : recentFiles) {
            if (f.exists()) {
                CustomMenuItem item = new CustomMenuItem(new Label(f.getName()));
                item.setOnAction(e -> loadSourceFromFile(f));
                item.setMnemonicParsing(false);
                Tooltip.install(item.getContent(), new Tooltip(f.getAbsolutePath()));
                items.add(item);
            } else {
                recentFiles.remove(f);
            }
        }
        if (items.isEmpty()) {
            openRecentMenu.setDisable(true);
            return;
        }

        Collections.reverse(items);

        items.add(new SeparatorMenuItem());
        MenuItem clearItem = new MenuItem();
        clearItem.setText("Clear menu");
        clearItem.setOnAction(e -> {
            recentFiles.clear();
            openRecentMenu.setDisable(true);
        });
        items.add(clearItem);

        openRecentMenu.getItems().setAll(items);
    }


    @Override
    public List<AppSetting> getSettings() {
        List<AppSetting> settings = new ArrayList<>();
        settings.add(new AppSetting("recentFiles", this::getRecentFiles, this::setRecentFiles));
        settings.add(new AppSetting("isMaximized", this::isMaximized, this::setIsMaximized));
        settings.add(new AppSetting("bottomExpandedTab", this::getBottomExpandedTab, this::setBottomExpandedTab));
        return settings;
    }


    private void saveSettings(XMLSettingsSaver saver) {
        saveSettingsOf(this, saver);
        saveSettingsOf(sourceEditorController, saver);
        saveSettingsOf(xpathPanelController, saver);
    }


    private void saveSettingsOf(SettingsOwner owner, XMLSettingsSaver saver) {
        for (AppSetting s : owner.getSettings()) {
            saver.put(s.getKeyName(), s.getValue());
        }
    }


    private void loadSettings(Map<String, String> settings) {
        loadSettingsOf(sourceEditorController, settings);
        loadSettingsOf(xpathPanelController, settings);
        loadSettingsOf(this, settings);
    }


    private void loadSettingsOf(SettingsOwner owner, Map<String, String> loaded) {
        for (AppSetting s : owner.getSettings()) {
            String val = loaded.get(s.getKeyName());
            if (val != null) {
                s.setValue(val);
            }
        }
    }


    private String getRecentFiles() {
        StringBuilder sb = new StringBuilder();
        for (File f : recentFiles) {
            sb.append(',').append(f.getAbsolutePath());
        }
        return sb.length() > 0 ? sb.substring(1) : "";
    }


    private void setRecentFiles(String files) {
        List<String> fileNames = Arrays.asList(files.split(","));
        Collections.reverse(fileNames);
        for (String fileName : fileNames) {
            File f = new File(fileName);
            recentFiles.push(f);
        }
    }


    private String isMaximized() {
        return Boolean.toString(designerApp.getMainStage().isMaximized());
    }


    private void setIsMaximized(String bool) {
        boolean b = Boolean.parseBoolean(bool);
        designerApp.getMainStage().setMaximized(!b); // trigger change listener anyway
        designerApp.getMainStage().setMaximized(b);
    }


    private String getBottomExpandedTab() {
        return (bottomTabsToggle.isSelected() ? "expanded:" : "collapsed:")
               + bottomTabPane.getSelectionModel().getSelectedIndex();
    }


    private void setBottomExpandedTab(String id) {
        String[] info = id.split(":");
        bottomTabsToggle.setSelected("expanded".equals(info[0]));
        bottomTabPane.getSelectionModel().select(Integer.parseInt(info[1]));
    }


    public void invalidateAst() {
        nodeInfoPanelController.invalidateInfo();
        xpathPanelController.invalidateResults(false);
        sourceEditorController.clearNodeHighlight();
    }
}
