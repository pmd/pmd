/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.rule.codesize;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTGotoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTRaiseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractStatisticalPLSQLRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Abstract superclass for NCSS counting methods. Analogous to and cribbed from {@link net.sourceforge.pmd.lang.java.rule.codesize.AbstractNcssCountRule}.
 */
public abstract class AbstractNcssCountRule extends AbstractStatisticalPLSQLRule {
    private final static String CLASS_NAME = AbstractNcssCountRule.class.getName(); 
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME); 

    private Class<?> nodeClass;

    /**
     * Count the nodes of the given type using NCSS rules.
     * 
     * @param nodeClass
     *          class of node to count
     */
    protected AbstractNcssCountRule(Class<?> nodeClass) {
	this.nodeClass = nodeClass;
        LOGGER.fine("Counting for " + nodeClass.getCanonicalName());
    }

    @Override
    public Object visit(PLSQLNode node, Object data) {
	int numNodes = 0;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
		PLSQLNode n = (PLSQLNode) node.jjtGetChild(i);
	    Integer treeSize = (Integer) n.jjtAccept(this, data);
	    numNodes += treeSize.intValue();
	}

        LOGGER.finer("Checking candidate " + node.getClass().getCanonicalName() 
                    + " against target class " + nodeClass.getCanonicalName() 
                    + " with " + numNodes + " nodes"
                   );

	if (this.nodeClass.isInstance(node)) {
          LOGGER.fine("Matched candidate " + node.getClass().getCanonicalName() 
                        + " against target class " + nodeClass.getCanonicalName() 
                       );
	    // Add 1 to account for base node
	    numNodes++;
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    point.setScore(1.0 * numNodes);
	    point.setMessage(getMessage());
	    addDataPoint(point);
            LOGGER.fine("Running score is " +  point.getScore());
	}

	return Integer.valueOf(numNodes);
    }

    /**
     * Count the number of children of the given PLSQL node. Adds one to count the
     * node itself.
     * 
     * @param node
     *          PLSQL node having children counted
     * @param data
     *          node data
     * @return count of the number of children of the node, plus one
     */
    protected Integer countNodeChildren(Node node, Object data) {
	Integer nodeCount = null;
	int lineCount = 0;
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    nodeCount = (Integer) ((PLSQLNode) node.jjtGetChild(i)).jjtAccept(this, data);
	    lineCount += nodeCount.intValue();
	}
	return ++lineCount;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTLoopStatement node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {

	Integer lineCount = countNodeChildren(node, data);

	return lineCount;
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {

	Integer lineCount = countNodeChildren(node, data);

	return lineCount;
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {

	Integer lineCount = countNodeChildren(node, data);

	return lineCount;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTExitStatement node, Object data) {
	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTExceptionHandler node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTGotoStatement node, Object data) {
	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTRaiseStatement node, Object data) {
	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTExpression node, Object data) {

	// "For" update expressions do not count as separate lines of code
	if (node.jjtGetParent() instanceof ASTStatement) {
	    return NumericConstants.ZERO;
	}

	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTLabelledStatement node, Object data) {
	return countNodeChildren(node, data);
    }



    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
	return countNodeChildren(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        LOGGER.fine("Point score is " + point.getScore());
	return new String[] { String.valueOf((int) point.getScore()) };
    }
}
