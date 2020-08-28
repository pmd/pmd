/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import static net.sourceforge.pmd.internal.util.PredicateUtil.always;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.AtfdBaseVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.ClassFanOutVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.CycloVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.NcssVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.NpathBaseVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.TccAttributeAccessCollector;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaAstUtils;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 *
 */
public final class JavaMetrics {


    public static final Metric<JavaNode, Integer> LINES_OF_CODE =
        Metric.of(JavaMetrics::computeLoc, isJavaNode(),
                  "Lines of code", "LOC");

    public static final Metric<JavaNode, Integer> NCSS =
        Metric.of(JavaMetrics::computeNcss, isJavaNode(),
                  "Non-commenting source statements", "NCSS");

    public static final Metric<ASTAnyTypeDeclaration, Integer> NUMBER_OF_ACCESSORS =
        Metric.of(JavaMetrics::computeNoam, asClass(always()),
                  "Number of accessor methods", "NOAM");

    public static final Metric<ASTAnyTypeDeclaration, Integer> NUMBER_OF_PUBLIC_FIELDS =
        Metric.of(JavaMetrics::computeNopa, asClass(always()),
                  "Number of public attributes", "NOPA");

    public static final Metric<ASTAnyTypeDeclaration, Double> TIGHT_CLASS_COHESION =
        Metric.of(JavaMetrics::computeTcc, asClass(it -> !it.isInterface()),
                  "Tight Class Cohesion", "TCC");

    public static final Metric<JavaNode, Integer> CYCLO =
        Metric.of(JavaMetrics::computeCyclo, isJavaNode(),
                  "Cyclomatic Complexity", "Cyclo");

    public static final Metric<JavaNode, Integer> NPATH =
        Metric.of(JavaMetrics::computeNpath, isJavaNode(),
                  "NPath Complexity", "NPath");

    public static final Metric<ASTAnyTypeDeclaration, Integer> WEIGHED_METHOD_COUNT =
        Metric.of(JavaMetrics::computeWmc, asClass(it -> !it.isInterface()),
                  "Weighed Method Count", "WMC");

    public static final Metric<JavaNode, Integer> ACCESS_TO_FOREIGN_DATA =
        Metric.of(JavaMetrics::computeAtfd, isJavaNode(),
                  "Access To Foreign Data", "ATFD");


    public static final Metric<ASTAnyTypeDeclaration, Double> WEIGHT_OF_CLASS =
        Metric.of(JavaMetrics::computeWoc, asClass(it -> !it.isInterface()),
                  "Weight Of Class", "WOC");


    public static final Metric<JavaNode, Integer> FAN_OUT =
        Metric.of(JavaMetrics::computeFanOut, isJavaNode(),
                  "Fan-Out", "CFO");


    private JavaMetrics() {
        // utility class
    }


    private static Function<Node, JavaNode> isJavaNode() {
        return filterMapNode(JavaNode.class, always());
    }

    private static <T extends Node> Function<Node, T> filterMapNode(Class<? extends T> klass, Predicate<? super T> pred) {
        return n -> n.asStream().filterIs(klass).filter(pred).first();
    }


    private static Function<Node, ASTAnyTypeDeclaration> asClass(Predicate<? super ASTAnyTypeDeclaration> pred) {
        return filterMapNode(ASTAnyTypeDeclaration.class, pred);
    }


    private static int computeNoam(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        return node.getDeclarations()
                   .filterIs(ASTMethodDeclaration.class)
                   .filter(JavaAstUtils::isGetterOrSetter)
                   .count();
    }

    private static int computeNopa(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        return node.getDeclarations()
                   .filterIs(ASTFieldDeclaration.class)
                   .filter(AccessNode::isPublic)
                   .flatMap(ASTFieldDeclaration::getVarIds)
                   .count();
    }

    private static int computeNcss(JavaNode node, MetricOptions options) {
        MutableInt ncss = (MutableInt) node.acceptVisitor(new NcssVisitor(options, node), new MutableInt(0));
        return ncss.getValue();
    }

    private static int computeLoc(JavaNode node, MetricOptions ignored) {
        return 1 + node.getEndLine() - node.getBeginLine();
    }


    private static int computeCyclo(JavaNode node, MetricOptions options) {
        MutableInt counter = new MutableInt(1);
        node.acceptVisitor(new CycloVisitor(options, node), counter);
        return counter.getValue();
    }

    private static int computeNpath(JavaNode node, MetricOptions ignored) {
        return (Integer) node.acceptVisitor(NpathBaseVisitor.INSTANCE, null);
    }

    private static int computeWmc(ASTAnyTypeDeclaration node, MetricOptions options) {
        return (int) MetricsUtil.computeStatistics(CYCLO, node.getOperations(), options).getSum();
    }


    private static double computeTcc(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        Map<String, Set<String>> usagesByMethod = new TccAttributeAccessCollector(node).start();

        int numPairs = numMethodsRelatedByAttributeAccess(usagesByMethod);
        int maxPairs = maxMethodPairs(usagesByMethod.size());

        return numPairs / (double) maxPairs;
    }


    /**
     * Gets the number of pairs of methods that use at least one attribute in common.
     *
     * @param usagesByMethod Map of method name to names of local attributes accessed
     *
     * @return The number of pairs
     */
    private static int numMethodsRelatedByAttributeAccess(Map<String, Set<String>> usagesByMethod) {
        List<String> methods = new ArrayList<>(usagesByMethod.keySet());
        int methodCount = methods.size();
        int pairs = 0;

        if (methodCount > 1) {
            for (int i = 0; i < methodCount - 1; i++) {
                for (int j = i + 1; j < methodCount; j++) {
                    String firstMethodName = methods.get(i);
                    String secondMethodName = methods.get(j);

                    if (!Collections.disjoint(usagesByMethod.get(firstMethodName),
                                              usagesByMethod.get(secondMethodName))) {
                        pairs++;
                    }
                }
            }
        }
        return pairs;
    }


    /**
     * Calculates the number of possible method pairs of two methods.
     *
     * @param methods Number of methods in the class
     *
     * @return Number of possible method pairs
     */
    private static int maxMethodPairs(int methods) {
        return methods * (methods - 1) / 2;
    }


    /** Variants of NCSS. */
    public enum NcssOption implements MetricOption {
        /** Counts import and package statement. This makes the metric JavaNCSS compliant. */
        COUNT_IMPORTS("countImports");

        private final String vName;


        NcssOption(String valueName) {
            this.vName = valueName;
        }


        @Override
        public String valueName() {
            return vName;
        }
    }

    /**
     * Evaluates the number of paths through a boolean expression. This is the total number of {@code &&} and {@code ||}
     * operators appearing in the expression. This is used in the calculation of cyclomatic and n-path complexity.
     *
     * @param expr Expression to analyse
     *
     * @return The number of paths through the expression
     */
    public static int booleanExpressionComplexity(Node expr) {
        if (expr == null) {
            return 0;
        }

        List<ASTConditionalAndExpression> andNodes = expr.findDescendantsOfType(ASTConditionalAndExpression.class);
        List<ASTConditionalOrExpression> orNodes = expr.findDescendantsOfType(ASTConditionalOrExpression.class);

        int complexity = 0;

        if (expr instanceof ASTConditionalOrExpression || expr instanceof ASTConditionalAndExpression) {
            complexity++;
        }

        for (ASTConditionalOrExpression element : orNodes) {
            complexity += element.getNumChildren() - 1;
        }

        for (ASTConditionalAndExpression element : andNodes) {
            complexity += element.getNumChildren() - 1;
        }

        return complexity;
    }


    /** Options for CYCLO. */
    public enum CycloOption implements MetricOption {
        /** Do not count the paths in boolean expressions as decision points. */
        IGNORE_BOOLEAN_PATHS("ignoreBooleanPaths"),
        /** Consider assert statements. */
        CONSIDER_ASSERT("considerAssert");

        private final String vName;


        CycloOption(String valueName) {
            this.vName = valueName;
        }


        @Override
        public String valueName() {
            return vName;
        }
    }

    private static int computeAtfd(JavaNode node, MetricOptions options) {
        return ((MutableInt) node.acceptVisitor(new AtfdBaseVisitor(), new MutableInt(0))).getValue();
    }


    private static double computeWoc(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        NodeStream<ASTMethodDeclaration> methods =
            node.getDeclarations()
                .filterIs(ASTMethodDeclaration.class)
                .filter(it -> !it.isPrivate());

        int notSetter = methods.filter(it -> !JavaAstUtils.isGetterOrSetter(it)).count();
        int total = methods.count();

        return notSetter / (double) total;
    }


    private static int computeFanOut(JavaNode node, MetricOptions options) {
        MutableInt cfo = (MutableInt) node.acceptVisitor(new ClassFanOutVisitor(options, node), new MutableInt(0));
        return cfo.getValue();
    }


    public enum ClassFanOutOption implements MetricOption {
        /** Whether to include Classes in the java.lang package. */
        INCLUDE_JAVA_LANG("includeJavaLang");

        private final String vName;

        ClassFanOutOption(String valueName) {
            this.vName = valueName;
        }

        @Override
        public String valueName() {
            return vName;
        }
    }
}
