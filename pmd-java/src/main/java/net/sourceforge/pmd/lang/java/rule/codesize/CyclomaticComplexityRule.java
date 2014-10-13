/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;

/**
 * @author Donald A. Leckie,
 *
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr 2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends StdCyclomaticComplexityRule {

  @Override
public Object visit(ASTIfStatement node, Object data) {
    super.visit( node, data );

    int boolCompIf = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    entryStack.peek().bumpDecisionPoints( boolCompIf );
    return data;
  }

  @Override
public Object visit(ASTForStatement node, Object data) {
    super.visit( node, data );

    int boolCompFor = NPathComplexityRule.sumExpressionComplexity( node.getFirstDescendantOfType( ASTExpression.class ) );
    entryStack.peek().bumpDecisionPoints( boolCompFor );
    return data;
  }

  @Override
public Object visit(ASTDoStatement node, Object data) {
    super.visit( node, data );

    int boolCompDo = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    entryStack.peek().bumpDecisionPoints( boolCompDo );
    return data;
  }

  @Override
public Object visit(ASTSwitchStatement node, Object data) {
    super.visit( node, data );

    int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    entryStack.peek().bumpDecisionPoints( boolCompSwitch );
    return data;
  }

  @Override
public Object visit(ASTWhileStatement node, Object data) {
    super.visit( node, data );

    int boolCompWhile = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    entryStack.peek().bumpDecisionPoints( boolCompWhile );
    return data;
  }

  @Override
public Object visit(ASTConditionalExpression node, Object data) {
    super.visit( node, data );

    if ( node.isTernary() ) {
      int boolCompTern = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
      entryStack.peek().bumpDecisionPoints( boolCompTern );
    }
    return data;
  }
}
