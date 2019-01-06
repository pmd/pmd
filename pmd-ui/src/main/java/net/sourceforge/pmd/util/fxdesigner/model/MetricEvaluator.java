/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricResult;


/**
 * Evaluates metrics.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class MetricEvaluator {

    /**
     * Evaluates all available metrics and returns a list of results.
     *
     * @param node Node
     *
     * @return List of all metric results (metric key + result), including NaN results
     *
     * @throws UnsupportedOperationException If no metrics are available for this node
     */
    public List<MetricResult> evaluateAllMetrics(Node node) throws UnsupportedOperationException {
        if (ASTAnyTypeDeclaration.class.isInstance(node)) {
            return evaluateAllMetrics((ASTAnyTypeDeclaration) node);
        } else if (ASTMethodOrConstructorDeclaration.class.isInstance(node)) {
            return evaluateAllMetrics((ASTMethodOrConstructorDeclaration) node);
        } else if (ASTMethod.class.isInstance(node)) {
            return evaluateAllMetrics((ASTMethod) node);
        } else if (ASTUserClass.class.isInstance(node)) {
            return evaluateAllMetrics((ASTUserClass) node);
        }
        throw new UnsupportedOperationException("That language does not support metrics");
    }


    private List<MetricResult> evaluateAllMetrics(ASTMethodOrConstructorDeclaration node) {
        List<MetricResult> metricResults = new ArrayList<>();
        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            metricResults.add(new MetricResult(key, JavaMetrics.get(key, node)));
        }

        return metricResults;
    }


    private List<MetricResult> evaluateAllMetrics(ASTAnyTypeDeclaration node) {
        List<MetricResult> metricResults = new ArrayList<>();
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            metricResults.add(new MetricResult(key, JavaMetrics.get(key, node)));
        }

        return metricResults;
    }


    private List<MetricResult> evaluateAllMetrics(ASTMethod node) {
        List<MetricResult> metricResults = new ArrayList<>();
        for (ApexOperationMetricKey key : ApexOperationMetricKey.values()) {
            metricResults.add(new MetricResult(key, ApexMetrics.get(key, node)));
        }

        return metricResults;
    }


    private List<MetricResult> evaluateAllMetrics(ASTUserClass node) {
        List<MetricResult> metricResults = new ArrayList<>();
        for (ApexClassMetricKey key : ApexClassMetricKey.values()) {
            metricResults.add(new MetricResult(key, ApexMetrics.get(key, node)));
        }

        return metricResults;
    }

}
