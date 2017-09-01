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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;

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
    @FXML
    private ToggleGroup xpathVersionToggleGroup;

    private ListView<String> xpathAttributesFloatingLV;


    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLanguageVersionMenu();
        initializeASTTreeView();
        initializeXPathResultsListView();
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
    }


    private void initializeXPathResultsListView() {

        xpathVersionToggleGroup.getToggles().get(0).setUserData(XPathRuleQuery.XPATH_1_0);
        xpathVersionToggleGroup.getToggles().get(1).setUserData(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        xpathVersionToggleGroup.getToggles().get(2).setUserData(XPathRuleQuery.XPATH_2_0);

        xpathResultListView.setCellFactory(param -> new XpathViolationListCell());
        xpathResultListView.getSelectionModel()
                           .selectedItemProperty()
                           .addListener((observable, oldValue, newValue) -> onNodeItemSelected(newValue));
    }


    private void initializeASTTreeView() {
        astTreeView.setCellFactory(param -> new ASTTreeCell());
        astTreeView.getSelectionModel()
                   .selectedItemProperty()
                   .addListener((observable, oldValue, newValue) -> onNodeItemSelected(newValue.getValue()));
    }


    private void onNodeItemSelected(Node selectedValue) {
        if (selectedValue != null) {
            ObservableList<String> atts = DesignerUtil.getAttributes(selectedValue);
            xpathAttributesListView.setItems(atts);
            if (xpathAttributesFloatingLV != null) {
                xpathAttributesFloatingLV.setItems(atts);
            }
            DesignerUtil.highlightNode(codeEditorArea, selectedValue);
        }
    }


    private void createFloatingAttributesLV(double posX, double posY) {
        if (xpathAttributesFloatingLV == null) {

            Label header = new Label("XPath attributes");
            xpathAttributesFloatingLV = new ListView<>();
            BorderPane bp = new BorderPane();
            bp.setTop(header);
            bp.setCenter(xpathAttributesFloatingLV);
            bp.setPrefHeight(100);
            bp.getStylesheets().add(getClass().getResource("designer.css").toString());

            Popup popup = new Popup();
            popup.getScene().setRoot(bp);
            popup.setHideOnEscape(true);

            ObjectProperty<Point2D> mouseLocation = new SimpleObjectProperty<>();

            bp.setOnMousePressed(event -> mouseLocation.setValue(new Point2D(event.getScreenX(), event.getScreenY())));

            bp.setOnMouseDragged((event -> {
                if (mouseLocation.get() != null) {
                    double x = event.getScreenX();
                    double y = event.getScreenY();
                    double dX = x - mouseLocation.get().getX();
                    double dY = y - mouseLocation.get().getY();
                    popup.setX(popup.getX() + dX);
                    popup.setY(popup.getY() + dY);
                    mouseLocation.setValue(new Point2D(x, y));
                }
            }));

            bp.setOnMouseReleased(event -> mouseLocation.setValue(null));

            popup.show(Designer.getPrimaryStage()//,
                       //  posX,
                       //  posY,
            );
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
            xpathRule.setVersion(xpathVersionToggleGroup.getSelectedToggle().getUserData().toString());

            final RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(xpathRule);

            RuleSets ruleSets = new RuleSets(ruleSet);

            RuleContext ruleContext = new RuleContext();
            ruleContext.setLanguageVersion(selectedLanguageVersion);

            List<Node> nodes = new ArrayList<>();
            nodes.add(c);
            ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());


        } catch (RuntimeException e) {
            xpathResultLabel.setText("Matches:\t (error)");
            return;
        }
        xpathResultLabel.setText("Matches:\t" + xpathResults.size());
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


    /**
     * Formats the cell for AST nodes in the TreeView.
     *
     * @author Clément Fournier
     * @since 6.0.0
     */
    public class ASTTreeCell extends TreeCell<Node> {

        @Override
        protected void updateItem(Node item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setContextMenu(getTheContextMenu(item));
                setText(item.toString() + (item.getImage() == null ? "" : " \"" + item.getImage() + "\""));
            }
        }


        private ContextMenu getTheContextMenu(Node node) {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem item = new MenuItem("Show XPath attributes");
            item.setOnAction(event -> {
                if (xpathAttributesFloatingLV == null) {
                    createFloatingAttributesLV(Designer.getPrimaryStage().getX() + 20,
                                               Designer.getPrimaryStage().getY() / 2);
                }
                xpathAttributesFloatingLV.setItems(DesignerUtil.getAttributes(node));
            });

            contextMenu.getItems().add(item);
            return contextMenu;
        }
    }


}
