/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * This rule finds concurrent calls to StringBuffer/Builder.append where String
 * literals are used. It would be much better to make these calls using one call
 * to <code>.append</code>.
 *
 * <p>Example:</p>
 *
 * <pre>
 * StringBuilder buf = new StringBuilder();
 * buf.append(&quot;Hello&quot;);
 * buf.append(&quot; &quot;).append(&quot;World&quot;);
 * </pre>
 *
 * <p>This would be more eloquently put as:</p>
 *
 * <pre>
 * StringBuilder buf = new StringBuilder();
 * buf.append(&quot;Hello World&quot;);
 * </pre>
 *
 * <p>The rule takes one parameter, threshold, which defines the lower limit of
 * consecutive appends before a violation is created. The default is 1.</p>
 */
public class ConsecutiveLiteralAppendsRule extends AbstractJavaRulechainRule {

    private static final Set<Class<?>> BLOCK_PARENTS;

    static {
        BLOCK_PARENTS = new HashSet<>();
        BLOCK_PARENTS.add(ASTForStatement.class);
        BLOCK_PARENTS.add(ASTForeachStatement.class);
        BLOCK_PARENTS.add(ASTWhileStatement.class);
        BLOCK_PARENTS.add(ASTDoStatement.class);
        BLOCK_PARENTS.add(ASTIfStatement.class);
        BLOCK_PARENTS.add(ASTSwitchStatement.class);
        BLOCK_PARENTS.add(ASTMethodDeclaration.class);
        BLOCK_PARENTS.add(ASTCatchClause.class);
        BLOCK_PARENTS.add(ASTFinallyClause.class);
        BLOCK_PARENTS.add(ASTLambdaExpression.class);
        BLOCK_PARENTS.add(ASTSwitchArrowBranch.class);
        BLOCK_PARENTS.add(ASTSwitchFallthroughBranch.class);
    }

    private static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR
            = PropertyFactory.intProperty("threshold")
                             .desc("Max consecutive appends")
                             .require(inRange(1, 10)).defaultValue(1).build();

    private ConsecutiveCounter counter = new ConsecutiveCounter();

    public ConsecutiveLiteralAppendsRule() {
        super(ASTVariableDeclaratorId.class);
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!isStringBuilderOrBuffer(node)) {
            return data;
        }

        counter.initThreshold(getProperty(THRESHOLD_DESCRIPTOR));
        counter.reset();
        checkConstructor(data, node);

        Node lastBlock = getFirstParentBlock(node);
        Node currentBlock = lastBlock;

        for (ASTNamedReferenceExpr namedReference : node.getLocalUsages()) {
            currentBlock = getFirstParentBlock(namedReference);

            // loop through method call chain
            Node current = namedReference;
            while (current.getParent() instanceof ASTMethodCall) {
                ASTMethodCall methodCall = (ASTMethodCall) current.getParent();
                current = methodCall;


                if (JavaRuleUtil.isStringBuilderCtorOrAppend(methodCall)) {
                    // append method call detected

                    // see if it changed blocks
                    if (currentBlock != null && lastBlock != null && !currentBlock.equals(lastBlock)
                            || currentBlock == null ^ lastBlock == null) {
                        checkForViolation(data);
                        counter.reset();
                    }

                    analyzeInvocation(data, methodCall);
                    lastBlock = currentBlock;
                } else {
                    // other method calls the stringbuilder variable, e.g. calling delete, toString, etc.
                    checkForViolation(data);
                    counter.reset();
                }
            }
            if (!(namedReference.getParent() instanceof ASTMethodCall)) {
                // usage of the stringbuilder variable for any other purpose, e.g. as a argument for
                // a different method call
                checkForViolation(data);
                counter.reset();
            }
        }
        checkForViolation(data);
        return data;
    }

    /**
     * Determine if the constructor contains (or ends with) a String Literal.
     * Also analyzes a possible method call chain for append calls.
     *
     * @param node
     */
    private void checkConstructor(Object data, ASTVariableDeclaratorId node) {
        ASTExpression initializer = node.getInitializer();
        if (initializer == null) {
            return;
        }

        ASTConstructorCall constructorCall = initializer.descendantsOrSelf().filterIs(ASTConstructorCall.class).first();
        if (constructorCall == null) {
            return;
        }

        analyzeInvocation(data, constructorCall);

        // analyze chained calls to append
        Node parent = constructorCall.getParent();
        while (parent instanceof ASTMethodCall) {
            analyzeInvocation(data, (ASTMethodCall) parent);
            parent = parent.getParent();
        }
    }

    private void analyzeInvocation(Object data, InvocationNode invocation) {
        if (!(invocation instanceof ASTExpression)) {
            return;
        }
        if (!JavaRuleUtil.isStringBuilderCtorOrAppend((ASTExpression) invocation)) {
            return;
        }

        if (isAdditive(invocation)) {
            processAdditive(data, invocation);
        } else if (isAppendingVariablesOrFields(invocation) || isAppendingInvocationResult(invocation)) {
            checkForViolation(data);
            counter.reset();
        } else if (invocation.getArguments().getFirstChild() instanceof ASTStringLiteral
                || invocation instanceof ASTMethodCall) {
            counter.count(invocation);
        }
    }

    private void processAdditive(Object data, InvocationNode invocation) {
        ASTExpression firstArg = invocation.getArguments().getFirstChild();
        if (firstArg.descendants(ASTNamedReferenceExpr.class).count() > 0) {
            // at least one variable/field access found
            checkForViolation(data);

            if (firstArg instanceof ASTInfixExpression) {
                if (((ASTInfixExpression) firstArg).getRightOperand() instanceof ASTStringLiteral) {
                    // argument ends with ... + "some string"
                    counter.count(invocation);
                }
            } else {
                // continue with a fresh round
                counter.reset();
            }
        } else {
            // no variables appended, compiler will take care of merging all the
            // string concats, we really only have 1 then
            counter.count(invocation);
        }
    }

    /**
     * Checks to see if there is string concatenation in the node.
     *
     * This method checks if it's additive with respect to the append method
     * only.
     *
     * @param n
     *            Node to check
     * @return true if the node has an additive expression (i.e. "Hello " +
     *         Const.WORLD)
     */
    private boolean isAdditive(InvocationNode n) {
        return JavaRuleUtil.isStringConcatExpr(n.getArguments().getFirstChild());
    }

    /**
     * Get the first parent. Keep track of the last node though. For If
     * statements it's the only way we can differentiate between if's and else's
     *
     * @param node The node to check
     * @return The first parent block
     */
    private Node getFirstParentBlock(Node node) {
        Node parentNode = node.getParent();

        Node lastNode = node;
        while (parentNode != null && !BLOCK_PARENTS.contains(parentNode.getClass())) {
            lastNode = parentNode;
            parentNode = parentNode.getParent();
        }
        if (parentNode instanceof ASTIfStatement) {
            parentNode = lastNode;
        }
        return parentNode;
    }

    /**
     * Helper method checks to see if a violation occurred, and adds a
     * RuleViolation if it did
     */
    private void checkForViolation(Object data) {
        if (counter.isViolation()) {
            assert counter.getReportNode() != null;
            String[] param = { String.valueOf(counter.getCounter()) };
            addViolation(data, counter.getReportNode(), param);
        }
    }

    private boolean isAppendingVariablesOrFields(InvocationNode node) {
        return node.getArguments().descendants(ASTNamedReferenceExpr.class).count() > 0;
    }

    private boolean isAppendingInvocationResult(InvocationNode node) {
        return node.getArguments().getFirstChild() instanceof ASTMethodCall
                || node.getArguments().getFirstChild() instanceof ASTConstructorCall;
    }

    static boolean isStringBuilderOrBuffer(TypeNode node) {
        return TypeTestUtil.isA(StringBuffer.class, node)
            || TypeTestUtil.isA(StringBuilder.class, node);
    }

    private static class ConsecutiveCounter {
        private int threshold;
        private int counter;
        private Node reportNode;

        public void initThreshold(int threshold) {
            this.threshold = threshold;
        }

        public void count(Node node) {
            if (counter == 0) {
                reportNode = node;
            }
            counter++;
        }

        public void reset() {
            counter = 0;
            reportNode = null;
        }

        public boolean isViolation() {
            return counter > threshold;
        }

        public int getCounter() {
            return counter;
        }

        public Node getReportNode() {
            return reportNode;
        }

        @Override
        public String toString() {
            return "counter=" + counter + ",threshold=" + threshold + ",node=" + reportNode;
        }
    }
}
