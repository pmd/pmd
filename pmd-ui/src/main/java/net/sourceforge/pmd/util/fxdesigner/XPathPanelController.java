/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.kordamp.ikonli.javafx.FontIcon;
import org.reactfx.EventStreams;
import org.reactfx.SuspendableEventStream;
import org.reactfx.collection.LiveArrayList;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.fxdesigner.app.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.app.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.model.ObservableXPathRuleBuilder;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluator;
import net.sourceforge.pmd.util.fxdesigner.popups.ExportXPathWizardController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.autocomplete.CompletionResultSource;
import net.sourceforge.pmd.util.fxdesigner.util.autocomplete.XPathAutocompleteProvider;
import net.sourceforge.pmd.util.fxdesigner.util.autocomplete.XPathCompletionSource;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlightingCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.PropertyTableView;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ToolbarTitledPane;
import net.sourceforge.pmd.util.fxdesigner.util.controls.XpathViolationListCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * XPath panel controller. One such controller is a presenter for an {@link ObservableXPathRuleBuilder},
 * which stores all data about one currently edited rule.
 *
 * @author Clément Fournier
 * @see ExportXPathWizardController
 * @since 6.0.0
 */
public class XPathPanelController extends AbstractController<MainDesignerController> implements NodeSelectionSource {

    private static final Pattern EXCEPTION_PREFIX_PATTERN = Pattern.compile("(?:(?:\\w+\\.)*\\w+:\\s*)*\\s*(.*)$", Pattern.DOTALL);
    private static final String NO_MATCH_MESSAGE = "No match in text";
    private static final Duration XPATH_REFRESH_DELAY = Duration.ofMillis(100);
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();
    private final ObservableXPathRuleBuilder ruleBuilder = new ObservableXPathRuleBuilder();


    @FXML
    public ToolbarTitledPane expressionTitledPane;
    @FXML
    public Button exportXpathToRuleButton;
    @FXML
    private MenuButton xpathVersionMenuButton;
    @FXML
    private PropertyTableView propertyTableView;
    @FXML
    private SyntaxHighlightingCodeArea xpathExpressionArea;
    @FXML
    private ToolbarTitledPane violationsTitledPane;
    @FXML
    private ListView<TextAwareNodeWrapper> xpathResultListView;

    private final Var<List<Node>> currentResults = Var.newSimpleVar(Collections.emptyList());

    // ui property
    private Var<String> xpathVersionUIProperty = Var.newSimpleVar(XPathRuleQuery.XPATH_2_0);

    private SuspendableEventStream<TextAwareNodeWrapper> selectionEvents;

    public XPathPanelController(MainDesignerController mainController) {
        super(mainController);
        getRuleBuilder().setClazz(XPathRule.class);
    }


    @Override
    protected void beforeParentInit() {
        xpathExpressionArea.setSyntaxHighlighter(new XPathSyntaxHighlighter());

        initGenerateXPathFromStackTrace();
        initialiseVersionSelection();

        expressionTitledPane.titleProperty().bind(xpathVersionUIProperty.map(v -> "XPath Expression (" + v + ")"));

        xpathResultListView.setCellFactory(v -> new XpathViolationListCell());

        exportXpathToRuleButton.setOnAction(e -> showExportXPathToRuleWizard());

        xpathExpressionArea.richChanges()
                           .filter(t -> !t.isIdentity())
                           .successionEnds(XPATH_REFRESH_DELAY)
                           // Reevaluate XPath anytime the expression or the XPath version changes
                           .or(xpathVersionProperty().changes())
                           .subscribe(tick -> parent.refreshXPathResults());

        selectionEvents = EventStreams.valuesOf(xpathResultListView.getSelectionModel().selectedItemProperty()).suppressible();

        initNodeSelectionHandling(getDesignerRoot(),
                                  selectionEvents.filter(Objects::nonNull).map(TextAwareNodeWrapper::getNode), false);

        violationsTitledPane.titleProperty().bind(currentResults.map(List::size).map(n -> "Matched nodes (" + n + ")"));
    }



    @Override
    protected void afterParentInit() {
        bindToParent();

        // init autocompletion only after binding to parent and settings restore
        // otherwise the popup is shown on startup
        Supplier<CompletionResultSource> suggestionMaker = () -> XPathCompletionSource.forLanguage(parent.getLanguageVersion().getLanguage());
        new XPathAutocompleteProvider(xpathExpressionArea, suggestionMaker).initialiseAutoCompletion();
    }


    // Binds the underlying rule parameters to the parent UI, disconnecting it from the wizard if need be
    private void bindToParent() {
        DesignerUtil.rewire(getRuleBuilder().languageProperty(), Val.map(parent.languageVersionProperty(), LanguageVersion::getLanguage));

        DesignerUtil.rewireInit(getRuleBuilder().xpathVersionProperty(), xpathVersionProperty());
        DesignerUtil.rewireInit(getRuleBuilder().xpathExpressionProperty(), xpathExpressionProperty());

        DesignerUtil.rewireInit(getRuleBuilder().rulePropertiesProperty(),
                                propertyTableView.rulePropertiesProperty(), propertyTableView::setRuleProperties);
    }

    private void initialiseVersionSelection() {
        ToggleGroup xpathVersionToggleGroup = new ToggleGroup();

        List<String> versionItems = new ArrayList<>();
        versionItems.add(XPathRuleQuery.XPATH_1_0);
        versionItems.add(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
        versionItems.add(XPathRuleQuery.XPATH_2_0);

        versionItems.forEach(v -> {
            RadioMenuItem item = new RadioMenuItem("XPath " + v);
            item.setUserData(v);
            item.setToggleGroup(xpathVersionToggleGroup);
            xpathVersionMenuButton.getItems().add(item);
        });

        xpathVersionUIProperty = DesignerUtil.mapToggleGroupToUserData(xpathVersionToggleGroup, DesignerUtil::defaultXPathVersion);

        setXpathVersion(XPathRuleQuery.XPATH_2_0);
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
                popup.initOwner(getDesignerRoot().getMainStage());
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

    @Override
    public void setFocusNode(Node node) {
        Optional<TextAwareNodeWrapper> firstResult = xpathResultListView.getItems().stream()
                           .filter(wrapper -> wrapper.getNode().equals(node))
                           .findFirst();

        // with Java 9, Optional#ifPresentOrElse can be used
        if (firstResult.isPresent()) {
            selectionEvents.suspendWhile(() -> xpathResultListView.getSelectionModel().select(firstResult.get()));
        } else {
            xpathResultListView.getSelectionModel().clearSelection();
        }
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
                updateResults(false, false, Collections.emptyList(), "Type an XPath expression to show results");
                return;
            }

            ObservableList<Node> results
                = FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit,
                                                                                 version,
                                                                                 getXpathVersion(),
                                                                                 xpath,
                                                                                 ruleBuilder.getRuleProperties()));

            updateResults(false, false, results, NO_MATCH_MESSAGE);
            // Notify that everything went OK so we can avoid logging very recent exceptions
            raiseParsableXPathFlag();
        } catch (XPathEvaluationException e) {
            updateResults(true, false, Collections.emptyList(), sanitizeExceptionMessage(e));
            logUserException(e, Category.XPATH_EVALUATION_EXCEPTION);
        }

    }


    public List<Node> runXPathQuery(Node compilationUnit, LanguageVersion version, String query) throws XPathEvaluationException {
        return xpathEvaluator.evaluateQuery(compilationUnit, version, XPathRuleQuery.XPATH_2_0, query, ruleBuilder.getRuleProperties());
    }


    /**
     * Called by the rest of the app.
     */
    public void invalidateResultsExternal(boolean error) {
        String placeholder = error ? "Compilation unit is invalid" : NO_MATCH_MESSAGE;
        updateResults(false, true, Collections.emptyList(), placeholder);
    }


    public void showExportXPathToRuleWizard() {
        ExportXPathWizardController wizard
            = new ExportXPathWizardController(xpathExpressionProperty());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/xpath-export-wizard.fxml"));
        loader.setController(wizard);

        final Stage dialog = new Stage();
        dialog.initOwner(getDesignerRoot().getMainStage());
        dialog.setOnCloseRequest(e -> wizard.shutdown());
        dialog.initModality(Modality.WINDOW_MODAL);

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        //stage.setTitle("PMD Rule Designer (v " + PMD.VERSION + ')');
        dialog.setScene(scene);
        dialog.show();
    }

    public Val<List<Node>> currentResultsProperty() {
        return currentResults;
    }

    public String getXpathExpression() {
        return xpathExpressionArea.getText();
    }


    public void setXpathExpression(String expression) {
        xpathExpressionArea.replaceText(expression);
    }


    public Var<String> xpathExpressionProperty() {
        return Var.fromVal(xpathExpressionArea.textProperty(), this::setXpathExpression);
    }


    public String getXpathVersion() {
        return xpathVersionProperty().getValue();
    }


    public void setXpathVersion(String xpathVersion) {
        xpathVersionProperty().setValue(xpathVersion);
    }


    public Var<String> xpathVersionProperty() {
        return xpathVersionUIProperty;
    }


    private ObservableXPathRuleBuilder getRuleBuilder() {
        return ruleBuilder;
    }


    @Override
    public List<SettingsOwner> getChildrenSettingsNodes() {
        return Collections.singletonList(getRuleBuilder());
    }


    private void updateResults(boolean xpathError,
                               boolean otherError,
                               List<Node> results,
                               String emptyResultsPlaceholder) {

        Label emptyLabel = xpathError || otherError
                           ? new Label(emptyResultsPlaceholder, new FontIcon("fas-exclamation"))
                           : new Label(emptyResultsPlaceholder);

        xpathResultListView.setPlaceholder(emptyLabel);

        xpathResultListView.setItems(results.stream().map(parent::wrapNode).collect(Collectors.toCollection(LiveArrayList::new)));
        this.currentResults.setValue(results);
        // only show the error label here when it's an xpath error
        expressionTitledPane.errorMessageProperty().setValue(xpathError ? emptyResultsPlaceholder : "");
    }


    private static String sanitizeExceptionMessage(Throwable exception) {
        Matcher matcher = EXCEPTION_PREFIX_PATTERN.matcher(exception.getMessage());
        return matcher.matches() ? matcher.group(1) : exception.getMessage();
    }

}
