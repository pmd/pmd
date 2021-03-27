/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.metrics.internal.CognitiveComplexityVisitor;
import net.sourceforge.pmd.lang.apex.metrics.internal.CognitiveComplexityVisitor.State;
import net.sourceforge.pmd.lang.apex.metrics.internal.StandardCycloVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * Built-in Apex metrics. See {@link Metric} and {@link MetricsUtil}
 * for usage doc.
 */
public final class ApexMetrics {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<ApexNode<?>> GENERIC_APEX_NODE_CLASS =
        (Class) ApexNode.class; // this is a Class<ApexNode>, the raw type


    /**
     * Number of independent paths through a block of code.
     * Formally, given that the control flow graph of the block has n
     * vertices, e edges and p connected components, the cyclomatic complexity
     * of the block is given by {@code CYCLO = e - n + 2p}. In practice
     * it can be calculated by counting control flow statements following
     * the standard rules given below.
     *
     *
     * <p>The standard version of the metric complies with McCabe’s original definition:
     * <ul>
     *  <li>Methods have a base complexity of 1.
     *  <li>+1 for every control flow statement (if, catch, throw, do, while, for, break, continue) and conditional expression (?:).
     *  <li>else, finally and default do not count;
     *  <li>+1 for every boolean operator ({@code &&}, {@code ||}) in
     *  the guard condition of a control flow statement. That’s because
     *  Apex has short-circuit evaluation semantics for boolean operators,
     *  which makes every boolean operator kind of a control flow statement in itself.
     * </ul>
     *
     * <p>Code example:
     * <pre>{@code
     * class Foo {
     *   void baseCyclo() {                // Cyclo = 1
     *     highCyclo();
     *   }
     *
     *   void highCyclo() {                // Cyclo = 10
     *     int x = 0, y = 2;
     *     boolean a = false, b = true;
     *
     *     if (a && (y == 1 ? b : true)) { // +3
     *       if (y == x) {                 // +1
     *         while (true) {              // +1
     *           if (x++ < 20) {           // +1
     *             break;                  // +1
     *           }
     *         }
     *       } else if (y == t && !d) {    // +2
     *         x = a ? y : x;              // +1
     *       } else {
     *         x = 2;
     *       }
     *     }
     *   }
     * }
     * }</pre>
     */
    public static final Metric<ApexNode<?>, Integer> CYCLO =
        Metric.of(ApexMetrics::computeCyclo, isRegularApexNode(),
                  "Cyclomatic Complexity", "Cyclo");

    public static final Metric<ApexNode<?>, Integer> COGNITIVE_COMPLEXITY =
        Metric.of(ApexMetrics::computeCognitiveComp, isRegularApexNode(),
                  "Cognitive Complexity");


    /**
     * Sum of the statistical complexity of the operations in the class.
     * We use CYCLO to quantify the complexity of an operation.
     *
     */
    public static final Metric<ASTUserClassOrInterface<?>, Integer> WEIGHED_METHOD_COUNT =
        Metric.of(ApexMetrics::computeWmc, filterMapNode(ASTUserClass.class, PredicateUtil.always()),
                  "Weighed Method Count", "WMC");

    private ApexMetrics() {
        // utility class
    }



    private static Function<Node, ApexNode<?>> isRegularApexNode() {
        return filterMapNode(GENERIC_APEX_NODE_CLASS, n -> !(n instanceof ASTMethod && ((ASTMethod) n).isSynthetic()));
    }


    private static <T extends Node> Function<Node, T> filterMapNode(Class<? extends T> klass, Predicate<? super T> pred) {
        return n -> n.asStream().filterIs(klass).filter(pred).first();
    }

    private static int computeCyclo(ApexNode<?> node, MetricOptions ignored) {
        MutableInt result = new MutableInt(1);
        node.acceptVisitor(new StandardCycloVisitor(), result);
        return result.getValue();
    }

    private static int computeCognitiveComp(ApexNode<?> node, MetricOptions ignored) {
        State state = new State();
        node.acceptVisitor(CognitiveComplexityVisitor.INSTANCE, state);
        return state.getComplexity();
    }



    private static int computeWmc(ASTUserClassOrInterface<?> node, MetricOptions options) {
        return (int) MetricsUtil.computeStatistics(CYCLO, node.getMethods(), options).getSum();
    }


}
