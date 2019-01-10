/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.collection.LiveArrayList;
import org.reactfx.util.Tuples;
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
import net.sourceforge.pmd.util.fxdesigner.model.XPathSuggestions;
import net.sourceforge.pmd.util.fxdesigner.popups.ExportXPathWizardController;
import net.sourceforge.pmd.util.fxdesigner.util.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlightingCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XPathSyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ContextMenuWithNoArrows;
import net.sourceforge.pmd.util.fxdesigner.util.controls.PropertyTableView;
import net.sourceforge.pmd.util.fxdesigner.util.controls.XpathViolationListCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
public class XPathPanelController extends AbstractController {

    private static final Duration XPATH_REFRESH_DELAY = Duration.ofMillis(100);
    private final DesignerRoot designerRoot;
    private final MainDesignerController parent;
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();
    private final ObservableXPathRuleBuilder ruleBuilder = new ObservableXPathRuleBuilder();


    @FXML
    public TitledPane centerTitledPane;
    @FXML
    private PropertyTableView propertyTableView;
    @FXML
    private SyntaxHighlightingCodeArea xpathExpressionArea;
    @FXML
    private TitledPane violationsTitledPane;
    @FXML
    private ListView<TextAwareNodeWrapper> xpathResultListView;
    @FXML
    ToggleGroup xpathVersionToggleGroup;

    // ui property
    private Var<String> xpathVersionUIProperty = Var.newSimpleVar(XPathRuleQuery.XPATH_2_0);


    public XPathPanelController(DesignerRoot owner, MainDesignerController mainController) {
        this.designerRoot = owner;
        parent = mainController;

        getRuleBuilder().setClazz(XPathRule.class);
    }

    @Override
    protected void beforeParentInit() {
        xpathExpressionArea.setSyntaxHighlighter(new XPathSyntaxHighlighter());

        initGenerateXPathFromStackTrace();

        Map<String, Toggle> stringToButton = new HashMap<>();

        xpathVersionUIProperty = Var.fromVal(xpathVersionToggleGroup.selectedToggleProperty(), xpathVersionToggleGroup::selectToggle)
                                    .mapBidirectional(
                                        toggle -> toggle.getUserData().toString(),
                                        str -> xpathVersionToggleGroup.getToggles()
                                                                      .stream()
                                                                      .filter(t -> t.getUserData().equals(str))
                                                                      .findFirst()
                                                                      .orElseThrow(() -> new IllegalArgumentException("Unknown XPath version"))
                                    );

        xpathResultListView.setCellFactory(v -> new XpathViolationListCell());

        EventStreams.valuesOf(xpathResultListView.getSelectionModel().selectedItemProperty())
                    .conditionOn(xpathResultListView.focusedProperty())
                    .filter(Objects::nonNull)
                    .map(TextAwareNodeWrapper::getNode)
                    .subscribe(parent::onNodeItemSelected);

        xpathExpressionArea.richChanges()
                           .filter(t -> !t.isIdentity())
                           .successionEnds(XPATH_REFRESH_DELAY)
                           // Reevaluate XPath anytime the expression or the XPath version changes
                           .or(xpathVersionProperty().changes())
                           .subscribe(tick -> parent.refreshXPathResults());

        initialiseAutoCompletion();
    }



    @Override
    protected void afterParentInit() {

        DesignerUtil.rewireInit(getRuleBuilder().xpathVersionProperty(), xpathVersionProperty());
        DesignerUtil.rewireInit(getRuleBuilder().xpathExpressionProperty(), xpathExpressionProperty());
        bindToParent();
    }


    private void initialiseAutoCompletion() {

        EventStream<Integer> changesEventStream = xpathExpressionArea.plainTextChanges()
                                                                     .map(characterChanges -> {
                                                                         if (characterChanges.getRemoved().length() > 0) {
                                                                             return characterChanges.getRemovalEnd() - 1;
                                                                         }
                                                                         return characterChanges.getInsertionEnd();
                                                                     });

        EventStream<Integer> keyCombo = EventStreams.eventsOf(xpathExpressionArea, KeyEvent.KEY_PRESSED)
                                                    .filter(key -> key.isControlDown() && key.getCode().equals(KeyCode.SPACE))
                                                    .map(searchPoint -> xpathExpressionArea.getCaretPosition());

        // captured in the closure
        final ContextMenu autoCompletePopup = new ContextMenuWithNoArrows();
        autoCompletePopup.setId("xpathAutocomplete");
        autoCompletePopup.setHideOnEscape(true);

        EventStreams.merge(keyCombo, changesEventStream)
                    .map(searchPoint -> {
                        int indexOfSlash = xpathExpressionArea.getText().lastIndexOf("/", searchPoint - 1) + 1;
                        String input = xpathExpressionArea.getText();
                        if (searchPoint > input.length()) {
                            searchPoint = input.length();
                        }
                        input = input.substring(indexOfSlash, searchPoint);

                        return Tuples.t(indexOfSlash, input);
                    })
                    .filter(t -> StringUtils.isAlpha(t._2))
                    .subscribe(s -> autoComplete(s._1, s._2, autoCompletePopup));


    }


    private void autoComplete(int slashPosition, String input, ContextMenu autoCompletePopup) {

        XPathSuggestions xPathSuggestions = new XPathSuggestions(parent.getLanguageVersion().getLanguage());
        List<String> suggestions = xPathSuggestions.getXPathSuggestions(input.trim());

        List<CustomMenuItem> resultToDisplay = new ArrayList<>();
        if (!suggestions.isEmpty()) {

            for (int i = 0; i < suggestions.size() && i < 5; i++) {
                final String searchResult = suggestions.get(i);

                Label entryLabel = new Label();
                entryLabel.setGraphic(highlightXPathSuggestion(suggestions.get(i), input));
                entryLabel.setPrefHeight(5);
                CustomMenuItem item = new CustomMenuItem(entryLabel, true);
                resultToDisplay.add(item);

                item.setOnAction(e -> {
                    xpathExpressionArea.replaceText(slashPosition, slashPosition + input.length(), searchResult);
                    autoCompletePopup.hide();
                });
            }
        }
        autoCompletePopup.getItems().setAll(resultToDisplay);

        xpathExpressionArea.getCharacterBoundsOnScreen(slashPosition, slashPosition + input.length())
                           .ifPresent(bounds -> autoCompletePopup.show(xpathExpressionArea, bounds.getMinX(), bounds.getMaxY()));
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

        DesignerUtil.rewire(getRuleBuilder().xpathVersionProperty(), xpathVersionProperty());
        DesignerUtil.rewire(getRuleBuilder().xpathExpressionProperty(), xpathExpressionProperty());

        DesignerUtil.rewireInit(getRuleBuilder().rulePropertiesProperty(),
                                propertyTableView.rulePropertiesProperty(), propertyTableView::setRuleProperties);
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
        return xpathEvaluator.evaluateQuery(compilationUnit, version, XPathRuleQuery.XPATH_2_0, query, ruleBuilder.getRuleProperties());
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


    private static TextFlow highlightXPathSuggestion(String text, String match) {
        int filterIndex = text.toLowerCase(Locale.ROOT).indexOf(match.toLowerCase(Locale.ROOT));

        Text textBefore = new Text(text.substring(0, filterIndex));
        Text textAfter = new Text(text.substring(filterIndex + match.length()));
        Text textFilter = new Text(text.substring(filterIndex, filterIndex + match.length())); //instead of "filter" to keep all "case sensitive"
        textFilter.setFill(Color.ORANGE);
        return new TextFlow(textBefore, textFilter, textAfter);
    }
}
