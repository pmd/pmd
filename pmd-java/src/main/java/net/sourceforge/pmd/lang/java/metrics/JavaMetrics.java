/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static net.sourceforge.pmd.internal.util.PredicateUtil.always;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdBaseVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.ClassFanOutVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.CognitiveComplexityVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.CognitiveComplexityVisitor.State;
import net.sourceforge.pmd.lang.java.metrics.internal.CycloVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssVisitor;
import net.sourceforge.pmd.lang.java.metrics.internal.NpathBaseVisitor;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * Built-in Java metrics. See {@link Metric} and {@link MetricsUtil}
 * for usage doc.
 *
 * @see "Michele Lanza and Radu Marinescu. <i>Object-Oriented Metrics in Practice:
 * Using Software Metrics to Characterize, Evaluate, and Improve the Design
 * of Object-Oriented Systems.</i> Springer, Berlin, 1 edition, October 2006."
 */
public final class JavaMetrics {

    /**
     * Number of usages of foreign attributes, both directly and through accessors.
     * "Foreign" hier means "not belonging to {@code this}", although field accesses
     * to fields declared in the enclosing class are not considered foreign.
     *
     * <p>High values of ATFD (&gt; 3 for an operation) may suggest that the
     * class or operation breaks encapsulation by relying on the internal
     * representation of the classes it uses instead of the services they provide.
     */
    public static final Metric<JavaNode, Integer> ACCESS_TO_FOREIGN_DATA =
        Metric.of(JavaMetrics::computeAtfd, isJavaNode(),
                  "Access To Foreign Data", "ATFD");


    /**
     * Number of independent paths through a block of code.
     * Formally, given that the control flow graph of the block has n
     * vertices, e edges and p connected components, the cyclomatic complexity
     * of the block is given by {@code CYCLO = e - n + 2p}. In practice
     * it can be calculated by counting control flow statements following
     * the standard rules given below.
     *
     * <p>The standard version of the metric complies with McCabe’s original definition:
     * <ul>
     *  <li>Methods have a base complexity of 1.
     *  <li>+1 for every control flow statement (if, case, catch, throw,
     *  do, while, for, break, continue) and conditional expression (?:).
     *  Notice switch cases count as one, but not the switch itself: the
     *  point is that a switch should have the same complexity value as
     *  the equivalent series of if statements.
     *  <li>else, finally and default do not count;
     *  <li>+1 for every boolean operator ({@code &&}, {@code ||}) in
     *  the guard condition of a control flow statement. That’s because
     *  Java has short-circuit evaluation semantics for boolean operators,
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
     *
     * @see CycloOption
     */
    public static final Metric<ASTMethodOrConstructorDeclaration, Integer> CYCLO =
        Metric.of(JavaMetrics::computeCyclo, asMethodOrCtor(),
                  "Cyclomatic Complexity", "Cyclo");

    /**
     * Cognitive complexity is a measure of how difficult it is for humans
     * to read and understand a method. Code that contains a break in the
     * control flow is more complex, whereas the use of language shorthands
     * doesn't increase the level of complexity. Nested control flows can
     * make a method more difficult to understand, with each additional
     * nesting of the control flow leading to an increase in cognitive
     * complexity.
     *
     * <p>Information about Cognitive complexity can be found in the original paper here:
     * <a href='https://www.sonarsource.com/docs/CognitiveComplexity.pdf'>CognitiveComplexity</a>
     *
     * <h3>Basic Idea</h3
     * <ol>
     * <li>Ignore structures that allow multiple statements to be readably shorthanded into one
     * <li>Increment (add one) for each break in the linear flow of the code
     * <li>Increment when flow-breaking structures are nested
     * </ol>
     *
     * <h3>Increments</h3>
     * There is an increment for each of the following:
     * <ul>
     * <li>`if`, `else if`, `else`, ternary operator
     * <li>`switch`
     * <li>`for`, `foreach`
     * <li>`while`, `do while`
     * <li>`catch`
     * <li>`goto LABEL`, `break LABEL`, `continue LABEL`
     * <li>sequences of binary logical operators
     * <li>each method in a recursion cycle
     * </ul>
     *
     * <h3>Nesting level</h3>
     * The following structures increment the nesting level:
     * <ul>
     * <li>`if`, `else if`, `else`, ternary operator
     * <li>`switch`
     * <li>`for`, `foreach`
     * <li>`while`, `do while`
     * <li>`catch`
     * <li>nested methods and method-like structures such as lambdas
     * </ul>
     *
     * <h3>Nesting increments</h3>
     * The following structures receive a nesting increment commensurate with their nested depth
     * inside nested structures:
     * <ul>
     * <li>`if`, ternary operator
     * <li>`switch`
     * <li>`for`, `foreach`
     * <li>`while`, `do while`
     * <li>`catch`
     * </ul>
     *
     * <h3>Code example</h3>
     *
     * <pre>{@code
     * class Foo {
     *   void myMethod () {
     *     try {
     *       if (condition1) { // +1
     *         for (int i = 0; i < 10; i++) { // +2 (nesting=1)
     *           while (condition2) { } // +3 (nesting=2)
     *         }
     *       }
     *     } catch (ExcepType1 | ExcepType2 e) { // +1
     *       if (condition2) { } // +2 (nesting=1)
     *     }
     *   } // Cognitive Complexity 9
     * }
     * }</pre>
     */
    public static final Metric<ASTMethodOrConstructorDeclaration, Integer> COGNITIVE_COMPLEXITY =
        Metric.of(JavaMetrics::computeCognitive, asMethodOrCtor(),
                  "Cognitive Complexity", "CognitiveComp");

    /**
     * This counts the number of other classes a given class or operation
     * relies on. Classes from the package {@code java.lang} are ignored
     * by default (can be changed via options). Also primitives are not
     * included into the count.
     *
     * <p>Code example:
     * <pre>{@code
     * import java.util.*;
     * import java.io.IOException;
     *
     * public class Foo { // total 8
     *     public Set set = new HashSet(); // +2
     *     public Map map = new HashMap(); // +2
     *     public String string = ""; // from java.lang -> does not count by default
     *     public Double number = 0.0; // from java.lang -> does not count by default
     *     public int[] intArray = new int[3]; // primitive -> does not count
     *
     *     \@Deprecated // from java.lang -> does not count by default
     *     public void foo(List list) throws Exception { // +1 (Exception is from java.lang)
     *         throw new IOException(); // +1
     *     }
     *
     *     public int getMapSize() {
     *         return map.size(); // +1 because it uses the Class from the 'map' field
     *     }
     * }
     * }</pre>
     *
     * @see ClassFanOutOption
     */
    public static final Metric<JavaNode, Integer> FAN_OUT =
        Metric.of(JavaMetrics::computeFanOut, isJavaNode(),
                  "Fan-Out", "CFO");

    /**
     * Simply counts the number of lines of code the operation or class
     * takes up in the source. This metric doesn’t discount comments or blank lines.
     * See {@link #NCSS} for a less biased metric.
     */
    public static final Metric<JavaNode, Integer> LINES_OF_CODE =
        Metric.of(JavaMetrics::computeLoc, isJavaNode(),
                  "Lines of code", "LOC");

    /**
     * Number of statements in a class or operation. That’s roughly
     * equivalent to counting the number of semicolons and opening braces
     * in the program. Comments and blank lines are ignored, and statements
     * spread on multiple lines count as only one (e.g. {@code int\n a;}
     * counts a single statement).
     *
     * <p>The standard version of the metric is based off [JavaNCSS](http://www.kclee.de/clemens/java/javancss/):
     * <ul>
     * <li>+1 for any of the following statements: {@code if}, {@code else},
     * {@code while}, {@code do}, {@code for}, {@code switch}, {@code break},
     * {@code continue}, {@code return}, {@code throw}, {@code synchronized},
     * {@code catch}, {@code finally}.
     * <li>+1 for each assignment, variable declaration (except for loop initializers)
     * or statement expression. We count variables declared on the same line (e.g.
     * {@code int a, b, c;}) as a single statement.
     * <li>Contrary to Sonarqube, but as JavaNCSS, we count type declarations (class, interface, enum, annotation),
     * and method and field declarations.
     * <li>Contrary to JavaNCSS, but as Sonarqube, we do not count package
     * declaration and import declarations as statements. This makes it
     * easier to compare nested classes to outer classes. Besides, it makes
     * for class metric results that actually represent the size of the class
     * and not of the file. If you don’t like that behaviour, use the {@link NcssOption#COUNT_IMPORTS} option.
     * </ul>
     *
     * <pre>{@code
     * import java.util.Collections;       // +0
     * import java.io.IOException;         // +0
     *
     * class Foo {                         // +1, total Ncss = 12
     *
     *   public void bigMethod()           // +1
     *       throws IOException {
     *     int x = 0, y = 2;               // +1
     *     boolean a = false, b = true;    // +1
     *
     *     if (a || b) {                   // +1
     *       try {                         // +1
     *         do {                        // +1
     *           x += 2;                   // +1
     *         } while (x < 12);
     *
     *         System.exit(0);             // +1
     *       } catch (IOException ioe) {   // +1
     *         throw new PatheticFailException(ioe); // +1
     *       }
     *     } else {
     *       assert false;                 // +1
     *     }
     *   }
     * }
     * }</pre>
     */
    public static final Metric<JavaNode, Integer> NCSS =
        Metric.of(JavaMetrics::computeNcss, isJavaNode(),
                  "Non-commenting source statements", "NCSS");

    /**
     * Number of acyclic execution paths through a piece of code. This
     * is related to cyclomatic complexity, but the two metrics don’t
     * count the same thing: NPath counts the number of distinct full
     * paths from the beginning to the end of the method, while Cyclo
     * only counts the number of decision points. NPath is not computed
     * as simply as {@link #CYCLO}. With NPath, two decision points appearing
     * sequentially have their complexity multiplied.
     *
     * <p>The fact that NPath multiplies the complexity of statements makes
     * it grow exponentially: 10 {@code if .. else} statements in a row
     * would give an NPath of 1024, while Cyclo would evaluate to 20.
     * Methods with an NPath complexity over 200 are generally considered
     * too complex.
     *
     * <p>We compute NPath recursively, with the following set of rules:
     * <ul>
     * <li>An empty block has a complexity of 1.
     * <li>The complexity of a block is the product of the NPath complexity
     * of its statements, calculated as follows:
     * <ul>
     * <li>The complexity of for, do and while statements is 1, plus the
     * complexity of the block, plus the complexity of the guard condition.
     * <li> The complexity of a cascading if statement ({@code if .. else if ..})
     * is the number of if statements in the chain, plus the complexity of
     * their guard condition, plus the complexity of the unguarded else block
     * (or 1 if there is none).
     * <li> The complexity of a switch statement is the number of cases,
     * plus the complexity of each case block. It’s equivalent to the
     * complexity of the equivalent cascade of if statements.
     * <li> The complexity of a ternary expression ({@code ? :}) is the complexity
     * of the guard condition, plus the complexity of both expressions.
     * It’s equivalent to the complexity of the equivalent {@code if .. else} construct.
     * <li> The complexity of a {@code try .. catch} statement is the complexity
     * of the {@code try} block, plus the complexity of each catch block.
     * <li> The complexity of a return statement is the complexity of the
     * expression (or 1 if there is none).
     * <li> All other statements have a complexity of 1 and are discarded
     * from the product.
     * </ul>
     * </li>
     * </ul>
     */
    public static final Metric<ASTMethodOrConstructorDeclaration, BigInteger> NPATH =
        Metric.of(JavaMetrics::computeNpath, asMethodOrCtor(),
                  "NPath Complexity", "NPath");

    public static final Metric<ASTAnyTypeDeclaration, Integer> NUMBER_OF_ACCESSORS =
        Metric.of(JavaMetrics::computeNoam, asClass(always()),
                  "Number of accessor methods", "NOAM");

    public static final Metric<ASTAnyTypeDeclaration, Integer> NUMBER_OF_PUBLIC_FIELDS =
        Metric.of(JavaMetrics::computeNopa, asClass(always()),
                  "Number of public attributes", "NOPA");

    /**
     * The relative number of method pairs of a class that access in common
     * at least one attribute of the measured class. TCC only counts direct
     * attribute accesses, that is, only those attributes that are accessed
     * in the body of the method.
     *
     * <p>The value is a double between 0 and 1.
     *
     * <p>TCC is taken to be a reliable cohesion metric for a class.
     * High values (&gt;70%) indicate a class with one basic function,
     * which is hard to break into subcomponents. On the other hand, low
     * values (&lt;50%) may indicate that the class tries to do too much
     * and defines several unrelated services, which is undesirable.
     */
    public static final Metric<ASTAnyTypeDeclaration, Double> TIGHT_CLASS_COHESION =
        Metric.of(JavaMetrics::computeTcc, asClass(it -> !it.isInterface()),
                  "Tight Class Cohesion", "TCC");

    /**
     * Sum of the statistical complexity of the operations in the class.
     * We use CYCLO to quantify the complexity of an operation.
     *
     * <p>WMC uses the same options as CYCLO, which are provided to CYCLO when computing it ({@link CycloOption}).
     */
    public static final Metric<ASTAnyTypeDeclaration, Integer> WEIGHED_METHOD_COUNT =
        Metric.of(JavaMetrics::computeWmc, asClass(it -> !it.isInterface()),
                  "Weighed Method Count", "WMC");


    /**
     * Number of “functional” public methods divided by the total number
     * of public methods. Our definition of “functional method” excludes
     * constructors, getters, and setters.
     *
     * <p>The value is a double between 0 and 1.
     *
     * <p>This metric tries to quantify whether the measured class’ interface
     * reveals more data than behaviour. Low values (less than 30%) indicate
     * that the class reveals much more data than behaviour, which is a
     * sign of poor encapsulation.
     */
    public static final Metric<ASTAnyTypeDeclaration, Double> WEIGHT_OF_CLASS =
        Metric.of(JavaMetrics::computeWoc, asClass(it -> !it.isInterface()),
                  "Weight Of Class", "WOC");

    private JavaMetrics() {
        // utility class
    }


    private static Function<Node, JavaNode> isJavaNode() {
        return n -> n instanceof JavaNode ? (JavaNode) n : null;
    }

    private static Function<Node, @Nullable ASTMethodOrConstructorDeclaration> asMethodOrCtor() {
        return n -> n instanceof ASTMethodOrConstructorDeclaration ? (ASTMethodOrConstructorDeclaration) n : null;
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
                   .filter(JavaRuleUtil::isGetterOrSetter)
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
        MutableInt result = new MutableInt(0);
        node.acceptVisitor(new NcssVisitor(options, node), result);
        return result.getValue();
    }

    private static int computeLoc(JavaNode node, MetricOptions ignored) {
        return 1 + node.getEndLine() - node.getBeginLine();
    }


    private static int computeCyclo(JavaNode node, MetricOptions options) {
        MutableInt counter = new MutableInt(0);
        node.acceptVisitor(new CycloVisitor(options, node), counter);
        return counter.getValue();
    }

    private static BigInteger computeNpath(JavaNode node, MetricOptions ignored) {
        return node.acceptVisitor(NpathBaseVisitor.INSTANCE, null);
    }

    private static int computeCognitive(JavaNode node, MetricOptions ignored) {
        State state = new State();
        node.acceptVisitor(CognitiveComplexityVisitor.INSTANCE, state);
        return state.getComplexity();
    }

    private static int computeWmc(ASTAnyTypeDeclaration node, MetricOptions options) {
        return (int) MetricsUtil.computeStatistics(CYCLO, node.getOperations(), options).getSum();
    }


    private static double computeTcc(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        List<Set<String>> usagesByMethod = attributeAccessesByMethod(node);

        int numPairs = numMethodsRelatedByAttributeAccess(usagesByMethod);
        int maxPairs = maxMethodPairs(usagesByMethod.size());

        if (maxPairs == 0) {
            return 0;
        }

        return numPairs / (double) maxPairs;
    }


    /**
     * Collects the attribute accesses by method into a map, for TCC.
     */
    private static List<Set<String>> attributeAccessesByMethod(ASTAnyTypeDeclaration type) {
        final List<Set<String>> map = new ArrayList<>();
        final JClassSymbol typeSym = type.getSymbol();
        for (ASTMethodDeclaration decl : type.getDeclarations(ASTMethodDeclaration.class)) {
            Set<String> attrs = new HashSet<>();
            decl.descendants().crossFindBoundaries()
                .filterIs(ASTNamedReferenceExpr.class)
                .forEach(it -> {
                    JVariableSymbol sym = it.getReferencedSym();
                    if (sym instanceof JFieldSymbol && typeSym.equals(((JFieldSymbol) sym).getEnclosingClass())) {
                        attrs.add(sym.getSimpleName());
                    }
                });

            map.add(attrs);

        }
        return map;
    }


    /**
     * Gets the number of pairs of methods that use at least one attribute in common.
     *
     * @param usagesByMethod Map of method name to names of local attributes accessed
     *
     * @return The number of pairs
     */
    private static int numMethodsRelatedByAttributeAccess(List<Set<String>> usagesByMethod) {
        int methodCount = usagesByMethod.size();
        int pairs = 0;

        if (methodCount > 1) {
            for (int i = 0; i < methodCount - 1; i++) {
                for (int j = i + 1; j < methodCount; j++) {
                    if (!Collections.disjoint(usagesByMethod.get(i),
                                              usagesByMethod.get(j))) {
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


    /**
     * Options for {@link #NCSS}.
     */
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
     * Options for {@link #CYCLO}.
     */
    public enum CycloOption implements MetricOption {
        /** Do not count the paths in boolean expressions as decision points. */
        IGNORE_BOOLEAN_PATHS("ignoreBooleanPaths"),
        /** Consider assert statements as if they were {@code if (..) throw new AssertionError(..)}. */
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

    private static int computeAtfd(JavaNode node, MetricOptions ignored) {
        MutableInt result = new MutableInt(0);
        node.acceptVisitor(new AtfdBaseVisitor(), result);
        return result.getValue();
    }


    private static double computeWoc(ASTAnyTypeDeclaration node, MetricOptions ignored) {
        NodeStream<ASTMethodDeclaration> methods =
            node.getDeclarations()
                .filterIs(ASTMethodDeclaration.class)
                .filter(it -> !it.isPrivate());

        int notSetter = methods.filter(it -> !JavaRuleUtil.isGetterOrSetter(it)).count();
        int total = methods.count();
        if (total == 0) {
            return 0;
        }
        return notSetter / (double) total;
    }


    private static int computeFanOut(JavaNode node, MetricOptions options) {
        Set<JClassSymbol> cfo = new HashSet<>();
        node.acceptVisitor(ClassFanOutVisitor.getInstance(options), cfo);
        return cfo.size();
    }


    /**
     * Options for {@link #FAN_OUT}.
     */
    public enum ClassFanOutOption implements MetricOption {
        /** Whether to include classes in the {@code java.lang} package. */
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
