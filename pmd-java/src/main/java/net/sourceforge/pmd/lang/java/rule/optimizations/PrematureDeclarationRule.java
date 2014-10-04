/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.optimizations;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Checks for variables in methods that are defined before they are really needed. 
 * A reference is deemed to be premature if it is created ahead of a block of code 
 * that doesn't use it that also has the ability to return or throw an exception.
 * 
 * @author Brian Remedios
 */
public class PrematureDeclarationRule extends AbstractJavaRule {

	public PrematureDeclarationRule() { }

    /**
     *
     * @param node ASTLocalVariableDeclaration
     * @param data Object
     * @return Object
     * @see net.sourceforge.pmd.lang.java.ast.JavaParserVisitor#visit(ASTLocalVariableDeclaration, Object)
     */
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
    	
    	// is it part of a for-loop declaration?
    	if (node.jjtGetParent().getClass().equals(ASTForInit.class)) {
    	   return visit((AbstractJavaNode) node, data);	// yes, those don't count
    	}
    	
    	String varName = varNameIn(node);
    	
    	AbstractJavaNode grandparent = (AbstractJavaNode)node.jjtGetParent().jjtGetParent();
   	
       	List<Node> nextBlocks = blocksAfter(grandparent, node);
       	
       	ASTBlockStatement statement;
       	
       	for (Node block : nextBlocks) {
       		
       		statement = (ASTBlockStatement)block;
       		
       		if (hasReferencesIn(statement, varName)) break;
       		
       		if (hasExit(statement)) {
       			addViolation(data, node, varName);
       			break;
       		}
       	}       	
       	
        return visit((AbstractJavaNode) node, data);
    }
    
    /**
     * Return whether a class of the specified type exists between the node argument
     * and the topParent argument.
     * 
     * @param node Node
     * @param intermediateParentClass Class
     * @param topParent Node
     * @return boolean
     */
    public static boolean hasAsParentBetween(Node node, Class<?> intermediateParentClass, Node topParent) {
    	
    	Node currentParent = node.jjtGetParent();
    	
    	while (currentParent != topParent) {
    		currentParent = currentParent.jjtGetParent();
    		if (currentParent.getClass().equals(intermediateParentClass)) return true;
    	}
    	return false;
    }
    
    /**
     * Returns whether the block contains a return call or throws an exception.
     * Exclude blocks that have these things as part of an inner class.
     * 
     * @param block ASTBlockStatement
     * @return boolean
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean hasExit(ASTBlockStatement block) {
    	
    	List exitBlocks = block.findDescendantsOfType(ASTReturnStatement.class);
    	exitBlocks.addAll(block.findDescendantsOfType(ASTThrowStatement.class));
    	
    	if (exitBlocks.isEmpty()) return false;
    	
    	// now check to see if the ones we have are part of a method on a declared inner class
    	for (int i=0; i<exitBlocks.size(); i++) {
    		Node exitNode = (Node)exitBlocks.get(i);
    		if (hasAsParentBetween(exitNode, ASTMethodDeclaration.class, block)) continue;
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Returns whether the variable is mentioned within the statement block
     * or not.
     * 
     * @param block ASTBlockStatement
     * @param varName String
     * @return boolean
     */
    private static boolean hasReferencesIn(ASTBlockStatement block, String varName) {
    	
    	List<ASTName> names = block.findDescendantsOfType(ASTName.class);
    	
    	for (ASTName name : names) {    		
    		if (isReference(varName, name.getImage())) return true;
    	}
    	return false;
    }
    
    /**
     * Return whether the shortName is part of the compound name
     * by itself or as a method call receiver.
     * 
     * @param shortName String
     * @param compoundName String
     * @return boolean
     */
    private static boolean isReference(String shortName, String compoundName) {
    	
    	int dotPos = compoundName.indexOf('.');

    	return dotPos < 0 ?
    		shortName.equals(compoundName) :
    		shortName.endsWith(compoundName.substring(0, dotPos));
    }
    
    /**
     * Return the name of the variable we just assigned something to.
     * 
     * @param node ASTLocalVariableDeclaration
     * @return String
     */
    private static String varNameIn(ASTLocalVariableDeclaration node) {
        ASTVariableDeclarator declarator = node.getFirstChildOfType(ASTVariableDeclarator.class);
        return ((ASTVariableDeclaratorId) declarator.jjtGetChild(0)).getImage();
    } 
    
    /**
     * Returns the index of the node block in relation to its siblings.
     * 
     * @param block SimpleJavaNode
     * @param node Node
     * @return int
     */
    private static int indexOf(AbstractJavaNode block, Node node) {
    
    	int count = block.jjtGetNumChildren();
    	
    	for (int i=0; i<count; i++) {
    		if (node == block.jjtGetChild(i)) return i;
    	}
    	
    	return -1;
    }
    
    /**
     * Returns all the blocks found right after the node supplied within
     * the its current scope.
     * 
     * @param block SimpleJavaNode
     * @param node SimpleNode
     * @return List
     */
    private static List<Node> blocksAfter(AbstractJavaNode block, AbstractJavaNode node) {
    	
    	int count = block.jjtGetNumChildren();
    	int start = indexOf(block, node.jjtGetParent()) + 1;
    	    	
    	List<Node> nextBlocks = new ArrayList<Node>(count);
    	
    	for (int i=start; i<count; i++) {
    		nextBlocks.add(block.jjtGetChild(i));
    	}
    	
    	return nextBlocks;
    }
}
