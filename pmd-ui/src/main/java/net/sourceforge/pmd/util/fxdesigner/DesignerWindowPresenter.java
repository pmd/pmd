/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseTimeException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;

/**
 * Presenter of the designer window.
 *
 * @author Clément Fournier
 */
public class DesignerWindowPresenter {

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

        view.getRefreshASTButton().setOnAction(this::onRefreshASTClicked);
        view.sourceCodeProperty().addListener((observable, oldValue, newValue) -> view.notifyOutdatedAST());

    }


    private void bindModelToView() {
        ObjectBinding<LanguageVersion> langVersionBinding
            = Bindings.createObjectBinding(() -> (LanguageVersion) languageVersionToggleGroup.getSelectedToggle().getUserData(),
                                           languageVersionToggleGroup.selectedToggleProperty());

        model.languageVersionProperty().bind(langVersionBinding);
        model.sourceCodeProperty().bind(view.sourceCodeProperty());

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
        if (view.getRefreshXPathToggle().isSelected()) {
            evaluateXPath();
        }
    }


    /** Refresh the AST view with the updated code. */
    private void refreshAST() {
        Node n = null;
        try {
            n = model.getCompilationUnit();
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


    private void notifyParseTimeException(Exception e) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText("An exception occurred:");
        errorAlert.setContentText(ExceptionUtils.getStackTrace(e));
        errorAlert.showAndWait();
    }


    /** Evaluate XPath expression, print results on the ListView. */
    private void evaluateXPath() {
        ObservableList<Node> results;
        try {
            results = model.evaluateXPath(view.getXpathExpressionArea().getText());
            view.getXpathResultListView().setItems(results);
        } catch (XPathEvaluationException e) {
            // Currently dismisses the exception
            view.getViolationsTitledPane().setText("Matched nodes\t(error)");
            return;
        }
        view.getViolationsTitledPane().setText("Matched nodes\t(" + results.size() + ")");
        view.getXpathResultListView().refresh();
        view.getXpathExpressionArea().requestFocus();
    }


}
