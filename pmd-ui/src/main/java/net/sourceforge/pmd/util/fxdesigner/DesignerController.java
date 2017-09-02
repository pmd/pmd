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
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.util.Duration;

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
    private CodeArea xpathExpressionArea;
    @FXML
    private ListView<Node> xpathResultListView;
    @FXML
    private ListView<String> xpathAttributesListView;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private TreeView<Node> astTreeView;
    @FXML
    private TitledPane xpathEditorTitledPane;
    @FXML
    private SplitPane mainHorizontalSplitPane;
    @FXML
    private ToggleGroup xpathVersionToggleGroup;
    @FXML
    private ToggleButton refreshXPathToggle;
    @FXML
    private TitledPane xpathAttributesTitledPane;
    @FXML
    private Accordion nodeInfoAccordion;

    private double defaultMainHorizontalSplitPaneDividerPos;
    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLanguageVersionMenu();
        initializeASTTreeView();
        initializeXPathResultsListView();
        codeEditorArea.setParagraphGraphicFactory(LineNumberFactory.get(codeEditorArea));

        nodeInfoAccordion.setExpandedPane(xpathAttributesTitledPane);

        defaultMainHorizontalSplitPaneDividerPos = mainHorizontalSplitPane.getDividerPositions()[0];

        xpathEditorTitledPane.expandedProperty().addListener((observable, wasExpanded, isNowExpanded) -> {
            KeyValue keyValue = null;
            DoubleProperty divPosition = mainHorizontalSplitPane.getDividers().get(0).positionProperty();
            if (wasExpanded && !isNowExpanded) {
                keyValue = new KeyValue(divPosition, 1);
            } else if (!wasExpanded && isNowExpanded) {
                keyValue = new KeyValue(divPosition, defaultMainHorizontalSplitPaneDividerPos);
            }

            if (keyValue != null) {
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(350), keyValue));
                timeline.play();
            }
        });

        // ensure main horizontal divider is never under 50%
        mainHorizontalSplitPane.getDividers()
                               .get(0)
                               .positionProperty()
                               .addListener((observable, oldValue, newValue) -> {
                                   if (newValue.doubleValue() < .5) {
                                       mainHorizontalSplitPane.setDividerPosition(0, .5);
                                   }
                               });
    }


    private void initializeXPathResultsListView() {

        xpathVersionToggleGroup.getToggles().get(0).setUserData(XPathRuleQuery.XPATH_1_0);
        xpathVersionToggleGroup.getToggles().get(1).setUserData(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        xpathVersionToggleGroup.getToggles().get(2).setUserData(XPathRuleQuery.XPATH_2_0);

        xpathResultListView.setCellFactory(param -> new XpathViolationListCell());
        xpathResultListView.getSelectionModel()
                           .selectedItemProperty()
                           .addListener((observable, oldValue, newValue) -> {
                               if (newValue != null) {
                                   onNodeItemSelected(newValue);
                               }
                           });
    }


    private void initializeASTTreeView() {
        astTreeView.setCellFactory(param -> new ASTTreeCell());
        astTreeView.getSelectionModel()
                   .selectedItemProperty()
                   .addListener((observable, oldValue, newValue) -> {
                       if (newValue != null) {
                           onNodeItemSelected(newValue.getValue());
                       }
                   });
    }


    private void onNodeItemSelected(Node selectedValue) {
        if (selectedValue != null) {
            ObservableList<String> atts = DesignerUtil.getAttributes(selectedValue);
            xpathAttributesListView.setItems(atts);
            DesignerUtil.highlightNode(codeEditorArea, selectedValue);
        }
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


    @FXML
    public void onRefreshASTClicked(ActionEvent event) {
        refreshAST();
        if (refreshXPathToggle.isSelected()) {
            evaluateXPath();
        }
    }


    /** Refresh the AST view with the updated code. */
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
            xpathRule.setVersion(xpathVersionToggleGroup.getSelectedToggle().getUserData().toString());

            final RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(xpathRule);

            RuleSets ruleSets = new RuleSets(ruleSet);

            RuleContext ruleContext = new RuleContext();
            ruleContext.setLanguageVersion(selectedLanguageVersion);

            List<Node> nodes = new ArrayList<>();
            nodes.add(c);
            ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());


        } catch (RuntimeException e) {
            violationsTitledPane.setText("Matched nodes\t(error)");
            return;
        }
        violationsTitledPane.setText("Matched nodes\t(" + xpathResults.size() + ")");
        xpathResultListView.refresh();
        xpathExpressionArea.requestFocus();
    }


    private Node getCompilationUnit() {
        LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
        return getCompilationUnit(languageVersionHandler, codeEditorArea.getText());
    }


    LanguageVersionHandler getLanguageVersionHandler() {
        return selectedLanguageVersion.getLanguageVersionHandler();
    }


    private static Node getCompilationUnit(LanguageVersionHandler languageVersionHandler, String code) {
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        Node node = parser.parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(node);
        languageVersionHandler.getTypeResolutionFacade(Designer.class.getClassLoader()).start(node);
        return node;
    }


}
