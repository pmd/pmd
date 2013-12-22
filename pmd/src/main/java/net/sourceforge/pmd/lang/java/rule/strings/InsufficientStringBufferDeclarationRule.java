/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
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
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * This rule finds StringBuffers which may have been pre-sized incorrectly
 *
 * See http://sourceforge.net/forum/forum.php?thread_id=1438119&forum_id=188194
 * @author Allan Caplan
 */
public class InsufficientStringBufferDeclarationRule extends AbstractJavaRule {

    private final static Set<Class<? extends Node>> BLOCK_PARENTS;

    static {
        BLOCK_PARENTS = new HashSet<Class<? extends Node>>(2);
        BLOCK_PARENTS.add(ASTIfStatement.class);
        BLOCK_PARENTS.add(ASTSwitchStatement.class);
    }

    public static final int DEFAULT_BUFFER_SIZE = 16;	// as specified in StringBuffer & StringBuilder

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!TypeHelper.isEither(node.getNameDeclaration(), StringBuffer.class, StringBuilder.class)) {
            return data;
        }
        Node rootNode = node;
        int anticipatedLength = 0;
        int constructorLength = DEFAULT_BUFFER_SIZE;

        constructorLength = getConstructorLength(node, constructorLength);
        anticipatedLength = getInitialLength(node);
        List<NameOccurrence> usage = node.getUsages();
        Map<Node, Map<Node, Integer>> blocks = new HashMap<Node, Map<Node, Integer>>();
        for (NameOccurrence no : usage) {
            JavaNameOccurrence jno = (JavaNameOccurrence)no;
            Node n = jno.getLocation();
            if (!InefficientStringBufferingRule.isInStringBufferOperation(n, 3, "append")) {

                if (!jno.isOnLeftHandSide() && !InefficientStringBufferingRule.isInStringBufferOperation(n, 3, "setLength")) {
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
            	Node sn = s.jjtGetChild(jx);
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
        if (block.jjtGetParent() instanceof ASTIfStatement) {
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

    private int processAdditive(Node sn) {
        ASTAdditiveExpression additive = sn.getFirstDescendantOfType(ASTAdditiveExpression.class);
        if (additive == null) {
            return 0;
        }
        int anticipatedLength = 0;
        for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
            Node childNode = additive.jjtGetChild(ix);
            ASTLiteral literal = childNode.getFirstDescendantOfType(ASTLiteral.class);
            if (literal != null && literal.getImage() != null) {
                anticipatedLength += literal.getImage().length() - 2;
            }
        }

        return anticipatedLength;
    }

    private static final boolean isStringOrCharLiteral(ASTLiteral literal) {
    	return literal.isStringLiteral() || literal.isCharLiteral();
    }

    private int processNode(Node sn) {
        int anticipatedLength = 0;
        if ( sn != null ) {
            ASTPrimaryPrefix xn = sn.getFirstDescendantOfType(ASTPrimaryPrefix.class);
            if ( xn != null ) {
                if (xn.jjtGetNumChildren() != 0 && xn.jjtGetChild(0) instanceof ASTLiteral) {
                	ASTLiteral literal = (ASTLiteral) xn.jjtGetChild(0);
                    String str = xn.jjtGetChild(0).getImage();
                    if (str != null) {
	                    if(isStringOrCharLiteral(literal)){
	                        anticipatedLength += str.length() - 2;
	                    } else if(literal.isIntLiteral() && str.startsWith("0x")){
	                    	// but only if we are not inside a cast expression
	                    	Node parentNode = literal.jjtGetParent().jjtGetParent().jjtGetParent();
							if (parentNode instanceof ASTCastExpression
	                    		&& parentNode.getFirstChildOfType(ASTType.class).getType() == char.class) {
	                    		anticipatedLength += 1;
	                    	} else {
    	                    	// e.g. 0xdeadbeef -> will be converted to a base 10 integer string: 3735928559
    	                    	anticipatedLength += String.valueOf(Long.parseLong(str.substring(2), 16)).length();
	                    	}
	                    } else {
	                        anticipatedLength += str.length();
	                    }
                    }
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append(3);
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

        //if there is any addition/subtraction going on then just use the default.
        ASTAdditiveExpression exp = block.getFirstDescendantOfType(ASTAdditiveExpression.class);
        if (exp != null){
            return DEFAULT_BUFFER_SIZE;
        }
        ASTMultiplicativeExpression mult = block.getFirstDescendantOfType(ASTMultiplicativeExpression.class);
        if (mult != null){
            return DEFAULT_BUFFER_SIZE;
        }

        List<ASTLiteral> literals = block.findDescendantsOfType(ASTLiteral.class);
        if (literals.isEmpty()) {
            List<ASTName> name = block.findDescendantsOfType(ASTName.class);
            if (!name.isEmpty()) {
                iConstructorLength = -1;
            }
        } else if (literals.size() == 1) {
        	ASTLiteral literal = literals.get(0);
            String str = literal.getImage();
            if (str == null) {
                iConstructorLength = 0;
            } else if (isStringOrCharLiteral(literal)) {
                // since it's not taken into account
                // anywhere. only count the extra 16
                // characters
                iConstructorLength = 14 + str.length(); // don't add the constructor's length,
            } else if (literal.isIntLiteral() && str.startsWith("0x")) {
            	// bug 3516101 - the string could be a hex number
            	iConstructorLength = Integer.parseInt(str.substring(2), 16);
            } else {
                iConstructorLength = Integer.parseInt(str);
            }
        } else {
            iConstructorLength = -1;
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

    private boolean isAdditive(Node n) {
        return n.hasDescendantOfType(ASTAdditiveExpression.class);
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
        while (parentNode != null && !BLOCK_PARENTS.contains(parentNode.getClass())) {
            lastNode = parentNode;
            parentNode = parentNode.jjtGetParent();
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
        int allChildren = parentNode.jjtGetNumChildren();
        ASTSwitchLabel label = null;
        for (int ix = 0; ix < allChildren; ix++) {
            Node n = parentNode.jjtGetChild(ix);
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
