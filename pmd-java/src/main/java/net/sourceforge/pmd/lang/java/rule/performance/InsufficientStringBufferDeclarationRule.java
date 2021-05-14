/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledBlock;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * This rule finds StringBuffers which may have been pre-sized incorrectly.
 *
 * @author Allan Caplan
 * @see <a href="https://sourceforge.net/p/pmd/discussion/188194/thread/aba9dae7/">Check StringBuffer sizes against usage </a>
 */
public class InsufficientStringBufferDeclarationRule extends AbstractJavaRule {

    private static final Set<Class<? extends Node>> BLOCK_PARENTS;

    static {
        BLOCK_PARENTS = new HashSet<>();
        BLOCK_PARENTS.add(ASTIfStatement.class);
        BLOCK_PARENTS.add(ASTSwitchStatement.class);
        BLOCK_PARENTS.add(ASTSwitchLabeledBlock.class);
        BLOCK_PARENTS.add(ASTSwitchLabeledExpression.class);
    }

    // as specified in StringBuffer and StringBuilder
    public static final int DEFAULT_BUFFER_SIZE = 16;

    public InsufficientStringBufferDeclarationRule() {
        addRuleChainVisit(ASTVariableDeclaratorId.class);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.getNameDeclaration() == null
                || !ConsecutiveLiteralAppendsRule.isStringBuilderOrBuffer(node)) {
            return data;
        }
        Node rootNode = node;
        int anticipatedLength = 0;
        int constructorLength = DEFAULT_BUFFER_SIZE;

        constructorLength = getConstructorLength(node, constructorLength);
        anticipatedLength = getInitialLength(node);

        anticipatedLength += getConstructorAppendsLength(node);

        List<NameOccurrence> usage = node.getUsages();
        Map<Node, Map<Node, Integer>> blocks = new HashMap<>();
        for (NameOccurrence no : usage) {
            JavaNameOccurrence jno = (JavaNameOccurrence) no;
            Node n = jno.getLocation();
            if (!InefficientStringBufferingRule.isInStringBufferOperationChain(n, "append")) {

                if (!jno.isOnLeftHandSide()
                        && !(InefficientStringBufferingRule.isInStringBufferOperationChain(n, "setLength")
                                || InefficientStringBufferingRule.isInStringBufferOperationChain(n, "ensureCapacity"))) {
                    continue;
                }

                if (n.getImage().endsWith("setLength")) {
                    int newLength = getConstructorLength(n, 0);
                    if (newLength > constructorLength) {
                        constructorLength = newLength; // a bigger setLength increases capacity
                        rootNode = n;
                    }
                    anticipatedLength = newLength; // setLength fills the string builder, any new append adds to this
                } else if (n.getImage().endsWith("ensureCapacity")) {
                    int newCapacity = getConstructorLength(n, 0);
                    if (newCapacity > constructorLength) { // only a bigger new capacity changes the capacity
                        constructorLength = newCapacity;
                        rootNode = n;
                    }
                } else {
                    // this is a constructor call. report possible violation for the old instance now
                    if (constructorLength != -1 && anticipatedLength > constructorLength) {
                        anticipatedLength += processBlocks(blocks);
                        reportViolation(data, node, rootNode, constructorLength, anticipatedLength);
                    }

                    // new initial capacity
                    constructorLength = getConstructorLength(n, constructorLength);
                    rootNode = n;
                    anticipatedLength = getInitialLength(n);
                }

                if (constructorLength != -1 && anticipatedLength > constructorLength) {
                    anticipatedLength += processBlocks(blocks);
                    reportViolation(data, node, rootNode, constructorLength, anticipatedLength);
                }
            } else {
                ASTPrimaryExpression s = n.getFirstParentOfType(ASTPrimaryExpression.class);
                int numChildren = s.getNumChildren();
                for (int jx = 0; jx < numChildren; jx++) {
                    Node sn = s.getChild(jx);
                    if (!(sn instanceof ASTPrimarySuffix) || sn.getImage() != null) {
                        continue;
                    }
                    int thisSize = 0;
                    Node block = getFirstParentBlock(sn);
                    if (isAdditive(sn)) {
                        thisSize = processAdditive(sn);
                    } else {
                        thisSize = processNode(sn);
                    }
                    if (block != null) {
                        storeBlockStatistics(blocks, thisSize, block);
                    } else {
                        anticipatedLength += thisSize;
                    }
                }
            }
        }
        anticipatedLength += processBlocks(blocks);
        if (constructorLength != -1 && anticipatedLength > constructorLength) {
            reportViolation(data, node, rootNode, constructorLength, anticipatedLength);
        }
        return data;
    }

    private void reportViolation(Object data, ASTVariableDeclaratorId instance, Node reportNode, int capacity,
            int anticipatedLength) {
        String typeName = "StringBuilder";
        if (instance.getType() != null) {
            typeName = instance.getType().getSimpleName();
        }

        addViolation(data, reportNode, new Object[] {typeName, capacity, anticipatedLength});
    }

    /**
     * This rule is concerned with IF and Switch blocks. Process the block into
     * a local Map, from which we can later determine which is the longest block
     * inside
     *
     * @param blocks
     *            The map of blocks in the method being investigated
     * @param thisSize
     *            The size of the current block
     * @param block
     *            The block in question
     */
    private void storeBlockStatistics(Map<Node, Map<Node, Integer>> blocks, int thisSize, Node block) {
        Node statement = block.getParent();
        if (block.getParent() instanceof ASTIfStatement) {
            // Else Ifs are their own subnode in AST. So we have to
            // look a little farther up the tree to find the IF statement
            Node possibleStatement = statement.getFirstParentOfType(ASTIfStatement.class);
            while (possibleStatement instanceof ASTIfStatement) {
                statement = possibleStatement;
                possibleStatement = possibleStatement.getFirstParentOfType(ASTIfStatement.class);
            }
        }
        Map<Node, Integer> thisBranch = blocks.get(statement);
        if (thisBranch == null) {
            thisBranch = new HashMap<>();
            blocks.put(statement, thisBranch);
        }
        Integer x = thisBranch.get(block);
        if (x != null) {
            thisSize += x;
        }
        thisBranch.put(statement, thisSize);
    }

    private int processBlocks(Map<Node, Map<Node, Integer>> blocks) {
        int anticipatedLength = 0;
        int ifLength = 0;
        for (Map.Entry<Node, Map<Node, Integer>> entry : blocks.entrySet()) {
            ifLength = 0;
            for (Map.Entry<Node, Integer> entry2 : entry.getValue().entrySet()) {
                Integer value = entry2.getValue();
                ifLength = Math.max(ifLength, value.intValue());
            }
            anticipatedLength += ifLength;
        }
        return anticipatedLength;
    }

    private int processAdditive(Node sn) {
        ASTAdditiveExpression additive = sn.getFirstDescendantOfType(ASTAdditiveExpression.class);
        if (additive == null) {
            return 0;
        }
        int anticipatedLength = 0;
        for (int ix = 0; ix < additive.getNumChildren(); ix++) {
            Node childNode = additive.getChild(ix);
            ASTLiteral literal = childNode.getFirstDescendantOfType(ASTLiteral.class);
            if (literal != null && literal.getImage() != null) {
                anticipatedLength += literal.getImage().length() - 2;
            }
        }

        return anticipatedLength;
    }

    private static boolean isStringOrCharLiteral(ASTLiteral literal) {
        return literal.isStringLiteral() || literal.isCharLiteral();
    }

    private int processNode(Node sn) {
        int anticipatedLength = 0;
        if (sn != null) {
            ASTPrimaryPrefix xn = sn.getFirstDescendantOfType(ASTPrimaryPrefix.class);
            if (xn != null) {
                if (xn.getNumChildren() != 0 && xn.getChild(0) instanceof ASTLiteral) {
                    ASTLiteral literal = (ASTLiteral) xn.getChild(0);
                    String str = xn.getChild(0).getImage();
                    if (str != null) {
                        if (literal.isStringLiteral()) {
                            anticipatedLength += str.length() - 2;
                        } else if (literal.isCharLiteral()) {
                            anticipatedLength += 1;
                        } else if (literal.isIntLiteral()) {
                            // but only if we are not inside a cast expression
                            Node parentNode = literal.getParent().getParent().getParent();
                            if (parentNode instanceof ASTCastExpression
                                    && ((ASTCastExpression) parentNode).getType() == char.class) {
                                anticipatedLength += 1;
                            } else {
                                // any number, regardless of the base will be converted to base 10
                                // e.g. 0xdeadbeef -> will be converted to a
                                // base 10 integer string: 3735928559
                                anticipatedLength += String.valueOf(literal.getValueAsLong()).length();
                            }
                        } else {
                            anticipatedLength += str.length();
                        }
                    }
                }
            }
        }
        return anticipatedLength;
    }

    private int getConstructorLength(Node node, int constructorLength) {
        int iConstructorLength = constructorLength;
        Node block = node.getFirstParentOfType(ASTBlockStatement.class);

        if (block == null) {
            block = node.getFirstParentOfType(ASTFieldDeclaration.class);
        }
        if (block == null) {
            block = node.getFirstParentOfType(ASTFormalParameter.class);
            if (block != null) {
                iConstructorLength = -1;
            } else {
                return DEFAULT_BUFFER_SIZE;
            }
        }

        // argumentList can be from constructor call or setLength call
        ASTArgumentList argumentList = block.getFirstDescendantOfType(ASTArgumentList.class);
        if (argumentList != null) {
            // if there are any method calls involved, we can't calculate the initial size
            List<ASTName> name = argumentList.findDescendantsOfType(ASTName.class);
            if (!name.isEmpty()) {
                iConstructorLength = -1;
            } else {
                iConstructorLength = calculateExpression(argumentList);
            }
        }

        if (iConstructorLength == 0) {
            if (constructorLength == -1) {
                iConstructorLength = DEFAULT_BUFFER_SIZE;
            } else {
                iConstructorLength = constructorLength;
            }
        }

        return iConstructorLength;
    }

    private int calculateExpression(ASTArgumentList argumentList) {
        if (argumentList == null) {
            return -1;
        }

        ASTExpression expr = argumentList.getFirstChildOfType(ASTExpression.class);
        if (expr == null) {
            return -1;
        }

        class ExpressionVisitor extends JavaParserVisitorAdapter {
            @Override
            public Object visit(ASTExpression node, Object data) {
                node.getChild(0).jjtAccept(this, data);
                return data;
            }

            @Override
            public Object visit(ASTAdditiveExpression node, Object data) {
                MutableInt partSum = new MutableInt(0);
                for (JavaNode child : node.children()) {
                    MutableInt part = new MutableInt();
                    child.jjtAccept(this, part);
                    partSum.add(part.getValue());
                }
                ((MutableInt) data).setValue(partSum.getValue());
                return data;
            }

            @Override
            public Object visit(ASTMultiplicativeExpression node, Object data) {
                MutableInt partResult = new MutableInt(1);
                for (JavaNode child : node.children()) {
                    MutableInt part = new MutableInt(0);
                    child.jjtAccept(this, part);
                    partResult.setValue(partResult.getValue() * part.getValue());
                }
                ((MutableInt) data).setValue(partResult.getValue());
                return data;
            }

            @Override
            public Object visit(ASTLiteral node, Object data) {
                ((MutableInt) data).setValue(node.getValueAsInt());
                return data;
            }
        }

        MutableInt result = new MutableInt(0);
        expr.jjtAccept(new ExpressionVisitor(), result);
        return result.getValue();
    }

    private int getInitialLength(Node node) {

        Node block = node.getFirstParentOfType(ASTBlockStatement.class);

        if (block == null) {
            block = node.getFirstParentOfType(ASTFieldDeclaration.class);
            if (block == null) {
                block = node.getFirstParentOfType(ASTFormalParameter.class);
            }
        }
        List<ASTLiteral> literals = block.findDescendantsOfType(ASTLiteral.class);
        if (literals.size() == 1) {
            ASTLiteral literal = literals.get(0);
            String str = literal.getImage();
            if (str != null && isStringOrCharLiteral(literal)) {
                return str.length() - 2; // take off the quotes
            }
        }

        return 0;
    }

    private int getConstructorAppendsLength(final Node node) {
        final Node parent = node.getFirstParentOfType(ASTVariableDeclarator.class);
        int size = 0;
        if (parent != null) {
            final Node initializer = parent.getFirstChildOfType(ASTVariableInitializer.class);
            if (initializer != null) {
                final Node primExp = initializer.getFirstDescendantOfType(ASTPrimaryExpression.class);
                if (primExp != null) {
                    for (int i = 0; i < primExp.getNumChildren(); i++) {
                        final Node sn = primExp.getChild(i);
                        if (!(sn instanceof ASTPrimarySuffix) || sn.getImage() != null) {
                            continue;
                        }
                        size += processNode(sn);
                    }
                }
            }
        }
        return size;
    }

    private boolean isAdditive(Node n) {
        ASTAdditiveExpression add = n.getFirstDescendantOfType(ASTAdditiveExpression.class);
        // if the first descendant additive expression is deeper than 4 levels,
        // it belongs to a nested method call and not anymore to the append
        // argument.
        return add != null && add.getNthParent(4) == n;
    }

    /**
     * Locate the block that the given node is in, if any
     *
     * @param node
     *            The node we're looking for a parent of
     * @return Node - The node that corresponds to any block that may be a
     *         parent of this object
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
    private static Node getSwitchParent(Node parentNode, Node lastNode) {
        int allChildren = parentNode.getNumChildren();
        ASTSwitchLabel label = null;
        for (int ix = 0; ix < allChildren; ix++) {
            Node n = parentNode.getChild(ix);
            if (n instanceof ASTSwitchLabel) {
                label = (ASTSwitchLabel) n;
            } else if (n.equals(lastNode)) {
                parentNode = label;
                break;
            }
        }
        return parentNode;
    }

}
