/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.util.designer.Designer;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * @author Clément Fournier
 */
public class DesignerController implements Initializable {

    @FXML
    public CodeArea codeEditorArea;
    @FXML
    private Menu languageMenu;
    @FXML
    private Button refreshASTButton;
    @FXML
    private TextArea xpathExpressionArea;
    @FXML
    private ListView<Node> xpathResultListView;
    @FXML
    private ListView<String> xpathAttributesListView;
    @FXML
    private Label xpathResultLabel;
    @FXML
    private TreeView<Node> astTreeView;
    @FXML
    private TitledPane xpathTitlePane;
    @FXML
    private SplitPane mainHorizontalSplitPane;


    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLanguageVersionMenu();
        initializeASTTreeView();
        codeEditorArea.setParagraphGraphicFactory(LineNumberFactory.get(codeEditorArea));

        // ensure main horizontal divider is never under 50%
        mainHorizontalSplitPane.getDividers()
                               .get(0)
                               .positionProperty()
                               .addListener((observable, oldValue, newValue) -> {
                                   if (newValue.doubleValue() < .5) {
                                       mainHorizontalSplitPane.setDividerPosition(0, .5);
                                   }
                               });

        xpathResultListView.setCellFactory(param -> new XpathViolationListCell());
        xpathResultListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                DesignerUtil.highlightNode(codeEditorArea, newValue);
            }
        });

    }


    private void initializeASTTreeView() {
        astTreeView.setCellFactory(param -> new ASTTreeCell());

        ReadOnlyObjectProperty<TreeItem<Node>> selectedItemProperty = astTreeView.getSelectionModel()
                                                                                 .selectedItemProperty();

        selectedItemProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                xpathAttributesListView.setItems(((ASTTreeItem) newValue).getAttributes());
                DesignerUtil.highlightNode(codeEditorArea, newValue.getValue());
            }
        });
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<MenuItem> items = languageMenu.getItems();
        ToggleGroup group = new ToggleGroup();

        for (LanguageVersion version : supported) {
            RadioMenuItem item = new RadioMenuItem(version.getShortName());
            item.setToggleGroup(group);
            items.add(item);
            languageRadioMenuMap.put(version, item);
        }

        selectedLanguageVersion = LanguageRegistry.getLanguage("Java").getDefaultVersion();
        languageRadioMenuMap.get(selectedLanguageVersion).setSelected(true);

        languageMenu.show();

    }


    private Node getCompilationUnit() {
        LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
        return getCompilationUnit(languageVersionHandler, codeEditorArea.getText());
    }


    LanguageVersionHandler getLanguageVersionHandler() {
        return selectedLanguageVersion.getLanguageVersionHandler();
    }


    /**
     * Refresh the AST view with the updated code.
     *
     * @param event ActionEvent
     */
    @FXML
    public void onRefreshASTClicked(ActionEvent event) {
        refreshAST();
        evaluateXPath();
    }


    private void refreshAST() {
        Node n = null;
        try {
            n = getCompilationUnit();
        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("An exception occurred:");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }

        if (n != null) {
            ASTTreeItem root = ASTTreeItem.getRoot(n);
            root.expandAll();
            astTreeView.setRoot(root);
        }
    }


    /** Evaluate XPath expression, print results on the ListView. */
    private void evaluateXPath() {
        ObservableList<Node> xpathResults = xpathResultListView.getItems();
        xpathResults.clear();
        if (StringUtils.isBlank(xpathExpressionArea.getText())) {
            return;
        }
        Node c = getCompilationUnit();
        try {
            XPathRule xpathRule = new XPathRule() {
                @Override
                public void addViolation(Object data, Node node, String arg) {
                    xpathResults.add(node);
                }
            };
            xpathRule.setMessage("");
            xpathRule.setLanguage(selectedLanguageVersion.getLanguage());
            xpathRule.setXPath(xpathExpressionArea.getText());
            // xpathRule.setVersion(xpathVersionButtonGroup.getSelection().);

            final RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(xpathRule);

            RuleSets ruleSets = new RuleSets(ruleSet);

            RuleContext ruleContext = new RuleContext();
            ruleContext.setLanguageVersion(selectedLanguageVersion);

            List<Node> nodes = new ArrayList<>();
            nodes.add(c);
            ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());


        } catch (ParseException pe) {
            // xpathResults.addElement(pe.fillInStackTrace().getMessage());
        }
        xpathResultListView.refresh();
        xpathExpressionArea.requestFocus();
    }


    private static Node getCompilationUnit(LanguageVersionHandler languageVersionHandler, String code) {
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        Node node = parser.parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(node);
        languageVersionHandler.getTypeResolutionFacade(Designer.class.getClassLoader()).start(node);
        return node;
    }

}
