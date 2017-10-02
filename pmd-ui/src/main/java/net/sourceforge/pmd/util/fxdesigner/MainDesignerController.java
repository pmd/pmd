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

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.LimitedSizeStack;
import net.sourceforge.pmd.util.fxdesigner.util.settings.AppSetting;
import net.sourceforge.pmd.util.fxdesigner.util.settings.XMLSettingsLoader;
import net.sourceforge.pmd.util.fxdesigner.util.settings.XMLSettingsSaver;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class MainDesignerController implements Initializable, SettingsOwner {


    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
        + System.getProperty("file.separator") + ".pmd_new_designer.xml";


    /** Callback to the owner. */
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
    private SplitPane eventLogPanelController;
    private Stack<File> recentFiles = new LimitedSizeStack<>(5);
    private final List<AppSetting> allSettings = getAllSettings();


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
        initializeXPath();
        initializeViewAnimation();

        sourceEditorController.languageVersionProperty().bind(languageChoiceBox.getSelectionModel().selectedItemProperty());
        xpathPanelController.xpathVersionProperty().bind(xpathVersionChoiceBox.getSelectionModel().selectedItemProperty());

        refreshASTButton.setOnAction(e -> onRefreshASTClicked());
        licenseMenuItem.setOnAction(this::showLicensePopup);
        openFileMenuItem.setOnAction(this::onOpenFileClicked);
        openRecentMenu.setOnAction(e -> updateRecentFilesMenu());
        openRecentMenu.setOnShowing(e -> updateRecentFilesMenu());
        fileMenu.setOnShowing(this::onFileMenuShowing);

    }


    private void initializeXPath() {

        ObservableList<String> versionItems = xpathVersionChoiceBox.getItems();
        versionItems.add(XPathRuleQuery.XPATH_1_0);
        versionItems.add(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        versionItems.add(XPathRuleQuery.XPATH_2_0);

        xpathVersionChoiceBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return "XPath " + object;
            }


            @Override
            public String fromString(String string) {
                return string.substring(6);
            }
        });

        xpathPanelController.selectedResultProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    onNodeItemSelected(newValue);
                                    sourceEditorController.focusNodeInTreeView(newValue);
                                }
                            });
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<LanguageVersion> items = languageChoiceBox.getItems();

        items.addAll(Arrays.asList(supported));

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
            saveSettings(saver);
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


    /** Executed when the user selects a node in a treeView or listView. */
    private void onNodeItemSelected(Node selectedValue) {
        nodeInfoPanelController.displayInfo(selectedValue);
        sourceEditorController.highlightNode(selectedValue);
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


    private List<AppSetting> getAllSettings() {
        List<AppSetting> settings = new ArrayList<>();
        settings.add(new AppSetting("recentFiles", this::getRecentFiles, this::setRecentFiles));
        settings.add(new AppSetting("isMaximized", this::isMaximized, this::setIsMaximized));
        settings.add(new AppSetting("bottomExpandedTab", this::getBottomExpandedTab, this::setBottomExpandedTab));
        return settings;
    }


    @Override
    public void saveSettings(SettingsAccumulator saver) {
        for (AppSetting s : allSettings) {
            saver.put(s.getKeyName(), s.getValue());
        }
    }


    @Override
    public void loadSettings(Map<String, String> loaded) {
        sourceEditorController.loadSettings(loaded);
        xpathPanelController.loadSettings(loaded);


        for (AppSetting s : allSettings) {
            s.setValue(loaded.get(s.getKeyName()));
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


}
