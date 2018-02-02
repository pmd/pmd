/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class MetricFunction implements Function {


    private static final Map<String, JavaClassMetricKey> CLASS_METRIC_KEY_MAP = EnumUtils.getEnumMap(JavaClassMetricKey.class);
    private static final Map<String, JavaOperationMetricKey> OPERATION_METRIC_KEY_MAP = EnumUtils.getEnumMap(JavaOperationMetricKey.class);


    @Override
    public Object call(Context context, List args) throws FunctionCallException {

        String metricKeyName = null;

        if (args.isEmpty()) {
            throw new IllegalArgumentException(badMetricKeyArgMessage());
        }

        if (!(args.get(0) instanceof String)) {
            throw new IllegalArgumentException(badMetricKeyArgMessage());
        }

        metricKeyName = (String) args.get(0);

        Node n = (Node) context.getNodeSet().get(0);
        return getMetric(n, metricKeyName);
    }


    public static String badOperationMetricKeyMessage() {
        return "This is not the name of an operation metric";
    }


    public static String badClassMetricKeyMessage() {
        return "This is not the name of a class metric";
    }


    public static String genericBadNodeMessage() {
        return "Incorrect node type: the 'metric' function cannot be applied";
    }


    public static String badMetricKeyArgMessage() {
        return "The 'metric' function expects the name of a metric key";
    }


    public static double getMetric(Node n, String metricKeyName) {
        if (n instanceof ASTAnyTypeDeclaration) {
            return getClassMetric((ASTAnyTypeDeclaration) n, getClassMetricKey(metricKeyName));
        } else if (n instanceof ASTMethodOrConstructorDeclaration) {
            return getOpMetric((ASTMethodOrConstructorDeclaration) n, getOperationMetricKey(metricKeyName));
        } else {
            throw new IllegalStateException(genericBadNodeMessage());
        }
    }


    private static JavaClassMetricKey getClassMetricKey(String s) {
        String constantName = s.toUpperCase();
        if (!CLASS_METRIC_KEY_MAP.containsKey(constantName)) {
            throw new IllegalArgumentException(badClassMetricKeyMessage());
        }
        return CLASS_METRIC_KEY_MAP.get(constantName);
    }


    private static JavaOperationMetricKey getOperationMetricKey(String s) {
        String constantName = s.toUpperCase();
        if (!OPERATION_METRIC_KEY_MAP.containsKey(constantName)) {
            throw new IllegalArgumentException(badOperationMetricKeyMessage());
        }
        return OPERATION_METRIC_KEY_MAP.get(constantName);
    }


    private static double getOpMetric(ASTMethodOrConstructorDeclaration node, JavaOperationMetricKey key) {
        return JavaMetrics.get(key, node);
    }


    private static double getClassMetric(ASTAnyTypeDeclaration node, JavaClassMetricKey key) {
        return JavaMetrics.get(key, node);
    }


    public static void registerSelfInSimpleContext() {
        ((SimpleFunctionContext) XPathFunctionContext.getInstance()).registerFunction(null,
                                                                                      "metric",
                                                                                      new MetricFunction());
    }
}
