/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BigDecimalValue;
import net.sf.saxon.value.SequenceType;


/**
 * Implements the {@code metric()} XPath function. Takes the
 * string name of a metric and the context node and returns
 * the result if the metric can be computed, otherwise returns
 * {@link Double#NaN}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class MetricFunction extends BaseJavaXPathFunction {

    public static final MetricFunction INSTANCE = new MetricFunction();

    private static final Map<String, JavaClassMetricKey> CLASS_METRIC_KEY_MAP = EnumUtils.getEnumMap(JavaClassMetricKey.class);
    private static final Map<String, JavaOperationMetricKey> OPERATION_METRIC_KEY_MAP = EnumUtils.getEnumMap(JavaOperationMetricKey.class);

    private MetricFunction() {
        super("metric");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_STRING};
    }


    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.SINGLE_DECIMAL;
    }


    @Override
    public boolean dependsOnFocus() {
        return true;
    }


    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();
                String metricKey = arguments[0].head().getStringValue();

                return new BigDecimalValue(getMetric(contextNode, metricKey));
            }
        };
    }


    static String badOperationMetricKeyMessage() {
        return "This is not the name of an operation metric";
    }


    static String badClassMetricKeyMessage() {
        return "This is not the name of a class metric";
    }


    static String genericBadNodeMessage() {
        return "Incorrect node type: the 'metric' function cannot be applied";
    }

    private static double getMetric(Node n, String metricKeyName) {
        if (n instanceof ASTAnyTypeDeclaration) {
            return computeMetric(getClassMetricKey(metricKeyName), (ASTAnyTypeDeclaration) n);
        } else if (n instanceof MethodLikeNode) {
            return computeMetric(getOperationMetricKey(metricKeyName), (MethodLikeNode) n);
        } else {
            throw new IllegalStateException(genericBadNodeMessage());
        }
    }

    private static <T extends Node> double computeMetric(MetricKey<T> metricKey, T n) {
        return metricKey.supports(n) ? MetricsUtil.computeMetric(metricKey, n) : Double.NaN;
    }


    private static JavaClassMetricKey getClassMetricKey(String s) {
        String constantName = s.toUpperCase(Locale.ROOT);
        if (!CLASS_METRIC_KEY_MAP.containsKey(constantName)) {
            throw new IllegalArgumentException(badClassMetricKeyMessage());
        }
        return CLASS_METRIC_KEY_MAP.get(constantName);
    }


    private static JavaOperationMetricKey getOperationMetricKey(String s) {
        String constantName = s.toUpperCase(Locale.ROOT);
        if (!OPERATION_METRIC_KEY_MAP.containsKey(constantName)) {
            throw new IllegalArgumentException(badOperationMetricKeyMessage());
        }
        return OPERATION_METRIC_KEY_MAP.get(constantName);
    }

}
