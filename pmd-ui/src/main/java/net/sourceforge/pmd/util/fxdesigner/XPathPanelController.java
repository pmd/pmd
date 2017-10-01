/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluator;
import net.sourceforge.pmd.util.fxdesigner.util.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.util.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.XpathViolationListCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

/**
 * XPath panel controller.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathPanelController implements Initializable {

    private final DesignerApp designerApp;
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();

    @FXML
    private CustomCodeArea xpathExpressionArea;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private ListView<Node> xpathResultListView;


    private ObjectProperty<Node> selectedResultProperty = new SimpleObjectProperty<>();


    XPathPanelController(DesignerApp owner) {
        this.designerApp = owner;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xpathExpressionArea.setSyntaxHighlightingEnabled(new XPathSyntaxHighlighter());
        xpathResultListView.setCellFactory(param -> new XpathViolationListCell());

        selectedResultProperty.bind(xpathResultListView.getSelectionModel().selectedItemProperty());
    }


    /**
     * Evaluate XPath on the given compilation unit.
     *
     * @param compilationUnit The AST root
     * @param version         The language version
     */
    public void evaluateXPath(Node compilationUnit, LanguageVersion version) {

        try {
            String xpath = xpathExpressionArea.getText();

            if (StringUtils.isBlank(xpath)) {
                xpathResultListView.getItems().clear();
                return;
            }

            ObservableList<Node> results
                = FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit, version, xpath));
            xpathResultListView.setItems(results);
            violationsTitledPane.setText("Matched nodes\t(" + results.size() + ")");
        } catch (XPathEvaluationException e) {
            notifyXPathError(e);
            Designer.instance().getLogger().logEvent(new LogEntry(e, Category.XPATH_EVALUATION_EXCEPTION));
        }

        xpathResultListView.refresh();
        xpathExpressionArea.requestFocus();
    }


    public void invalidateResults() {
        xpathResultListView.getItems().clear();
    }


    public void shutdown() {
        xpathExpressionArea.disableSyntaxHighlighting();
    }


    private void notifyXPathError(Throwable t) {
        // Currently dismisses the exception
        violationsTitledPane.setText("Matched nodes\t(error)");
    }


    public Node getSelectedResultProperty() {
        return selectedResultProperty.get();
    }


    public ObjectProperty<Node> selectedResultPropertyProperty() {
        return selectedResultProperty;
    }
}
