package net.sourceforge.pmd.rules.codesize;

import net.sourceforge.pmd.ast.ASTBreakStatement;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTContinueStatement;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTFinallyStatement;
import net.sourceforge.pmd.ast.ASTForInit;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLabeledStatement;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Abstract superclass for NCSS counting methods. Counts tokens according to <a
 * href="http://www.kclee.de/clemens/java/javancss/">JavaNCSS rules</a>.
 * 
 * @author Jason Bennett
 */
public abstract class AbstractNcssCount extends StatisticalRule {

  private Class nodeClass;

  /**
   * Count the nodes of the given type using NCSS rules.
   * 
   * @param nodeClass
   *          class of node to count
   */
  protected AbstractNcssCount(Class nodeClass) {
    this.nodeClass = nodeClass;
  }

  public Object visit(SimpleJavaNode node, Object data) {
    int numNodes = 0;

    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
      Integer treeSize = (Integer) simpleNode.jjtAccept( this, data );
      numNodes += treeSize.intValue();
    }

    if ( this.nodeClass.isInstance( node ) ) {
      // Add 1 to account for base node
      numNodes++;
      DataPoint point = new DataPoint();
      point.setNode( node );
      point.setScore( 1.0 * numNodes );
      point.setMessage( getMessage() );
      addDataPoint( point );
    }

    return new Integer( numNodes );
  }

  /**
   * Count the number of children of the given Java node. Adds one to count the
   * node itself.
   * 
   * @param node
   *          java node having children counted
   * @param data
   *          node data
   * @return count of the number of children of the node, plus one
   */
  protected Integer countNodeChildren(SimpleJavaNode node, Object data) {
    Integer nodeCount = null;
    int lineCount = 0;
    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      nodeCount = (Integer) ( (SimpleJavaNode) node.jjtGetChild( i ) ).jjtAccept(
          this, data );
      lineCount += nodeCount.intValue();
    }
    return new Integer( ++lineCount );
  }

  public Object visit(ASTForStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTDoStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTIfStatement node, Object data) {

    Integer lineCount = countNodeChildren( node, data );

    if ( node.hasElse() ) {
      int lines = lineCount.intValue();
      lines++;
      lineCount = new Integer( lines );
    }

    return lineCount;
  }

  public Object visit(ASTWhileStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTBreakStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTCatchStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTContinueStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTFinallyStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTReturnStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTSwitchStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTSynchronizedStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTThrowStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTStatementExpression node, Object data) {

    // "For" update expressions do not count as separate lines of code
    if ( node.jjtGetParent() instanceof ASTStatementExpressionList ) {
      return NumericConstants.ZERO;
    }

    return NumericConstants.ONE;
  }

  public Object visit(ASTLabeledStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTLocalVariableDeclaration node, Object data) {

    // "For" init declarations do not count as separate lines of code
    if ( node.jjtGetParent() instanceof ASTForInit ) {
      return NumericConstants.ZERO;
    }

    /*
     * This will count variables declared on the same line as separate NCSS
     * counts. This violates JavaNCSS standards, but I'm not convinced that's a
     * bad thing here.
     */

    return countNodeChildren( node, data );
  }

  public Object visit(ASTSwitchLabel node, Object data) {
    return countNodeChildren( node, data );
  }

}
