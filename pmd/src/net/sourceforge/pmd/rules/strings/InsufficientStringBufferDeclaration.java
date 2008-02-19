/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMultiplicativeExpression;
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
import net.sourceforge.pmd.typeresolution.TypeHelper;

/**
 * This rule finds StringBuffers which may have been pre-sized incorrectly
 * 
 * See http://sourceforge.net/forum/forum.php?thread_id=1438119&forum_id=188194
 * @author Allan Caplan
 */
public class InsufficientStringBufferDeclaration extends AbstractRule {

    private final static Set<Class<? extends SimpleNode>> blockParents;

    static {
        blockParents = new HashSet<Class<? extends SimpleNode>>();
        blockParents.add(ASTIfStatement.class);
        blockParents.add(ASTSwitchStatement.class);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isA(node.getNameDeclaration(), StringBuffer.class)) {
            return data;
        }
        Node rootNode = node;
        int anticipatedLength = 0;
        int constructorLength = 16;

        constructorLength = getConstructorLength(node, constructorLength);
        anticipatedLength = getInitialLength(node);
        List<NameOccurrence> usage = node.getUsages();
        Map<Node, Map<Node, Integer>> blocks = new HashMap<Node, Map<Node, Integer>>();
        for (int ix = 0; ix < usage.size(); ix++) {
            NameOccurrence no = usage.get(ix);
            SimpleNode n = no.getLocation();
            if (!InefficientStringBuffering.isInStringBufferOperation(n, 3, "append")) {

                if (!no.isOnLeftHandSide() && !InefficientStringBuffering.isInStringBufferOperation(n, 3, "setLength")) {
                    continue;
                }
                if (constructorLength != -1 && anticipatedLength > constructorLength) {
                    anticipatedLength += processBlocks(blocks);
                    String[] param = { String.valueOf(constructorLength), String.valueOf(anticipatedLength) };
                    addViolation(data, rootNode, param);
                }
                constructorLength = getConstructorLength(n, constructorLength);
                rootNode = n;
                anticipatedLength = getInitialLength(node);
            }
            ASTPrimaryExpression s = n.getFirstParentOfType(ASTPrimaryExpression.class);
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
    private void storeBlockStatistics(Map<Node, Map<Node, Integer>> blocks, int thisSize, Node block) {
        Node statement = block.jjtGetParent();
        if (ASTIfStatement.class.equals(block.jjtGetParent().getClass())) {
            // Else Ifs are their own subnode in AST. So we have to
            // look a little farther up the tree to find the IF statement
            Node possibleStatement = ((SimpleNode) statement).getFirstParentOfType(ASTIfStatement.class);
            while(possibleStatement != null && possibleStatement.getClass().equals(ASTIfStatement.class)) {
                statement = possibleStatement;
                possibleStatement = ((SimpleNode) possibleStatement).getFirstParentOfType(ASTIfStatement.class);
            }
        }
        Map<Node, Integer> thisBranch = blocks.get(statement);
        if (thisBranch == null) {
            thisBranch = new HashMap<Node, Integer>();
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
        for (Map.Entry<Node, Map<Node, Integer>> entry: blocks.entrySet()) {
            ifLength = 0;
            for (Map.Entry<Node, Integer> entry2: entry.getValue().entrySet()) {
                Integer value = entry2.getValue();
                ifLength = Math.max(ifLength, value.intValue());
            }
            anticipatedLength += ifLength;
        }
        return anticipatedLength;
    }

    private int processAdditive(SimpleNode sn) {
        ASTAdditiveExpression additive = sn.getFirstChildOfType(ASTAdditiveExpression.class);
        if (additive == null) {
            return 0;
        }
        int anticipatedLength = 0;
        for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
            SimpleNode childNode = (SimpleNode) additive.jjtGetChild(ix);
            ASTLiteral literal = childNode.getFirstChildOfType(ASTLiteral.class);
            if (literal != null && literal.getImage() != null) {
                anticipatedLength += literal.getImage().length() - 2;
            }
        }

        return anticipatedLength;
    }

    private static final boolean isLiteral(String str) {
        if (str.length() == 0) {
            return false;
        }
        char c = str.charAt(0);
        return (c == '"' || c == '\'');
    }

    private int processNode(SimpleNode sn) {
        int anticipatedLength = 0;
        ASTPrimaryPrefix xn = sn.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (xn.jjtGetNumChildren() != 0 && xn.jjtGetChild(0).getClass().equals(ASTLiteral.class)) {
            String str = ((SimpleNode) xn.jjtGetChild(0)).getImage();
            if (str != null) {
	            if(isLiteral(str)){
	                anticipatedLength += str.length() - 2;
	            } else if(str.startsWith("0x")){
	                anticipatedLength += 1;
	            } else {
	                anticipatedLength += str.length();
	            }
            }
        }
        return anticipatedLength;
    }

    private int getConstructorLength(SimpleNode node, int constructorLength) {
        int iConstructorLength = constructorLength;
        SimpleNode block = node.getFirstParentOfType(ASTBlockStatement.class);
        List<ASTLiteral> literal;

        if (block == null) {
            block = node.getFirstParentOfType(ASTFieldDeclaration.class);
        }
        if (block == null) {
            block = node.getFirstParentOfType(ASTFormalParameter.class);
            if (block != null) {
                iConstructorLength = -1;
            }
        }
        
        //if there is any addition/subtraction going on then just use the default.
        ASTAdditiveExpression exp = block.getFirstChildOfType(ASTAdditiveExpression.class);
        if(exp != null){
            return 16;
        }
        ASTMultiplicativeExpression mult = block.getFirstChildOfType(ASTMultiplicativeExpression.class);
        if(mult != null){
            return 16;
        }
        
        literal = block.findChildrenOfType(ASTLiteral.class);
        if (literal.isEmpty()) {
            List<ASTName> name = block.findChildrenOfType(ASTName.class);
            if (!name.isEmpty()) {
                iConstructorLength = -1;
            }
        } else if (literal.size() == 1) {
            String str = literal.get(0).getImage();
            if (str == null) {
                iConstructorLength = 0;
            } else if (isLiteral(str)) {
                // since it's not taken into account
                // anywhere. only count the extra 16
                // characters
                iConstructorLength = 14 + str.length(); // don't add the constructor's length,
            } else {
                iConstructorLength = Integer.parseInt(str);
            }
        } else {
            iConstructorLength = -1;
        }
        
        if(iConstructorLength == 0){
            iConstructorLength = 16;
        }

        return iConstructorLength;
    }


    private int getInitialLength(SimpleNode node) {
        SimpleNode block = node.getFirstParentOfType(ASTBlockStatement.class);

        if (block == null) {
            block = node.getFirstParentOfType(ASTFieldDeclaration.class);
            if (block == null) {
                block = node.getFirstParentOfType(ASTFormalParameter.class);
            }
        }
        List<ASTLiteral> literal = block.findChildrenOfType(ASTLiteral.class);
        if (literal.size() == 1) {
            String str = literal.get(0).getImage();
            if (str != null && isLiteral(str)) {
                return str.length() - 2; // take off the quotes
            }
        }
        
        return 0;
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