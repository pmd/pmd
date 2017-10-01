/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.util.fxdesigner.model.MetricEvaluator;
import net.sourceforge.pmd.util.fxdesigner.model.MetricResult;
import net.sourceforge.pmd.util.fxdesigner.util.controls.MetricResultListCell;
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
public class NodeInfoPanelController implements Initializable {

    private final DesignerApp designerApp;

    @FXML
    private TabPane nodeInfoTabPane;
    @FXML
    private Tab xpathAttributesTitledPane;
    @FXML
    private ListView<String> xpathAttributesListView;
    @FXML
    private Tab metricResultsTitledPane;
    @FXML
    private ListView<MetricResult> metricResultsListView;
    @FXML
    private Label metricsTitleLabel;
    @FXML
    private TreeView<Object> scopeHierarchyTreeView;
    private MetricEvaluator metricEvaluator = new MetricEvaluator();


    NodeInfoPanelController(DesignerApp root) {
        this.designerApp = root;
    }


    /**
     * Displays info about a node.
     *
     * @param node Node to inspect
     */
    public void displayInfo(Node node) {

        Objects.requireNonNull(node, "Node cannot be null");

        ObservableList<String> atts = getAttributes(node);
        xpathAttributesListView.setItems(atts);

        ObservableList<MetricResult> metrics = evaluateAllMetrics(node);
        metricResultsListView.setItems(metrics);
        notifyMetricsAvailable(metrics.stream()
                                      .map(MetricResult::getValue)
                                      .filter(result -> !result.isNaN())
                                      .count());


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
        metricResultsTitledPane.setText("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + ")");
        metricsTitleLabel.setText("Metrics\t(" + (numMetrics == 0 ? "none" : numMetrics) + " available)");
        metricResultsTitledPane.setDisable(numMetrics == 0);
    }


    private ObservableList<MetricResult> evaluateAllMetrics(Node n) {
        try {
            return FXCollections.observableArrayList(metricEvaluator.evaluateAllMetrics(n));
        } catch (UnsupportedOperationException e) {
            return FXCollections.emptyObservableList();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metricResultsListView.setCellFactory(param -> new MetricResultListCell());
        scopeHierarchyTreeView.setCellFactory(param -> new ScopeHierarchyTreeCell());

    }


    /** Gets the XPath attributes of the node for display within a listview. */
    private static ObservableList<String> getAttributes(Node node) {
        ObservableList<String> result = FXCollections.observableArrayList();
        AttributeAxisIterator attributeAxisIterator = new AttributeAxisIterator(node);
        while (attributeAxisIterator.hasNext()) {
            Attribute attribute = attributeAxisIterator.next();
            result.add(attribute.getName() + " = "
                           + ((attribute.getValue() != null) ? attribute.getStringValue() : "null"));
        }

        if (node instanceof TypeNode) {
            result.add("typeof() = " + ((TypeNode) node).getType());
        }
        Collections.sort(result);
        return result;
    }
}
