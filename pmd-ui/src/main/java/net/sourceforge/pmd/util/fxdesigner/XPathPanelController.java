/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluator;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.XpathViolationListCell;
import net.sourceforge.pmd.util.fxdesigner.util.settings.AppSetting;
import net.sourceforge.pmd.util.fxdesigner.util.settings.SettingsOwner;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.util.StringConverter;

/**
 * XPath panel controller.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathPanelController implements Initializable, SettingsOwner {

    private final DesignerApp designerApp;
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();

    @FXML
    private CustomCodeArea xpathExpressionArea;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private ListView<Node> xpathResultListView;

    private ChoiceBox<String> xpathVersionChoiceBox;

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


    public void initialiseVersionChoiceBox(ChoiceBox<String> choiceBox) {
        this.xpathVersionChoiceBox = choiceBox;

        ObservableList<String> versionItems = choiceBox.getItems();
        versionItems.add(XPathRuleQuery.XPATH_1_0);
        versionItems.add(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        versionItems.add(XPathRuleQuery.XPATH_2_0);

        xpathVersionChoiceBox.getSelectionModel().select(xpathEvaluator.xpathVersionProperty().get());

        choiceBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return "XPath " + object;
            }


            @Override
            public String fromString(String string) {
                return string.substring(6);
            }
        });
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
            designerApp.getLogger().logEvent(new LogEntry(e, Category.XPATH_EVALUATION_EXCEPTION));
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


    public ObjectProperty<Node> selectedResultProperty() {
        return selectedResultProperty;
    }


    public StringProperty xpathVersionProperty() {
        return xpathEvaluator.xpathVersionProperty();
    }


    @Override
    public List<AppSetting> getSettings() {
        List<AppSetting> settings = new ArrayList<>();
        settings.add(new AppSetting("xpathVersion", () -> xpathEvaluator.xpathVersionProperty().getValue(),
                                    v -> {
                                        if (!"".equals(v)) {
                                            xpathEvaluator.xpathVersionProperty().setValue(v);
                                        }
                                    }));
        settings.add(new AppSetting("xpathCode", () -> xpathExpressionArea.getText(), (v) -> xpathExpressionArea.replaceText(v)));

        return settings;
    }


}
