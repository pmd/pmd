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
 *
 */
public final class ApexMetrics {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Class<ApexNode<?>> GENERIC_APEX_NODE_CLASS =
        (Class) ApexNode.class; // this is a Class<ApexNode>, the raw type


    public static final Metric<ApexNode<?>, Integer> CYCLO =
        Metric.of(ApexMetrics::computeCyclo, isRegularApexNode(),
                  "Cyclomatic Complexity", "Cyclo");

    public static final Metric<ApexNode<?>, Integer> COGNITIVE_COMPLEXITY =
        Metric.of(ApexMetrics::computeCognitiveComp, isRegularApexNode(),
                  "Cognitive Complexity");


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

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static int computeCyclo(ApexNode<?> node, MetricOptions ignored) {
        MutableInt result = new MutableInt(1);
        node.acceptVisitor(new StandardCycloVisitor(), result);
        return result.getValue();
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static int computeCognitiveComp(ApexNode<?> node, MetricOptions ignored) {
        State state = new State();
        node.acceptVisitor(CognitiveComplexityVisitor.INSTANCE, state);
        return state.getComplexity();
    }



    private static int computeWmc(ASTUserClassOrInterface<?> node, MetricOptions options) {
        return (int) MetricsUtil.computeStatistics(CYCLO, node.getMethods(), options).getSum();
    }


}
