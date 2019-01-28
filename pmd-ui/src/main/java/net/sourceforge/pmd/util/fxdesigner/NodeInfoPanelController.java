/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import static net.sourceforge.pmd.util.fxdesigner.util.DesignerIteratorUtil.parentIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.value.Var;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;
import net.sourceforge.pmd.util.fxdesigner.model.MetricResult;
import net.sourceforge.pmd.util.fxdesigner.app.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeCell;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeItem;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ToolbarTitledPane;

import javafx.application.Platform;
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

    }


    @Override
    public EventStream<NodeSelectionEvent> getSelectionEvents() {
        return EventStreams.valuesOf(scopeHierarchyTreeView.getSelectionModel().selectedItemProperty())
                           .filter(Objects::nonNull)
                           .map(TreeItem::getValue)
                           .filterMap(o -> o instanceof NameDeclaration, o -> (NameDeclaration) o)
                           .map(NameDeclaration::getNode)
                           .map(n -> new NodeSelectionEvent(n, this));

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

        Platform.runLater(() -> displayAttributes(node));
        Platform.runLater(() -> displayMetrics(node));
        displayScopes(node);

        if (node instanceof ScopedNode) {
            // not null as well
            highlightNameOccurences((ScopedNode) node);
        }
    }


    private void highlightNameOccurences(ScopedNode node) {

        // For MethodNameDeclaration the scope is the method scope, which is not the scope it is declared
        // in but the scope it declares! That means that getDeclarations().get(declaration) returns null
        // and no name occurrences are found. We thus look in the parent, but ultimately the name occurrence
        // finder is broken since it can't find e.g. the use of a method in another scope. Plus in case of
        // overloads both overloads are reported to have a usage.

        // Plus this is some serious law of Demeter breaking there...

        Set<NameDeclaration> candidates = new HashSet<>(node.getScope().getDeclarations().keySet());

        Optional.ofNullable(node.getScope().getParent())
                .map(Scope::getDeclarations)
                .map(Map::keySet)
                .ifPresent(candidates::addAll);

        List<NameOccurrence> occurrences =
            candidates.stream()
                      .filter(nd -> node.equals(nd.getNode()))
                      .findFirst()
                      .map(nd -> {
                          // nd.getScope() != nd.getNode().getScope()?? wtf?

                          List<NameOccurrence> usages = nd.getNode().getScope().getDeclarations().get(nd);

                          if (usages == null) {
                              usages = nd.getNode().getScope().getParent().getDeclarations().get(nd);
                          }

                          return usages;
                      })
                      .orElse(Collections.emptyList());

        parent.highlightAsNameOccurences(occurrences);
    }


    private void displayAttributes(Node node) {
        ObservableList<String> atts = getAttributes(node);
        xpathAttributesListView.setItems(atts);
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
                     .ifPresent(scopeHierarchyTreeView.getSelectionModel()::select);
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

        // TODO maybe put some equivalent to TypeNode inside pmd-core
        //        if (node instanceof TypeNode) {
        //            result.add("typeIs() = " + ((TypeNode) node).getType());
        //        }
        Collections.sort(result);
        return result;
    }


    @Override
    public String getDebugName() {
        return "info-panel";
    }
}
