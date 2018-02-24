/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.reactfx.EventStreams;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.model.ObservableXPathRuleBuilder;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluator;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.PropertyTableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * XPath panel controller.
 *
 * @author Clément Fournier
 * @see ExportXPathWizardController
 * @since 6.0.0
 */
public class XPathPanelController implements Initializable, SettingsOwner {

    private final DesignerRoot designerRoot;
    private final MainDesignerController parent;

    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();

    private final ObservableXPathRuleBuilder ruleBuilder = new ObservableXPathRuleBuilder();


    @FXML
    private PropertyTableView propertyView;
    @FXML
    private CustomCodeArea xpathExpressionArea;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private ListView<Node> xpathResultListView;
    // Actually a child of the main view toolbar, but this controller is responsible for it
    private ChoiceBox<String> xpathVersionChoiceBox;


    public XPathPanelController(DesignerRoot owner, MainDesignerController mainController) {
        this.designerRoot = owner;
        parent = mainController;

        getRuleBuilder().setClazz(XPathRule.class);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xpathExpressionArea.setSyntaxHighlightingEnabled(new XPathSyntaxHighlighter());

        EventStreams.valuesOf(xpathResultListView.getSelectionModel().selectedItemProperty())
                    .filter(Objects::nonNull)
                    .subscribe(parent::onNodeItemSelected);

        Platform.runLater(this::bindToParent);
    }


    // Binds the underlying rule parameters to the parent UI, disconnecting it from the wizard if need be
    private void bindToParent() {
        DesignerUtil.rewire(getRuleBuilder().languageProperty(),
                            Val.map(parent.languageVersionProperty(), LanguageVersion::getLanguage));

        DesignerUtil.rewire(getRuleBuilder().xpathVersionProperty(), parent.xpathVersionProperty());
        DesignerUtil.rewire(getRuleBuilder().xpathExpressionProperty(), xpathExpressionProperty());

        DesignerUtil.rewire(getRuleBuilder().rulePropertiesProperty(),
                            propertyView.rulePropertiesProperty(), propertyView::setRuleProperties);
    }


    public void initialiseVersionChoiceBox(ChoiceBox<String> choiceBox) {
        this.xpathVersionChoiceBox = choiceBox;

        ObservableList<String> versionItems = choiceBox.getItems();
        versionItems.add(XPathRuleQuery.XPATH_1_0);
        versionItems.add(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        versionItems.add(XPathRuleQuery.XPATH_2_0);

        xpathVersionChoiceBox.getSelectionModel().select(XPathRuleQuery.XPATH_2_0);
        choiceBox.setConverter(DesignerUtil.stringConverter(s -> "XPath " + s, s -> s.substring(6)));
    }


    /**
     * Evaluate XPath on the given compilation unit.
     *
     * @param compilationUnit The AST root
     * @param version         The language version
     */
    public void evaluateXPath(Node compilationUnit, LanguageVersion version) {

        try {
            String xpath = getXpathExpression();

            if (StringUtils.isBlank(xpath)) {
                xpathResultListView.getItems().clear();
                return;
            }

            ObservableList<Node> results
                    = FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit,
                                                                                     version,
                                                                                     getXpathVersion(),
                                                                                     xpath,
                                                                                     ruleBuilder.getRuleProperties()));
            xpathResultListView.setItems(results);
            violationsTitledPane.setText("Matched nodes\t(" + results.size() + ")");
        } catch (XPathEvaluationException e) {
            invalidateResults(true);
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.XPATH_EVALUATION_EXCEPTION));
        }

        xpathResultListView.refresh();
        xpathExpressionArea.requestFocus();
    }


    public void invalidateResults(boolean error) {
        xpathResultListView.getItems().clear();
        violationsTitledPane.setText("Matched nodes" + (error ? "\t(error)" : ""));
    }


    public void showExportXPathToRuleWizard() throws IOException {
        // doesn't work for some reason
        ExportXPathWizardController wizard
                = new ExportXPathWizardController(xpathExpressionProperty());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/xpath-export-wizard.fxml"));
        loader.setController(wizard);

        final Stage dialog = new Stage();
        dialog.initOwner(designerRoot.getMainStage());
        dialog.setOnCloseRequest(e -> wizard.shutdown());
        dialog.initModality(Modality.WINDOW_MODAL);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        //stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        dialog.setScene(scene);
        dialog.show();
    }


    public void shutdown() {
        xpathExpressionArea.disableSyntaxHighlighting();
    }


    @PersistentProperty
    public String getXpathExpression() {
        return xpathExpressionArea.getText();
    }


    public void setXpathExpression(String expression) {
        xpathExpressionArea.replaceText(expression);
    }


    public Val<String> xpathExpressionProperty() {
        return Val.wrap(xpathExpressionArea.textProperty());
    }


    @PersistentProperty
    public String getXpathVersion() {
        return getRuleBuilder().getXpathVersion();
    }


    public void setXpathVersion(String xpathVersion) {
        getRuleBuilder().setXpathVersion(xpathVersion);
    }


    public Var<String> xpathVersionProperty() {
        return getRuleBuilder().xpathVersionProperty();
    }


    private ObservableXPathRuleBuilder getRuleBuilder() {
        return ruleBuilder;
    }


    @Override
    public List<SettingsOwner> getChildrenSettingsNodes() {
        return Collections.singletonList(getRuleBuilder());
    }
}
