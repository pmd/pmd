/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
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
public class ConsecutiveLiteralAppendsRule extends AbstractJavaRule {

    private static final Set<Class<?>> BLOCK_PARENTS;

    static {
        BLOCK_PARENTS = new HashSet<>();
        BLOCK_PARENTS.add(ASTForStatement.class);
        BLOCK_PARENTS.add(ASTWhileStatement.class);
        BLOCK_PARENTS.add(ASTDoStatement.class);
        BLOCK_PARENTS.add(ASTIfStatement.class);
        BLOCK_PARENTS.add(ASTSwitchStatement.class);
        BLOCK_PARENTS.add(ASTMethodDeclaration.class);
        BLOCK_PARENTS.add(ASTCatchStatement.class);
        BLOCK_PARENTS.add(ASTFinallyStatement.class);
    }

    private static final PropertyDescriptor<Integer> THRESHOLD_DESCRIPTOR
            = PropertyFactory.intProperty("threshold")
                             .desc("Max consecutive appends")
                             .require(inRange(1, 10)).defaultValue(1).build();

    private int threshold = 1;

    public ConsecutiveLiteralAppendsRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (!isStringBuilderOrBuffer(node)) {
            return data;
        }
        threshold = getProperty(THRESHOLD_DESCRIPTOR);

        int concurrentCount = checkConstructor(node, data);
        if (hasInitializer(node)) {
            concurrentCount += checkInitializerExpressions(node);
        }
        Node lastBlock = getFirstParentBlock(node);
        Node currentBlock = lastBlock;
        Node rootNode = null;
        // only want the constructor flagged if it's really containing strings
        if (concurrentCount >= 1) {
            rootNode = node;
        }

        List<NameOccurrence> usages = determineUsages(node);

        for (NameOccurrence no : usages) {
            JavaNameOccurrence jno = (JavaNameOccurrence) no;
            Node n = jno.getLocation();

            currentBlock = getFirstParentBlock(n);

            if (InefficientStringBufferingRule.isInStringBufferOperation(n, 3, "append")) {
                // append method call detected
                ASTPrimaryExpression s = n.getFirstParentOfType(ASTPrimaryExpression.class);
                int numChildren = s.getNumChildren();
                for (int jx = 0; jx < numChildren; jx++) {
                    Node sn = s.getChild(jx);
                    if (!(sn instanceof ASTPrimarySuffix) || sn.getImage() != null) {
                        continue;
                    }

                    // see if it changed blocks
                    if (currentBlock != null && lastBlock != null && !currentBlock.equals(lastBlock)
                            || currentBlock == null ^ lastBlock == null) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }

                    // if concurrent is 0 then we reset the root to report from
                    // here
                    if (concurrentCount == 0) {
                        rootNode = sn;
                    }
                    if (isAdditive(sn)) {
                        concurrentCount = processAdditive(data, concurrentCount, sn, rootNode);
                        if (concurrentCount != 0) {
                            rootNode = sn;
                        }
                    } else if (!isAppendingStringLiteral(sn)) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    } else {
                        concurrentCount++;
                    }
                    lastBlock = currentBlock;
                }
            } else if (n.getImage().endsWith(".toString") || n.getImage().endsWith(".length")) {
                // ignore toString and length, they do not change affect the content of the sb
            } else {
                // usage of the stringbuilder variable for any other purpose, including
                // calling e.g. delete
                checkForViolation(rootNode, data, concurrentCount);
                concurrentCount = 0;
            }
        }
        checkForViolation(rootNode, data, concurrentCount);
        return data;
    }

    private List<NameOccurrence> determineUsages(ASTVariableDeclaratorId node) {
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : decls.entrySet()) {
            // find the first variable that matches
            if (node.hasImageEqualTo(entry.getKey().getName())) {
                return entry.getValue();
            }
        }

        return Collections.emptyList();
    }

    /**
     * Determine if the constructor contains (or ends with) a String Literal
     *
     * @param node
     * @return 1 if the constructor contains string argument, else 0
     */
    private int checkConstructor(ASTVariableDeclaratorId node, Object data) {
        Node parent = node.getParent();
        if (parent.getNumChildren() >= 2) {
            ASTAllocationExpression allocationExpression = parent.getChild(1)
                    .getFirstDescendantOfType(ASTAllocationExpression.class);
            ASTArgumentList list = null;
            if (allocationExpression != null) {
                list = allocationExpression.getFirstDescendantOfType(ASTArgumentList.class);
            }

            if (list != null) {
                ASTLiteral literal = list.getFirstDescendantOfType(ASTLiteral.class);
                if (!isAdditive(list) && literal != null && literal.isStringLiteral()) {
                    return 1;
                }
                return processAdditive(data, 0, list, node);
            }
        }
        return 0;
    }

    /**
     * Determine if during the variable initializer calls to ".append" are done.
     *
     * @param node
     * @return
     */
    private int checkInitializerExpressions(ASTVariableDeclaratorId node) {
        ASTVariableInitializer initializer = node.getParent().getFirstChildOfType(ASTVariableInitializer.class);
        ASTPrimaryExpression primary = initializer.getFirstDescendantOfType(ASTPrimaryExpression.class);

        int result = 0;
        boolean previousWasAppend = false;
        for (int i = 0; i < primary.getNumChildren(); i++) {
            Node child = primary.getChild(i);
            if (child.getNumChildren() > 0 && child.getChild(0) instanceof ASTAllocationExpression) {
                // skip the constructor call, that has already been checked
                continue;
            }
            if (child instanceof ASTPrimarySuffix) {
                ASTPrimarySuffix suffix = (ASTPrimarySuffix) child;
                if (suffix.getNumChildren() == 0 && suffix.hasImageEqualTo("append")) {
                    previousWasAppend = true;
                } else if (suffix.getNumChildren() > 0 && previousWasAppend) {
                    previousWasAppend = false;

                    ASTLiteral literal = suffix.getFirstDescendantOfType(ASTLiteral.class);
                    if (literal != null && literal.isStringLiteral()) {
                        result++;
                    } else {
                        // if it was not a String literal that was appended,
                        // then we don't
                        // have a consecutive literal string append anymore and
                        // we can skip
                        // checking the remainder of the initializer
                        break;
                    }
                }
            }
        }

        return result;
    }

    private boolean hasInitializer(ASTVariableDeclaratorId node) {
        return node.getParent().hasDescendantOfType(ASTVariableInitializer.class);
    }

    private int processAdditive(Object data, int concurrentCount, Node sn, Node rootNode) {
        ASTAdditiveExpression additive = sn.getFirstDescendantOfType(ASTAdditiveExpression.class);
        // The additive expression must of be type String to count
        if (additive == null || additive.getType() != null && !TypeHelper.isA(additive, String.class)) {
            return 0;
        }
        // check for at least one string literal
        List<ASTLiteral> literals = additive.findDescendantsOfType(ASTLiteral.class);
        boolean stringLiteralFound = false;
        for (ASTLiteral l : literals) {
            if (l.isCharLiteral() || l.isStringLiteral()) {
                stringLiteralFound = true;
                break;
            }
        }
        if (!stringLiteralFound) {
            return 0;
        }

        int count = concurrentCount;
        boolean found = false;
        for (int ix = 0; ix < additive.getNumChildren(); ix++) {
            Node childNode = additive.getChild(ix);
            if (childNode.getNumChildren() != 1 || childNode.hasDescendantOfType(ASTName.class)) {
                if (!found) {
                    checkForViolation(rootNode, data, count);
                    found = true;
                }
                count = 0;
            } else {
                count++;
            }
        }

        // no variables appended, compiler will take care of merging all the
        // string concats, we really only have 1 then
        if (!found) {
            count = 1;
        }

        return count;
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
    private boolean isAdditive(Node n) {
        List<ASTAdditiveExpression> lstAdditive = n.findDescendantsOfType(ASTAdditiveExpression.class);
        if (lstAdditive.isEmpty()) {
            return false;
        }
        // if there are more than 1 set of arguments above us we're not in the
        // append
        // but a sub-method call
        for (int ix = 0; ix < lstAdditive.size(); ix++) {
            ASTAdditiveExpression expr = lstAdditive.get(ix);
            if (expr.getParentsOfType(ASTArgumentList.class).size() != 1) {
                return false;
            }
        }
        return true;
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

    private boolean isAppendingStringLiteral(Node node) {
        Node n = node;
        while (n.getNumChildren() != 0 && !(n instanceof ASTLiteral)) {
            n = n.getChild(0);
        }
        return n instanceof ASTLiteral;
    }

    private static boolean isStringBuilderOrBuffer(ASTVariableDeclaratorId node) {
        if (node.getType() != null) {
            return TypeHelper.isEither(node, StringBuffer.class, StringBuilder.class);
        }
        Node nn = node.getTypeNameNode();
        if (nn == null || nn.getNumChildren() == 0) {
            return false;
        }
        return TypeHelper.isEither((TypeNode) nn.getChild(0), StringBuffer.class, StringBuilder.class);
    }
}
