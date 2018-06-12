/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.reactfx.EventStreams;
import org.reactfx.collection.LiveArrayList;
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
import net.sourceforge.pmd.util.fxdesigner.popups.ExportXPathWizardController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.PropertyTableView;
import net.sourceforge.pmd.util.fxdesigner.util.controls.XpathViolationListCell;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * XPath panel controller.
 *
 * @author Clément Fournier
 * @see ExportXPathWizardController
 * @since 6.0.0
 */
public class XPathPanelController implements Initializable, SettingsOwner {

    private static final Duration XPATH_REFRESH_DELAY = Duration.ofMillis(100);
    private final DesignerRoot designerRoot;
    private final MainDesignerController parent;
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();
    private final ObservableXPathRuleBuilder ruleBuilder = new ObservableXPathRuleBuilder();

    @FXML
    private PropertyTableView propertyTableView;
    @FXML
    private CustomCodeArea xpathExpressionArea;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private ListView<TextAwareNodeWrapper> xpathResultListView;

    // Actually a child of the main view toolbar, but this controller is responsible for it
    @SuppressWarnings("PMD.SingularField")
    private ChoiceBox<String> xpathVersionChoiceBox;


    public XPathPanelController(DesignerRoot owner, MainDesignerController mainController) {
        this.designerRoot = owner;
        parent = mainController;

        getRuleBuilder().setClazz(XPathRule.class);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xpathExpressionArea.setSyntaxHighlighter(new XPathSyntaxHighlighter());

        initGenerateXPathFromStackTrace();

        xpathResultListView.setCellFactory(v -> new XpathViolationListCell());

        EventStreams.valuesOf(xpathResultListView.getSelectionModel().selectedItemProperty())
                    .conditionOn(xpathResultListView.focusedProperty())
                    .filter(Objects::nonNull)
                    .map(TextAwareNodeWrapper::getNode)
                    .subscribe(parent::onNodeItemSelected);

        Platform.runLater(this::bindToParent);

        xpathExpressionArea.richChanges()
                           .filter(t -> !t.isIdentity())
                           .successionEnds(XPATH_REFRESH_DELAY)
                           // Reevaluate XPath anytime the expression or the XPath version changes
                           .or(xpathVersionProperty().changes())
                           .subscribe(tick -> parent.refreshXPathResults());
    }


    private void initGenerateXPathFromStackTrace() {

        ContextMenu menu = new ContextMenu();

        MenuItem item = new MenuItem("Generate from stack trace...");
        item.setOnAction(e -> {
            try {
                Stage popup = new Stage();
                FXMLLoader loader = new FXMLLoader(DesignerUtil.getFxml("generate-xpath-from-stack-trace.fxml"));
                Parent root = loader.load();
                Button button = (Button) loader.getNamespace().get("generateButton");
                TextArea area = (TextArea) loader.getNamespace().get("stackTraceArea");

                ValidationSupport validation = new ValidationSupport();

                validation.registerValidator(area, Validator.createEmptyValidator("The stack trace may not be empty"));
                button.disableProperty().bind(validation.invalidProperty());

                button.setOnAction(f -> {
                    DesignerUtil.stackTraceToXPath(area.getText()).ifPresent(xpathExpressionArea::replaceText);
                    popup.close();
                });

                popup.setScene(new Scene(root));
                popup.initStyle(StageStyle.UTILITY);
                popup.initModality(Modality.WINDOW_MODAL);
                popup.initOwner(designerRoot.getMainStage());
                popup.show();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });

        menu.getItems().add(item);

        xpathExpressionArea.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                menu.show(xpathExpressionArea, t.getScreenX(), t.getScreenY());
            }
        });
    }


    // Binds the underlying rule parameters to the parent UI, disconnecting it from the wizard if need be
    private void bindToParent() {
        DesignerUtil.rewire(getRuleBuilder().languageProperty(),
                            Val.map(parent.languageVersionProperty(), LanguageVersion::getLanguage));

        DesignerUtil.rewire(getRuleBuilder().xpathVersionProperty(), parent.xpathVersionProperty());
        DesignerUtil.rewire(getRuleBuilder().xpathExpressionProperty(), xpathExpressionProperty());

        DesignerUtil.rewire(getRuleBuilder().rulePropertiesProperty(),
                            propertyTableView.rulePropertiesProperty(), propertyTableView::setRuleProperties);
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
     * Evaluate the contents of the XPath expression area
     * on the given compilation unit. This updates the xpath
     * result panel, and can log XPath exceptions to the
     * event log panel.
     *
     * @param compilationUnit The AST root
     * @param version         The language version
     */
    public void evaluateXPath(Node compilationUnit, LanguageVersion version) {

        try {
            String xpath = getXpathExpression();
            if (StringUtils.isBlank(xpath)) {
                invalidateResults(false);
                return;
            }

            ObservableList<Node> results
                    = FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit,
                                                                                     version,
                                                                                     getXpathVersion(),
                                                                                     xpath,
                                                                                     ruleBuilder.getRuleProperties()));
            xpathResultListView.setItems(results.stream().map(parent::wrapNode).collect(Collectors.toCollection(LiveArrayList::new)));
            parent.highlightXPathResults(results);
            violationsTitledPane.setText("Matched nodes\t(" + results.size() + ")");
        } catch (XPathEvaluationException e) {
            invalidateResults(true);
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.XPATH_EVALUATION_EXCEPTION));
        }

        xpathResultListView.refresh();


    }


    public List<Node> runXPathQuery(Node compilationUnit, LanguageVersion version, String query) throws XPathEvaluationException {
        return xpathEvaluator.evaluateQuery(compilationUnit, version, "2.0", query, ruleBuilder.getRuleProperties());
    }


    public void invalidateResults(boolean error) {
        xpathResultListView.getItems().clear();
        parent.resetXPathResults();
        violationsTitledPane.setText("Matched nodes" + (error ? "\t(error)" : ""));
    }


    public void showExportXPathToRuleWizard() throws IOException {
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
