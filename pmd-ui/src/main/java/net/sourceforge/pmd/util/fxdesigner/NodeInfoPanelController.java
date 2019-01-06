/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.reactfx.EventStreams;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.util.fxdesigner.model.MetricResult;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeCell;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ScopeHierarchyTreeItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


/**
 * Controller of the node info panel (left).
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public class NodeInfoPanelController implements Initializable {

    private final MainDesignerController parent;

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
    private Label metricsTitleLabel;
    @FXML
    private TreeView<Object> scopeHierarchyTreeView;
    private Node selectedNode;

    public NodeInfoPanelController(MainDesignerController mainController) {
        parent = mainController;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventStreams.valuesOf(scopeHierarchyTreeView.getSelectionModel().selectedItemProperty())
                    .filter(Objects::nonNull)
                    .map(TreeItem::getValue)
                    .filterMap(o -> o instanceof NameDeclaration, o -> (NameDeclaration) o)
                    .subscribe(parent::onNameDeclarationSelected);

        scopeHierarchyTreeView.setCellFactory(view -> new ScopeHierarchyTreeCell());
    }


    /**
     * Displays info about a node.
     *
     * @param node Node to inspect
     */
    public void displayInfo(Node node) {
        Objects.requireNonNull(node, "Node cannot be null");

        if (node.equals(selectedNode)) {
            return;
        }

        ObservableList<String> atts = getAttributes(node);
        xpathAttributesListView.setItems(atts);

        ObservableList<MetricResult> metrics = evaluateAllMetrics(node);
        metricResultsListView.setItems(metrics);
        notifyMetricsAvailable(metrics.stream()
                                      .map(MetricResult::getValue)
                                      .filter(result -> !result.isNaN())
                                      .count());

        // TODO maybe a better way would be to build all the scope TreeItem hierarchy once
        // and only expand the ascendants of the node.
        TreeItem<Object> rootScope = ScopeHierarchyTreeItem.buildAscendantHierarchy(node);
        scopeHierarchyTreeView.setRoot(rootScope);
    }


    /**
     * Invalidates the info being displayed.
     */
    public void invalidateInfo() {
        metricResultsListView.setItems(FXCollections.emptyObservableList());
        xpathAttributesListView.setItems(FXCollections.emptyObservableList());
        scopeHierarchyTreeView.setRoot(null);
    }


    private void notifyMetricsAvailable(long numMetrics) {
        metricResultsTab.setText("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + ")");
        metricsTitleLabel.setText("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + " available)");
        metricResultsTab.setDisable(numMetrics == 0);
    }


    private ObservableList<MetricResult> evaluateAllMetrics(Node n) {
        Map<MetricKey<?>, Double> results = parent.getLanguageVersion().getLanguageVersionHandler().getLanguageMetricsProvider().computeAllMetricsFor(n);
        List<MetricResult> resultList = results.entrySet().stream().map(e -> new MetricResult(e.getKey(), e.getValue())).collect(Collectors.toList());
        return FXCollections.observableArrayList(resultList);
    }


    /**
     * Gets the XPath attributes of the node for display within a listview.
     */
    private static ObservableList<String> getAttributes(Node node) {
        ObservableList<String> result = FXCollections.observableArrayList();
        Iterator<Attribute> attributeAxisIterator = node.getXPathAttributesIterator();
        while (attributeAxisIterator.hasNext()) {
            Attribute attribute = attributeAxisIterator.next();
            // TODO the display should be handled in a ListCell
            result.add(attribute.getName() + " = "
                               + ((attribute.getValue() != null) ? attribute.getStringValue() : "null"));
        }

        // TODO maybe put some equivalent to TypeNode inside pmd-core
        //        if (node instanceof TypeNode) {
        //            result.add("typeIs() = " + ((TypeNode) node).getType());
        //        }
        Collections.sort(result);
        return result;
    }
}
