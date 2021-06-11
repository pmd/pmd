/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * This rule finds concurrent calls to StringBuffer/Builder.append where String
 * literals are used It would be much better to make these calls using one call
 * to <code>.append</code>
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

    private int threshold = 1;

    public ConsecutiveLiteralAppendsRule() {
        super(ASTVariableDeclaratorId.class);
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (!isStringBuilderOrBuffer(node)) {
            return data;
        }
        threshold = getProperty(THRESHOLD_DESCRIPTOR);

        int concurrentCount = checkConstructor(node);
        concurrentCount += checkInitializerExpressions(node.getInitializer());
        Node lastBlock = getFirstParentBlock(node);
        Node currentBlock = lastBlock;
        Node rootNode = null;
        // only want the constructor flagged if it's really containing strings
        if (concurrentCount >= 1) {
            rootNode = node;
        }

        List<ASTNamedReferenceExpr> usages = node.getLocalUsages();

        for (ASTNamedReferenceExpr namedReference : usages) {
            Node current = namedReference;
            // loop through method call chain
            while (current.getParent() instanceof ASTMethodCall) {
                ASTMethodCall methodCall = (ASTMethodCall) current.getParent();
                current = methodCall;

                currentBlock = getFirstParentBlock(namedReference);

                if (JavaRuleUtil.isStringBuilderCtorOrAppend(methodCall)) {
                    // append method call detected

                    // see if it changed blocks
                    if (currentBlock != null && lastBlock != null && !currentBlock.equals(lastBlock)
                            || currentBlock == null ^ lastBlock == null) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }

                    // if concurrent is 0 then we reset the root to report from
                    // here
                    if (concurrentCount == 0) {
                        rootNode = methodCall;
                    }
                    if (isAdditive(methodCall)) {
                        concurrentCount = processAdditive(data, concurrentCount, methodCall, rootNode);
                        if (concurrentCount != 0) {
                            rootNode = methodCall;
                        }
                    } else if (isAppendingVariablesOrFields(methodCall) || isAppendingMethodResult(methodCall)) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    } else {
                        concurrentCount++;
                    }
                    lastBlock = currentBlock;
                } else if ("length".equals(methodCall.getMethodName())) {
                    // ignore length, they do not change affect the content of the sb
                    // note: while toString doesn't change the content, it depends on the content.
                    // changing/merging appends call would change the result of toString
                } else {
                    // other method calls the stringbuilder variable, e.g. calling delete
                    checkForViolation(rootNode, data, concurrentCount);
                    concurrentCount = 0;
                }
            }
            if (!(namedReference.getParent() instanceof ASTMethodCall)) {
                // usage of the stringbuilder variable for any other purpose, e.g. as a argument for
                // a different method call
                checkForViolation(rootNode, data, concurrentCount);
                concurrentCount = 0;
            }
        }
        checkForViolation(rootNode, data, concurrentCount);
        return data;
    }

    /**
     * Determine if the constructor contains (or ends with) a String Literal
     *
     * @param node
     * @return 1 if the constructor contains string argument, else 0
     */
    private int checkConstructor(ASTVariableDeclaratorId node) {
        ASTExpression initializer = node.getInitializer();
        ASTConstructorCall constructorCall = null;
        if (initializer != null) {
            constructorCall = initializer.descendantsOrSelf().filterIs(ASTConstructorCall.class).first();
        }
        
        if (constructorCall != null) {
            ASTArgumentList list = constructorCall.getArguments();
            
            @Nullable
            ASTExpression firstArgument = list.children().first();
            if (firstArgument instanceof ASTStringLiteral) {
                return 1;
            } else if (JavaRuleUtil.isStringConcatExpr(firstArgument)) {
                
                if (firstArgument instanceof ASTInfixExpression) {
                    if (((ASTInfixExpression) firstArgument).getRightOperand() instanceof ASTStringLiteral) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Determine if during the variable initializer calls to ".append" are done (call chains).
     *
     * @param node
     * @return
     */
    private int checkInitializerExpressions(ASTExpression initializer) {
        if (initializer == null) {
            return 0;
        }
        
        if (initializer instanceof ASTConstructorCall) {
            return 0;
        }
        
        if (initializer instanceof ASTMethodCall && JavaRuleUtil.isStringBuilderCtorOrAppend(initializer)) {
            int count = 1;
            @Nullable
            JavaNode child = initializer.getFirstChild();
            while (child instanceof ASTMethodCall) {
                ASTMethodCall call = (ASTMethodCall) child;
                if (call.getArguments().descendants(ASTNamedReferenceExpr.class).count() > 0) {
                    // at least one variable/field access found - can't combine appends
                    count = 0;
                    break;
                }
                if (JavaRuleUtil.isStringBuilderCtorOrAppend(call)) {
                    count++;
                }
                child = child.getFirstChild();
            }
            return count;
        }

        return 0;
    }

    private int processAdditive(Object data, int concurrentCount, ASTMethodCall methodCall, Node rootNode) {
        ASTExpression firstArg = methodCall.getArguments().children().first();
        if (firstArg == null) {
            // no arg found, this doesn't count
            return 0;
        }
        
        if (firstArg.descendants(ASTNamedReferenceExpr.class).count() > 0) {
            // at least one variable/field access found
            checkForViolation(rootNode, data, concurrentCount);
            // continue with a fresh round
            return 0;
        }
        
        // no variables appended, compiler will take care of merging all the
        // string concats, we really only have 1 then
        return 1;
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
    private boolean isAdditive(ASTMethodCall n) {
        ASTExpression firstArg = n.getArguments().children().first();
        return JavaRuleUtil.isStringConcatExpr(firstArg);
    }

    /**
     * Get the first parent. Keep track of the last node though. For If
     * statements it's the only way we can differentiate between if's and else's
     * For switches it's the only way we can differentiate between switches
     *
     * @param node
     *            The node to check
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
        } else if (parentNode instanceof ASTSwitchStatement) {
            parentNode = getSwitchParent(parentNode, lastNode);
        }
        return parentNode;
    }

    /**
     * Determine which SwitchLabel we belong to inside a switch
     *
     * @param parentNode
     *            The parent node we're looking at
     * @param lastNode
     *            The last node processed
     * @return The parent node for the switch statement
     */
    private Node getSwitchParent(Node parentNode, Node lastNode) {
        int allChildren = parentNode.getNumChildren();
        Node result = parentNode;
        ASTSwitchLabel label = null;
        for (int ix = 0; ix < allChildren; ix++) {
            Node n = result.getChild(ix);
            if (n instanceof ASTSwitchLabel) {
                label = (ASTSwitchLabel) n;
            } else if (n.equals(lastNode)) {
                result = label;
                break;
            }
        }
        return result;
    }

    /**
     * Helper method checks to see if a violation occurred, and adds a
     * RuleViolation if it did
     */
    private void checkForViolation(Node node, Object data, int concurrentCount) {
        if (concurrentCount > threshold) {
            String[] param = { String.valueOf(concurrentCount) };
            addViolation(data, node, param);
        }
    }

    private boolean isAppendingVariablesOrFields(ASTMethodCall node) {
        return node.getArguments().descendants(ASTNamedReferenceExpr.class).count() > 0;
    }

    private boolean isAppendingMethodResult(ASTMethodCall node) {
        return node.getArguments().getFirstChild() instanceof ASTMethodCall;
    }

    static boolean isStringBuilderOrBuffer(TypeNode node) {
        return TypeTestUtil.isA(StringBuffer.class, node)
            || TypeTestUtil.isA(StringBuilder.class, node);
    }
}
