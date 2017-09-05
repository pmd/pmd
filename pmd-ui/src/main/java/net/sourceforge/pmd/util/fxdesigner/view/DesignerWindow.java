/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.DesignerWindowPresenter;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * View class for the designer window.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerWindow implements Initializable {

    @FXML
    private CodeArea codeEditorArea;
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
    @FXML
    private BorderPane mainEditorBorderPane;
    @FXML
    private TitledPane astTitledPane;
    /* */
    private StringProperty sourceCodeProperty;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeView();
        new DesignerWindowPresenter(this).initialize(); // instantiate presenter
    }


    private void initializeView() {

        sourceCodeProperty = new SimpleStringProperty();
        sourceCodeProperty.bind(codeEditorArea.textProperty());


        codeEditorArea.setParagraphGraphicFactory(LineNumberFactory.get(codeEditorArea));
        nodeInfoAccordion.setExpandedPane(xpathAttributesTitledPane);


        double defaultMainHorizontalSplitPaneDividerPosition = mainHorizontalSplitPane.getDividerPositions()[0];


        // Fold xpath panel
        xpathEditorTitledPane.expandedProperty().addListener((observable, wasExpanded, isNowExpanded) -> {
            KeyValue keyValue = null;
            DoubleProperty divPosition = mainHorizontalSplitPane.getDividers().get(0).positionProperty();
            if (wasExpanded && !isNowExpanded) {
                keyValue = new KeyValue(divPosition, 1);
            } else if (!wasExpanded && isNowExpanded) {
                keyValue = new KeyValue(divPosition, defaultMainHorizontalSplitPaneDividerPosition);
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


    public void notifyOutdatedAST() {
        astTitledPane.setText("Abstract syntax tree (outdated)");
    }


    public void acknowledgeUpdatedAST() {
        astTitledPane.setText("Abstract syntax tree");
    }


    public void displayXPathResultsSize(int size) {
        violationsTitledPane.setText("Matched nodes\t(" + size + ")");
    }


    public void displayXPathError(Throwable t) {
        // Currently dismisses the exception
        violationsTitledPane.setText("Matched nodes\t(error)");
    }


    public CodeArea getCodeEditorArea() {
        return codeEditorArea;
    }


    public StringProperty sourceCodeProperty() {
        return sourceCodeProperty;
    }


    public Menu getLanguageMenu() {
        return languageMenu;
    }


    public ToggleGroup getXpathVersionToggleGroup() {
        return xpathVersionToggleGroup;
    }


    public Button getRefreshASTButton() {
        return refreshASTButton;
    }


    public CodeArea getXpathExpressionArea() {
        return xpathExpressionArea;
    }


    public ListView<Node> getXpathResultListView() {
        return xpathResultListView;
    }


    public ListView<String> getXpathAttributesListView() {
        return xpathAttributesListView;
    }


    public TitledPane getViolationsTitledPane() {
        return violationsTitledPane;
    }


    public TreeView<Node> getAstTreeView() {
        return astTreeView;
    }


    public ToggleButton getRefreshXPathToggle() {
        return refreshXPathToggle;
    }


    public BorderPane getMainEditorBorderPane() {
        return mainEditorBorderPane;
    }


    public TitledPane getAstTitledPane() {
        return astTitledPane;
    }


    public TitledPane getXpathEditorTitledPane() {
        return xpathEditorTitledPane;
    }


}
