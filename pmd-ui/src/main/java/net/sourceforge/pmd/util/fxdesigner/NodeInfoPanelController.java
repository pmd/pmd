/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import static net.sourceforge.pmd.util.fxdesigner.util.DesignerIteratorUtil.parentIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.SuspendableEventStream;
import org.reactfx.value.Var;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.util.fxdesigner.app.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.model.MetricResult;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeCell;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeItem;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ToolbarTitledPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


/**
 * Controller of the node info panel (left).
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public class NodeInfoPanelController extends AbstractController<MainDesignerController> implements NodeSelectionSource {


    /** List of attribute names that are ignored if {@link #isHideCommonAttributes()} is true. */
    private static final List<String> IGNORABLE_ATTRIBUTES =
        Arrays.asList("BeginLine", "EndLine", "BeginColumn", "EndColumn", "FindBoundary", "SingleLine");

    @FXML
    private ToggleButton hideCommonAttributesToggle;
    @FXML
    private ToolbarTitledPane metricsTitledPane;
    @FXML
    private TabPane nodeInfoTabPane;
    @FXML
    private Tab xpathAttributesTab;
    @FXML
    private ListView<String> xpathAttributesListView;
    @FXML
    private Tab metricResultsTab;
    @FXML
    private ListView<MetricResult> metricResultsListView;
    @FXML
    private TreeView<Object> scopeHierarchyTreeView;

    private Node selectedNode;

    private SuspendableEventStream<TreeItem<Object>> myScopeItemSelectionEvents;


    public NodeInfoPanelController(MainDesignerController mainController) {
        super(mainController);
    }



    @Override
    protected void beforeParentInit() {

        xpathAttributesListView.setPlaceholder(new Label("No available attributes"));

        scopeHierarchyTreeView.setCellFactory(view -> new ScopeHierarchyTreeCell());

        hideCommonAttributesProperty()
            .values()
            .distinct()
            .subscribe(show -> displayAttributes(selectedNode));

        // suppress as early as possible in the pipeline
        myScopeItemSelectionEvents = EventStreams.valuesOf(scopeHierarchyTreeView.getSelectionModel().selectedItemProperty()).suppressible();

        EventStream<Node> selectionEvents = myScopeItemSelectionEvents.filter(Objects::nonNull)
                                                                      .map(TreeItem::getValue)
                                                                      .filterMap(o -> o instanceof NameDeclaration, o -> (NameDeclaration) o)
                                                                      .map(NameDeclaration::getNode);

        // TODO split this into independent NodeSelectionSources
        initNodeSelectionHandling(getDesignerRoot(), selectionEvents, true);
    }



    /**
     * Displays info about a node. If null, the panels are reset.
     *
     * @param node Node to inspect
     */
    @Override
    public void setFocusNode(Node node) {
        if (node == null) {
            invalidateInfo();
            return;
        }

        if (node.equals(selectedNode)) {
            return;
        }
        selectedNode = node;

        displayAttributes(node);
        displayMetrics(node);
        displayScopes(node);
    }


    private void displayAttributes(Node node) {
        xpathAttributesListView.setItems(getAttributes(node));
    }


    private void displayMetrics(Node node) {
        ObservableList<MetricResult> metrics = evaluateAllMetrics(node);
        metricResultsListView.setItems(metrics);
        notifyMetricsAvailable(metrics.stream()
                                      .map(MetricResult::getValue)
                                      .filter(result -> !result.isNaN())
                                      .count());
    }


    private void displayScopes(Node node) {

        // current selection
        TreeItem<Object> previousSelection = scopeHierarchyTreeView.getSelectionModel().getSelectedItem();

        ScopeHierarchyTreeItem rootScope = ScopeHierarchyTreeItem.buildAscendantHierarchy(node);
        scopeHierarchyTreeView.setRoot(rootScope);

        if (previousSelection != null) {
            // Try to find the node that was previously selected and focus it in the new ascendant hierarchy.
            // Otherwise, when you select a node in the scope tree, since focus of the app is shifted to that
            // node, the scope hierarchy is reset and you lose the selection - even though obviously the node
            // you selected is in its own scope hierarchy so it looks buggy.
            int maxDepth = IteratorUtil.count(parentIterator(previousSelection, true));
            rootScope.tryFindNode(previousSelection.getValue(), maxDepth)
                     // suspend notifications while selecting
                     .ifPresent(item -> myScopeItemSelectionEvents.suspendWhile(() -> scopeHierarchyTreeView.getSelectionModel().select(item)));
        }
    }

    /**
     * Invalidates the info being displayed.
     */
    private void invalidateInfo() {
        metricResultsListView.setItems(FXCollections.emptyObservableList());
        xpathAttributesListView.setItems(FXCollections.emptyObservableList());
        scopeHierarchyTreeView.setRoot(null);
    }


    private void notifyMetricsAvailable(long numMetrics) {
        metricResultsTab.setText("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + ")");
        metricsTitledPane.setTitle("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + " available)");
        metricResultsTab.setDisable(numMetrics == 0);
    }


    private ObservableList<MetricResult> evaluateAllMetrics(Node n) {
        LanguageMetricsProvider<?, ?> provider = parent.getLanguageVersion().getLanguageVersionHandler().getLanguageMetricsProvider();
        if (provider == null) {
            return FXCollections.emptyObservableList();
        }
        List<MetricResult> resultList =
            provider.computeAllMetricsFor(n)
                    .entrySet()
                    .stream()
                    .map(e -> new MetricResult(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        return FXCollections.observableArrayList(resultList);
    }


    @PersistentProperty
    public boolean isHideCommonAttributes() {
        return hideCommonAttributesToggle.isSelected();
    }


    public void setHideCommonAttributes(boolean bool) {
        hideCommonAttributesToggle.setSelected(bool);
    }


    public Var<Boolean> hideCommonAttributesProperty() {
        return Var.fromVal(hideCommonAttributesToggle.selectedProperty(), hideCommonAttributesToggle::setSelected);
    }


    /**
     * Gets the XPath attributes of the node for display within a listview.
     */
    private ObservableList<String> getAttributes(Node node) {
        if (node == null) {
            return FXCollections.emptyObservableList();
        }

        ObservableList<String> result = FXCollections.observableArrayList();
        Iterator<Attribute> attributeAxisIterator = node.getXPathAttributesIterator();
        while (attributeAxisIterator.hasNext()) {
            Attribute attribute = attributeAxisIterator.next();

            if (!(isHideCommonAttributes() && IGNORABLE_ATTRIBUTES.contains(attribute.getName()))) {
                // TODO the display should be handled in a ListCell
                result.add(attribute.getName() + " = "
                               + ((attribute.getValue() != null) ? attribute.getStringValue() : "null"));
            }
        }

        DesignerUtil.getResolvedType(node).map(t -> "typeIs() = " + t).ifPresent(result::add);

        Collections.sort(result);
        return result;
    }


    @Override
    public String getDebugName() {
        return "info-panel";
    }
}
