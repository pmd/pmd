/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseTimeException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.XMLSettingsLoader;
import net.sourceforge.pmd.util.fxdesigner.util.XMLSettingsSaver;
import net.sourceforge.pmd.util.fxdesigner.view.DesignerWindow;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;

/**
 * Presenter of the designer window.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerWindowPresenter {

    private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
        + System.getProperty("file.separator") + ".pmd_designer.xml";

    private DesignerWindow view;
    private ASTManager model;
    private ToggleGroup languageVersionToggleGroup;


    public DesignerWindowPresenter(DesignerWindow designerWindow) {
        view = designerWindow;
        model = new ASTManager();
    }


    public void initialize() {
        initializeLanguageVersionMenu();
        initializeASTTreeView();
        initializeXPath();
        bindModelToView();

        try {
            loadSettings();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // no big deal
        }

        Designer.getMainStage().setOnCloseRequest(event -> {
            try {
                saveSettings();
            } catch (IOException e) {
                e.printStackTrace();
                // no big deal
            }
        });

        view.getRefreshASTButton().setOnAction(this::onRefreshASTClicked);
        view.sourceCodeProperty().addListener((observable, oldValue, newValue) -> view.notifyOutdatedAST());

        onRefreshASTClicked(null); // Restore AST and XPath results
    }


    /** Creates direct bindings from model properties to UI properties. */
    private void bindModelToView() {
        ObjectBinding<LanguageVersion> langVersionBinding
            = Bindings.createObjectBinding(() -> (LanguageVersion) languageVersionToggleGroup.getSelectedToggle().getUserData(),
                                           languageVersionToggleGroup.selectedToggleProperty());

        model.languageVersionProperty().bind(langVersionBinding);

        ToggleGroup tg = view.getXpathVersionToggleGroup();

        StringBinding xpathVersionBinding
            = Bindings.createStringBinding(() -> tg.getSelectedToggle().getUserData().toString(),
                                           tg.selectedToggleProperty());

        model.xpathVersionProperty().bind(xpathVersionBinding);
    }


    private void initializeXPath() {

        ToggleGroup xpathVersionToggleGroup = view.getXpathVersionToggleGroup();

        xpathVersionToggleGroup.getToggles().get(0).setUserData(XPathRuleQuery.XPATH_1_0);
        xpathVersionToggleGroup.getToggles().get(1).setUserData(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        xpathVersionToggleGroup.getToggles().get(2).setUserData(XPathRuleQuery.XPATH_2_0);


        ListView<Node> xpathResultsListView = view.getXpathResultListView();

        xpathResultsListView.setCellFactory(param -> new XpathViolationListCell());
        xpathResultsListView.getSelectionModel()
                            .selectedItemProperty()
                            .addListener((observable, oldValue, newValue) -> {
                                if (newValue != null) {
                                    onNodeItemSelected(newValue);
                                }
                            });
    }


    private void initializeASTTreeView() {

        TreeView<Node> astTreeView = view.getAstTreeView();

        astTreeView.setCellFactory(param -> new ASTTreeCell());
        astTreeView.getSelectionModel()
                   .selectedItemProperty()
                   .addListener((observable, oldValue, newValue) -> {
                       if (newValue != null) {
                           onNodeItemSelected(newValue.getValue());
                       }
                   });
    }


    /** Executed when the user selects a node in a treeView or listView. */
    private void onNodeItemSelected(Node selectedValue) {
        if (selectedValue != null) {
            ObservableList<String> atts = DesignerUtil.getAttributes(selectedValue);
            view.getXpathAttributesListView().setItems(atts);

            DesignerUtil.highlightNode(view.getCodeEditorArea(), selectedValue);
        }
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<MenuItem> items = view.getLanguageMenu().getItems();
        languageVersionToggleGroup = new ToggleGroup();

        LanguageVersion defaultLangVersion = LanguageRegistry.getLanguage("Java").getDefaultVersion();

        for (LanguageVersion version : supported) {
            RadioMenuItem item = new RadioMenuItem(version.getShortName());
            item.setToggleGroup(languageVersionToggleGroup);
            item.setUserData(version);
            items.add(item);

            if (version.equals(defaultLangVersion)) {
                item.setSelected(true);
            }
        }

        view.getLanguageMenu().show();

    }


    private void onRefreshASTClicked(ActionEvent event) {
        refreshAST();
        if (StringUtils.isNotBlank(view.getXpathExpressionArea().getText())) {
            evaluateXPath();
        } else {
            view.getXpathResultListView().getItems().clear();
        }
    }


    /** Refresh the AST view with the updated code. */
    private void refreshAST() {
        Node n = null;
        try {
            n = model.getCompilationUnit(view.getCodeEditorArea().getText());
        } catch (ParseTimeException e) {
            notifyParseTimeException(e);
        }

        if (n != null) {
            view.acknowledgeUpdatedAST();
            ASTTreeItem root = ASTTreeItem.getRoot(n);
            root.expandAll();
            view.getAstTreeView().setRoot(root);
        }
    }


    // not very elegant
    private void notifyParseTimeException(Exception e) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setWidth(1.5 * errorAlert.getWidth());
        errorAlert.setHeaderText("An exception occurred during parsing:");

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(new TextArea(ExceptionUtils.getStackTrace(e)));
        errorAlert.getDialogPane().setContent(scroll);
        errorAlert.showAndWait();
    }


    /** Evaluate XPath expression, print results on the ListView. */
    private void evaluateXPath() {

        try {
            String xpath = view.getXpathExpressionArea().getText();

            if (StringUtils.isBlank(xpath)) {
                return;
            }

            ObservableList<Node> results = model.evaluateXPath(xpath);
            view.getXpathResultListView().setItems(results);
            view.displayXPathResultsSize(results.size());
        } catch (XPathEvaluationException e) {
            view.displayXPathError(e);
        }

        view.getXpathResultListView().refresh();
        view.getXpathExpressionArea().requestFocus();
    }


    private void saveSettings() throws IOException {
        XMLSettingsSaver saver = XMLSettingsSaver.forFile(SETTINGS_FILE_NAME);

        for (DesignerWindowSettings setting : DesignerWindowSettings.values()) {
            saver.put(setting.keyName, setting.getValueFrom(this));
        }

        saver.save();
    }


    // Must only be called *after* initialization
    private void loadSettings() throws IOException {

        Set<String> keyNames = Arrays.stream(DesignerWindowSettings.values())
                                     .map(e -> e.keyName)
                                     .collect(Collectors.toSet());

        XMLSettingsLoader loader = new XMLSettingsLoader(SETTINGS_FILE_NAME, keyNames);

        for (Entry<String, String> e : loader.loadSettings().entrySet()) {
            DesignerWindowSettings setting = DesignerWindowSettings.ofKeyName(e.getKey());
            setting.setValueIn(this, e.getValue());
        }
    }

    /********************************/
    /* SETTINGS LOAD/STORE ROUTINES */


    /********************************/


    String getLanguageVersionTerseName() {
        return model.getLanguageVersion().getTerseName();
    }


    void setLanguageVersionFromTerseName(String name) {
        LanguageVersion version = LanguageRegistry.findLanguageVersionByTerseName(name);
        languageVersionToggleGroup.getToggles()
                                  .stream()
                                  .filter(toggle -> toggle.getUserData().equals(version))
                                  .findAny()
                                  .orElse(new RadioMenuItem()) // discard
                                  .setSelected(true);
    }


    String getSourceCode() {
        return view.getCodeEditorArea().getText();
    }


    void setSourceCode(String code) {
        view.getCodeEditorArea().replaceText(code);
    }


    String getXPathVersion() {
        return model.getXPathVersion();
    }


    void setXPathVersion(String version) {
        view.getXpathVersionToggleGroup()
            .getToggles()
            .stream()
            .filter(toggle -> toggle.getUserData().equals(version))
            .findFirst()
            .orElse(new RadioMenuItem()) // discard
            .setSelected(true);
    }


    String getXPathCode() {
        return view.getXpathExpressionArea().getText();
    }


    void setXPathCode(String code) {
        view.getXpathExpressionArea().replaceText(code);
    }


    String isXPathPanelExpanded() {
        return Boolean.toString(view.getXpathEditorTitledPane().isExpanded());
    }


    void setIsXPathPanelExpanded(String bool) {
        boolean b = Boolean.parseBoolean(bool);
        view.getXpathEditorTitledPane().setExpanded(b);
    }


    String getLeftToolbarDividerPosition() {
        return String.valueOf(view.getMainVerticalSplitPane().getDividerPositions()[0]);
    }


    void setLeftToolbarDividerPosition(String pos) {
        view.getMainVerticalSplitPane().setDividerPosition(0, Double.parseDouble(pos));
    }
}
