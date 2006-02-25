/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.oro.text.perl.Perl5Util;

/**
 * This rule finds StringBuffers which may have been pre-sized incorrectly
 * 
 * @see http://sourceforge.net/forum/forum.php?thread_id=1438119&forum_id=188194
 * @author Allan Caplan
 */
public class InsufficientStringBufferDeclaration extends AbstractRule {

    private final static Set blockParents;

    static {
        blockParents = new HashSet();
        blockParents.add(ASTIfStatement.class);
        blockParents.add(ASTSwitchStatement.class);
    }

    private final Perl5Util regexp = new Perl5Util();

    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (!"StringBuffer".equals(node.getNameDeclaration().getTypeImage())) {
            return data;
        }
        Node rootNode = node;
        int anticipatedLength = 0;
        int constructorLength = 16;

        constructorLength = getConstructorLength(node, constructorLength);
        List usage = node.getUsages();
        Map blocks = new HashMap();
        for (int ix = 0; ix < usage.size(); ix++) {
            NameOccurrence no = (NameOccurrence) usage.get(ix);
            SimpleNode n = no.getLocation();
            if (!InefficientStringBuffering.isInStringBufferAppend(n, 3)) {
                if (!no.isOnLeftHandSide()) {
                    continue;
                }
                if (constructorLength != -1 && anticipatedLength > constructorLength) {
                    anticipatedLength += processBlocks(blocks);
                    String[] param = { String.valueOf(constructorLength), String.valueOf(anticipatedLength) };
                    addViolation(data, rootNode, param);
                }
                constructorLength = getConstructorLength(n, constructorLength);
                rootNode = n;
                anticipatedLength = 0;
            }
            ASTPrimaryExpression s = (ASTPrimaryExpression) n.getFirstParentOfType(ASTPrimaryExpression.class);
            int numChildren = s.jjtGetNumChildren();
            for (int jx = 0; jx < numChildren; jx++) {
                SimpleNode sn = (SimpleNode) s.jjtGetChild(jx);
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
        anticipatedLength += processBlocks(blocks);
        if (constructorLength != -1 && anticipatedLength > constructorLength) {
            String[] param = { String.valueOf(constructorLength), String.valueOf(anticipatedLength) };
            addViolation(data, rootNode, param);
        }
        return data;
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
    private void storeBlockStatistics(Map blocks, int thisSize, Node block) {
        Node statement = block.jjtGetParent();
        if (ASTIfStatement.class.equals(block.jjtGetParent().getClass())) {
            // Else Ifs are their own subnode in AST. So we have to
            // look a little farther up the tree to find the IF statement
            Node possibleStatement = ((SimpleNode) statement).getFirstParentOfType(ASTIfStatement.class);
            if (possibleStatement != null && possibleStatement.getClass().equals(ASTIfStatement.class)) {
                statement = possibleStatement;
            }
        }
        Map thisBranch = (Map) blocks.get(statement);
        if (thisBranch == null) {
            thisBranch = new HashMap();
            blocks.put(statement, thisBranch);
        }
        Integer x = (Integer) thisBranch.get(block);
        if (x != null) {
            thisSize += x.intValue();
        }
        thisBranch.put(statement, new Integer(thisSize));
    }

    private int processBlocks(Map blocks) {
        int anticipatedLength = 0;
        int ifLength = 0;
        for (Iterator iter = blocks.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            ifLength = 0;
            for (Iterator iter2 = ((Map) entry.getValue()).entrySet().iterator(); iter2.hasNext();) {
                Map.Entry entry2 = (Map.Entry) iter2.next();
                Integer value = (Integer) entry2.getValue();
                ifLength = Math.max(ifLength, value.intValue());
            }
            anticipatedLength += ifLength;
        }
        return anticipatedLength;
    }

    private int processAdditive(SimpleNode sn) {
        ASTAdditiveExpression additive = (ASTAdditiveExpression) sn.getFirstChildOfType(ASTAdditiveExpression.class);
        if (additive == null) {
            return 0;
        }
        int anticipatedLength = 0;
        for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
            SimpleNode childNode = (SimpleNode) additive.jjtGetChild(ix);
            ASTLiteral literal = (ASTLiteral) childNode.getFirstChildOfType(ASTLiteral.class);
            if (literal != null && literal.getImage() != null) {
                anticipatedLength += literal.getImage().length() - 2;
            }
        }

        return anticipatedLength;
    }

    private int processNode(SimpleNode sn) {
        int anticipatedLength = 0;
        ASTPrimaryPrefix xn = (ASTPrimaryPrefix) sn.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (xn.jjtGetNumChildren() != 0 && xn.jjtGetChild(0).getClass().equals(ASTLiteral.class)) {

            String str = ((SimpleNode) xn.jjtGetChild(0)).getImage();
            int i = (regexp.match("/^[\"']/", str)) ? 2 : 0;
            anticipatedLength += str.length() - i;
        }
        return anticipatedLength;
    }

    private int getConstructorLength(SimpleNode node, int constructorLength) {
        SimpleNode block = (SimpleNode) node.getFirstParentOfType(ASTBlockStatement.class);
        List literal;

        if (block == null) {
            block = (ASTFieldDeclaration) node.getFirstParentOfType(ASTFieldDeclaration.class);
        }
        if (block == null) {
            block = (ASTFormalParameter) node.getFirstParentOfType(ASTFormalParameter.class);
            if (block != null) {
                constructorLength = -1;
            }
        }
        literal = (block.findChildrenOfType(ASTLiteral.class));
        if (literal.size() == 0) {
            List name = (block.findChildrenOfType(ASTName.class));
            if (name.size() != 0) {
                constructorLength = -1;
            }
        } else if (literal.size() == 1) {
            String str = ((SimpleNode) literal.get(0)).getImage();
            if (str == null) {
                constructorLength = 0;
            } else if (regexp.match("/^['\"]/", str)) {
                // since it's not taken into account
                // anywhere. only count the extra 16
                // characters
                constructorLength = 16; // don't add the constructor's length,
            } else {
                constructorLength = Integer.parseInt(str);
            }
        }
        return constructorLength;
    }

    private boolean isAdditive(SimpleNode n) {
        return n.findChildrenOfType(ASTAdditiveExpression.class).size() >= 1;
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
        Node parentNode = node.jjtGetParent();

        Node lastNode = node;
        while (parentNode != null && !blockParents.contains(parentNode.getClass())) {
            lastNode = parentNode;
            parentNode = parentNode.jjtGetParent();
        }
        if (parentNode != null && ASTIfStatement.class.equals(parentNode.getClass())) {
            parentNode = lastNode;
        } else if (parentNode != null && parentNode.getClass().equals(ASTSwitchStatement.class)) {
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

}