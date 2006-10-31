/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This rule finds concurrent calls to StringBuffer.append where String literals
 * are used It would be much better to make these calls using one call to
 * .append
 * <p/>
 * example:
 * <p/>
 * <pre>
 * StringBuffer buf = new StringBuffer();
 * buf.append(&quot;Hello&quot;);
 * buf.append(&quot; &quot;).append(&quot;World&quot;);
 * </pre>
 * <p/>
 * This would be more eloquently put as:
 * <p/>
 * <pre>
 * StringBuffer buf = new StringBuffer();
 * buf.append(&quot;Hello World&quot;);
 * </pre>
 * <p/>
 * The rule takes one parameter, threshold, which defines the lower limit of
 * consecutive appends before a violation is created. The default is 1.
 */
public class ConsecutiveLiteralAppends extends AbstractRule {

    private final static Set blockParents;

    static {
        blockParents = new HashSet();
        blockParents.add(ASTForStatement.class);
        blockParents.add(ASTWhileStatement.class);
        blockParents.add(ASTDoStatement.class);
        blockParents.add(ASTIfStatement.class);
        blockParents.add(ASTSwitchStatement.class);
        blockParents.add(ASTMethodDeclaration.class);
    }
    
    private static final PropertyDescriptor thresholdDescriptor = new IntegerProperty(
    		"threshold", 
    		"?",
    		1,
    		1.0f
    		);
    
    private static final Map propertyDescriptorsByName = asFixedMap(thresholdDescriptor);
 

    private int threshold = 1;

    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (!isStringBuffer(node)) {
            return data;
        }
        threshold = getIntProperty(thresholdDescriptor);

        int concurrentCount = checkConstructor(node, data);
        Node lastBlock = getFirstParentBlock(node);
        Node currentBlock = lastBlock;
        Map decls = node.getScope().getVariableDeclarations();
        SimpleNode rootNode = null;
        // only want the constructor flagged if it's really containing strings
        if (concurrentCount == 1) {
            rootNode = node;
        }
        for (Iterator iter = decls.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            List decl = (List) entry.getValue();
            for (int ix = 0; ix < decl.size(); ix++) {
                NameOccurrence no = (NameOccurrence) decl.get(ix);
                SimpleNode n = no.getLocation();

                currentBlock = getFirstParentBlock(n);

                if (!InefficientStringBuffering.isInStringBufferOperation(n, 3,"append")) {
                    if (!no.isPartOfQualifiedName()) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }
                    continue;
                }
                ASTPrimaryExpression s = (ASTPrimaryExpression) n
                        .getFirstParentOfType(ASTPrimaryExpression.class);
                int numChildren = s.jjtGetNumChildren();
                for (int jx = 0; jx < numChildren; jx++) {
                    SimpleNode sn = (SimpleNode) s.jjtGetChild(jx);
                    if (!(sn instanceof ASTPrimarySuffix)
                            || sn.getImage() != null) {
                        continue;
                    }

                    // see if it changed blocks
                    if ((currentBlock != null && lastBlock != null && !currentBlock
                            .equals(lastBlock))
                            || (currentBlock == null ^ lastBlock == null)) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }

                    // if concurrent is 0 then we reset the root to report from
                    // here
                    if (concurrentCount == 0) {
                        rootNode = sn;
                    }
                    if (isAdditive(sn)) {
                        concurrentCount = processAdditive(data,
                                concurrentCount, sn, rootNode);
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
            }
        }
        checkForViolation(rootNode, data, concurrentCount);
        return data;
    }

    /**
     * Determie if the constructor contains (or ends with) a String Literal
     *
     * @param node
     * @return 1 if the constructor contains string argument, else 0
     */
    private int checkConstructor(ASTVariableDeclaratorId node, Object data) {
        Node parent = node.jjtGetParent();
        if (parent.jjtGetNumChildren() >= 2) {
            ASTArgumentList list = (ASTArgumentList) ((SimpleNode) parent
                    .jjtGetChild(1)).getFirstChildOfType(ASTArgumentList.class);
            if (list != null) {
                ASTLiteral literal = (ASTLiteral) list
                        .getFirstChildOfType(ASTLiteral.class);
                if (!isAdditive(list) && literal != null
                        && literal.isStringLiteral()) {
                    return 1;
                } 
                return processAdditive(data, 0, list, node);
            }
        }
        return 0;
    }

    private int processAdditive(Object data, int concurrentCount,
                                SimpleNode sn, SimpleNode rootNode) {
        ASTAdditiveExpression additive = (ASTAdditiveExpression) sn
                .getFirstChildOfType(ASTAdditiveExpression.class);
        if (additive == null) {
            return 0;
        }
        int count = concurrentCount;
        boolean found = false;
        for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
            SimpleNode childNode = (SimpleNode) additive.jjtGetChild(ix);
            if (childNode.jjtGetNumChildren() != 1
                    || childNode.findChildrenOfType(ASTName.class).size() != 0) {
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
    private boolean isAdditive(SimpleNode n) {
        List lstAdditive = n.findChildrenOfType(ASTAdditiveExpression.class);
        if (lstAdditive.isEmpty()) {
            return false;
        }
        // if there are more than 1 set of arguments above us we're not in the
        // append
        // but a sub-method call
        for (int ix = 0; ix < lstAdditive.size(); ix++) {
            ASTAdditiveExpression expr = (ASTAdditiveExpression) lstAdditive.get(ix);
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
     * @param node The node to check
     * @return The first parent block
     */
    private Node getFirstParentBlock(Node node) {
        Node parentNode = node.jjtGetParent();

        Node lastNode = node;
        while (parentNode != null
                && !blockParents.contains(parentNode.getClass())) {
            lastNode = parentNode;
            parentNode = parentNode.jjtGetParent();
        }
        if (parentNode != null
                && parentNode.getClass().equals(ASTIfStatement.class)) {
            parentNode = lastNode;
        } else if (parentNode != null
                && parentNode.getClass().equals(ASTSwitchStatement.class)) {
            parentNode = getSwitchParent(parentNode, lastNode);
        }
        return parentNode;
    }

    /**
     * Determine which SwitchLabel we belong to inside a switch
     *
     * @param parentNode The parent node we're looking at
     * @param lastNode   The last node processed
     * @return The parent node for the switch statement
     */
    private Node getSwitchParent(Node parentNode, Node lastNode) {
        int allChildren = parentNode.jjtGetNumChildren();
        ASTSwitchLabel label = null;
        for (int ix = 0; ix < allChildren; ix++) {
            Node n = parentNode.jjtGetChild(ix);
            if (n.getClass().equals(ASTSwitchLabel.class)) {
                label = (ASTSwitchLabel) n;
            } else if (n.equals(lastNode)) {
                parentNode = label;
                break;
            }
        }
        return parentNode;
    }

    /**
     * Helper method checks to see if a violation occured, and adds a
     * RuleViolation if it did
     */
    private void checkForViolation(SimpleNode node, Object data,
                                   int concurrentCount) {
        if (concurrentCount > threshold) {
            String[] param = {String.valueOf(concurrentCount)};
            addViolation(data, node, param);
        }
    }

    private boolean isAppendingStringLiteral(SimpleNode node) {
        SimpleNode n = node;
        while (n.jjtGetNumChildren() != 0
                && !n.getClass().equals(ASTLiteral.class)) {
            n = (SimpleNode) n.jjtGetChild(0);
        }
        return n.getClass().equals(ASTLiteral.class);
    }

    private static boolean isStringBuffer(ASTVariableDeclaratorId node) {
        SimpleNode nn = node.getTypeNameNode();
        if (nn.jjtGetNumChildren() == 0) {
            return false;
        }
        return "StringBuffer".equals(((SimpleNode) nn.jjtGetChild(0)).getImage());
    }

    protected Map propertiesByName() {
    	return propertyDescriptorsByName;
    }
}