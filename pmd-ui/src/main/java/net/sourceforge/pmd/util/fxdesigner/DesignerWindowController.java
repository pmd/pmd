/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.reactfx.Subscription;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseAbortedException;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.LimitedSizeStack;
import net.sourceforge.pmd.util.fxdesigner.util.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.util.XMLSettingsLoader;
import net.sourceforge.pmd.util.fxdesigner.util.XMLSettingsSaver;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.AvailableSyntaxHighlighters;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeCell;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeItem;
import net.sourceforge.pmd.util.fxdesigner.view.DesignerWindow;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * Main controller of the app.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerWindowController implements Initializable {

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
        + System.getProperty("file.separator") + ".pmd_new_designer.xml";

    private final DesignerApp designerApp;

    @FXML
    private NodeInfoPanelController nodeInfoPanelController;

    @FXML
    private XPathPanelController xpathPanelController;


    private DesignerWindow view;
    private ASTManager model = new ASTManager();
    private Stack<File> recentFiles = new LimitedSizeStack<>(5);
    private Subscription compilAutoRefresh;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    public DesignerWindowController(DesignerApp root) {
        this.designerApp = root;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLanguageVersionMenu();
        initializeASTTreeView();
        initializeXPath();
        bindModelToView();
        initializeSyntaxHighlighting();
        initializeEventLog();

        try {
            loadSettings();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // no big deal
        }


        Designer.instance().getMainStage().setOnCloseRequest(event -> {
            try {
                saveSettings();
                view.getCodeEditorArea().disableSyntaxHighlighting(); // shutdown the executor
                xpathPanelController.shutdown(); // shutdown syntax highlighting
                if (compilAutoRefresh != null) {
                    compilAutoRefresh.unsubscribe();
                }
                if (executorService != null) {
                    executorService.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // no big deal
            }
        });


        view.getRefreshASTButton().setOnAction(e -> this.onRefreshASTClicked());
        view.getLicenseMenuItem().setOnAction(this::showLicensePopup);
        view.getLoadSourceFromFileMenuItem().setOnAction(this::onOpenFileClicked);
        view.getOpenRecentMenu().setOnAction(e -> updateRecentFilesMenu());
        view.getOpenRecentMenu().setOnShowing(e -> updateRecentFilesMenu());
        view.getFileMenu().setOnShowing(this::onFileMenuShowing);

        try {
            setUpToDateCompilationUnit(getCompilationUnitUpdate(view.sourceCodeProperty().get()));
        } catch (ParseAbortedException e) {
            invalidateAST(true);
        }
    }


    private void enableASTAutoRefresh() {
        CustomCodeArea area = view.getCodeEditorArea();
        compilAutoRefresh = area.richChanges()
                                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                                .successionEnds(Duration.ofMillis(500))
                                .supplyTask(() -> updateASTTask(area.getText()))
                                .awaitLatest(area.richChanges())
                                .filterMap(t -> {
                                    if (t.isSuccess()) {
                                        return Optional.ofNullable(t.get());
                                    } else {
                                        invalidateAST(true);
                                        return Optional.empty();
                                    }
                                })
                                .subscribe(this::setUpToDateCompilationUnit);
    }


    private void disableASTAutoRefresh() {

    }


    private Task<Node> updateASTTask(final String text) {
        Task<Node> task = new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                return getCompilationUnitUpdate(text);
            }
        };
        if (!executorService.isShutdown()) {
            executorService.execute(task);
        }
        return task;
    }


    /** Creates direct bindings from model properties to UI properties. */
    private void bindModelToView() {
        model.languageVersionProperty().bind(view.getLanguageChoiceBox().getSelectionModel().selectedItemProperty());
        model.xpathVersionProperty().bind(view.getXpathVersionChoiceBox().getSelectionModel().selectedItemProperty());
    }


    private void initializeXPath() {

        ObservableList<String> xpathVersionChoiceBox = view.getXpathVersionChoiceBox().getItems();
        xpathVersionChoiceBox.add(XPathRuleQuery.XPATH_1_0);
        xpathVersionChoiceBox.add(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        xpathVersionChoiceBox.add(XPathRuleQuery.XPATH_2_0);

        view.getXpathVersionChoiceBox().setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return "XPath " + object;
            }


            @Override
            public String fromString(String string) {
                return string.substring(6);
            }
        });


        xpathPanelController.selectedResultPropertyProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    onNodeItemSelected(newValue);
                                    focusNodeInASTTreeView(newValue);
                                }
                            });
    }


    private void initializeASTTreeView() {

        TreeView<Node> astTreeView = view.getAstTreeView();

        astTreeView.setCellFactory(param -> new ASTTreeCell());

        ReadOnlyObjectProperty<TreeItem<Node>> selectedItemProperty
            = astTreeView.getSelectionModel().selectedItemProperty();


        astTreeView.rootProperty().addListener((obs, oldRoot, newRoot) -> {
            if (newRoot == null) {
                nodeInfoPanelController.invalidateInfo();
            }
        });

        selectedItemProperty.addListener(observable -> {
            nodeInfoPanelController.invalidateInfo();
        });

        selectedItemProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                onNodeItemSelected(newValue.getValue());
            }
        });
    }


    /** Executed when the user selects a node in a treeView or listView. */
    private void onNodeItemSelected(Node selectedValue) {

        nodeInfoPanelController.displayInfo(selectedValue);


        if (view.getCodeEditorArea().isInRange(selectedValue)) {
            highlightNode(selectedValue, view.getCodeEditorArea());
            view.getCodeEditorArea().positionCaret(selectedValue.getBeginLine(), selectedValue.getBeginColumn());
        } else {
            view.getCodeEditorArea().clearPrimaryStyleLayer();
        }

    }


    private void highlightNode(Node node, CustomCodeArea codeArea) {
        codeArea.restylePrimaryStyleLayer(node, Collections.singleton("primary-highlight"));
        codeArea.paintCss();
    }


    private void focusNodeInASTTreeView(Node node) {
        TreeView<Node> astTreeView = view.getAstTreeView();
        ASTTreeItem found = ((ASTTreeItem) astTreeView.getRoot()).findItem(node);
        if (found != null) {
            SelectionModel<TreeItem<Node>> selectionModel = astTreeView.getSelectionModel();
            selectionModel.select(found);
            astTreeView.getFocusModel().focus(selectionModel.getSelectedIndex());
            // astTreeView.scrollTo(selectionModel.getSelectedIndex());
        }
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<LanguageVersion> items = view.getLanguageChoiceBox().getItems();

        items.addAll(Arrays.asList(supported));

        view.getLanguageChoiceBox().setConverter(new StringConverter<LanguageVersion>() {
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
        view.getLanguageChoiceBox().getSelectionModel().select(defaultLangVersion);
        view.getLanguageChoiceBox().show();

    }


    private void initializeSyntaxHighlighting() {
        view.isSyntaxHighlightingEnabledProperty().addListener(((observable, wasEnabled, isEnabled) -> {
            if (!wasEnabled && isEnabled) {
                updateSyntaxHighlighter();
            } else if (!isEnabled) {
                view.getCodeEditorArea().disableSyntaxHighlighting();
            }
        }));

        model.languageVersionProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                updateSyntaxHighlighter();
            }
        });


    }


    private void initializeEventLog() {
        view.getLogCategoryColumn().setCellValueFactory(new PropertyValueFactory<>("category"));
        view.getLogMessageColumn().setCellValueFactory(new PropertyValueFactory<>("message"));
        final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        view.getLogDateColumn().setCellValueFactory(
            entry -> new SimpleObjectProperty<>(entry.getValue().getTimestamp()));
        view.getLogDateColumn().setCellFactory(column -> new TableCell<LogEntry, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        view.getEventLogTableView().setItems(Designer.instance().getLogger().getLog());

        view.getEventLogTableView()
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> view.getLogDetailsTextArea()
                                                      .setText(newVal == null ? "" : newVal.getStackTrace()));

        view.getEventLogTableView().resizeColumn(view.getLogMessageColumn(), -1);

    }


    /**
     * Returns the new compilation unit if it has changed. Returns null in case of no change.
     *
     * @param source Source code
     *
     * @return The new compilation unit, or null if it has not changed
     *
     * @throws ParseAbortedException if there was an error during parsing
     */
    private Node getCompilationUnitUpdate(String source) {
        return model.isRecompilationNeeded(source) ? model.getCompilationUnit(source) : null;
    }


    private void setUpToDateCompilationUnit(Node node) {
        ASTTreeItem root = ASTTreeItem.getRoot(node);
        view.getAstTreeView().setRoot(root);
        view.acknowledgeUpdatedAST();
    }


    private void onRefreshASTClicked() {
        String source = view.getCodeEditorArea().getText();
        if (model.isRecompilationNeeded(source)) {
            refreshAST(source);
            view.getCodeEditorArea().clearPrimaryStyleLayer();
        }
        xpathPanelController.evaluateXPath(model.getCompilationUnit(),
                                           model.getLanguageVersion());

    }


    /** Refresh the AST view with the updated code. */
    private void refreshAST(String source) {
        Node n;
        try {
            n = model.getCompilationUnit(source);
        } catch (ParseAbortedException e) {
            n = null;
        }

        if (n != null) {
            view.acknowledgeUpdatedAST();
            ASTTreeItem root = ASTTreeItem.getRoot(n);
            view.getAstTreeView().setRoot(root);
        } else {
            invalidateAST(true);
        }
    }


    /** Clears the ast and all node inspection views. */
    private void invalidateAST(boolean error) {
        view.getAstTreeView().setRoot(null);
        view.notifyOutdatedAST(error);
        nodeInfoPanelController.invalidateInfo();
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
        view.getOpenRecentMenu().setDisable(recentFiles.size() == 0);
    }


    private void onOpenFileClicked(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load source from file");
        File file = chooser.showOpenDialog(Designer.instance().getMainStage());
        loadSourceFromFile(file);
        view.getCodeEditorArea().clearStyleLayers();
    }


    private void loadSourceFromFile(File file) {
        if (file != null) {
            try {
                String source = IOUtils.toString(new FileInputStream(file));
                view.getCodeEditorArea().replaceText(source);
                LanguageVersion guess = DesignerUtil.getLanguageVersionFromExtension(file.getName());
                if (guess != null) { // guess the language from the extension
                    view.getLanguageChoiceBox().getSelectionModel().select(guess);
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
            view.getOpenRecentMenu().setDisable(true);
            return;
        }

        Collections.reverse(items);

        items.add(new SeparatorMenuItem());
        MenuItem clearItem = new MenuItem();
        clearItem.setText("Clear menu");
        clearItem.setOnAction(e -> {
            recentFiles.clear();
            view.getOpenRecentMenu().setDisable(true);
        });
        items.add(clearItem);

        view.getOpenRecentMenu().getItems().setAll(items);
    }


    private void updateSyntaxHighlighter() {
        SyntaxHighlighter computer = AvailableSyntaxHighlighters.getComputerForLanguage(model.getLanguageVersion().getLanguage());
        if (computer != null) {
            view.getCodeEditorArea().setSyntaxHighlightingEnabled(computer);
        } else {
            view.getCodeEditorArea().disableSyntaxHighlighting();
        }
    }


    private void saveSettings() throws IOException {
        XMLSettingsSaver saver = XMLSettingsSaver.forFile(SETTINGS_FILE_NAME);

        for (DesignerWindowSettings setting : DesignerWindowSettings.values()) {
            saver.put(setting.getKeyName(), setting.getValueFrom(this));
        }

        saver.save();
    }


    // Must only be called *after* initialization
    private void loadSettings() throws IOException {

        Set<String> keyNames = Arrays.stream(DesignerWindowSettings.values())
                                     .map(DesignerWindowSettings::getKeyName)
                                     .collect(Collectors.toSet());

        XMLSettingsLoader loader = new XMLSettingsLoader(SETTINGS_FILE_NAME, keyNames);

        for (Entry<String, String> e : loader.loadSettings().entrySet()) {
            DesignerWindowSettings setting = DesignerWindowSettings.ofKeyName(e.getKey());
            setting.setValueIn(this, e.getValue());
        }
    }

    /* SETTINGS LOAD/STORE ROUTINES */


    String getLanguageVersionTerseName() {
        return model.getLanguageVersion().getTerseName();
    }


    void setLanguageVersionFromTerseName(String name) {
        LanguageVersion version = LanguageRegistry.findLanguageVersionByTerseName(name);
        view.getLanguageChoiceBox().getSelectionModel().select(version);
    }


    String getSourceCode() {
        return view.getCodeEditorArea().getText();
    }


    void setSourceCode(String code) {
        view.getCodeEditorArea().replaceText(code);
    }


    String getASTPaneWidth() {
        return Double.toString(view.getAstTreeView().getWidth());
    }


    void setAstPaneWidth(String val) {
        view.getAstTreeView().setPrefWidth(Double.parseDouble(val));
    }


    String getXPathVersion() {
        return model.getXPathVersion();
    }


    void setXPathVersion(String version) {
        view.getXpathVersionChoiceBox().getSelectionModel().select(version);
    }


    String getXPathCode() {
        return view.getXpathExpressionArea().getText();
    }


    void setXPathCode(String code) {
        view.getXpathExpressionArea().replaceText(code);
    }


    String getBottomExpandedTab() {
        return (view.isBottomPaneExpanded() ? "expanded:" : "collapsed:")
            + view.getBottomTabPane().getSelectionModel().getSelectedIndex();
    }


    void setBottomExpandedTab(String id) {
        String[] info = id.split(":");
        view.getBottomTabsToggle().setSelected("expanded".equals(info[0]));
        view.getBottomTabPane().getSelectionModel().select(Integer.parseInt(info[1]));
    }


    String isMaximized() {
        return Boolean.toString(Designer.instance().getMainStage().isMaximized());
    }


    void setIsMaximized(String bool) {
        boolean b = Boolean.parseBoolean(bool);
        Designer.instance().getMainStage().setMaximized(!b); // trigger change listener anyway
        Designer.instance().getMainStage().setMaximized(b);
    }


    String getRecentFiles() {
        StringBuilder sb = new StringBuilder();
        for (File f : recentFiles) {
            sb.append(',').append(f.getAbsolutePath());
        }
        return sb.length() > 0 ? sb.substring(1) : "";
    }


    void setRecentFiles(String files) {
        List<String> fileNames = Arrays.asList(files.split(","));
        Collections.reverse(fileNames);
        for (String fileName : fileNames) {
            File f = new File(fileName);
            recentFiles.push(f);
        }
    }


    String isSyntaxHighlightingEnabled() {
        return Boolean.toString(view.isSyntaxHighlightingEnabledProperty().get());
    }


    void setIsSyntaxHighlightingEnabled(String bool) {
        boolean b = Boolean.parseBoolean(bool);
        if (view.isSyntaxHighlightingEnabledProperty().get() != b) {
            view.onToggleSyntaxHighlightingClicked(null);
        }
    }

}
