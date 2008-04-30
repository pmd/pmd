package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Non-commented source statement counter for methods.
 * 
 * @author Jason Bennett
 */
public class NcssMethodCountRule extends AbstractNcssCountRule {

  /**
   * Count the size of all non-constructor methods.
   */
  public NcssMethodCountRule() {
    super( ASTMethodDeclaration.class );
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    return super.visit( node, data );
  }
  
  @Override
  public Object[] getViolationParameters(DataPoint point) {
    return new String[] {
              ( (ASTMethodDeclaration) point.getNode() ).getMethodName(),
              String.valueOf( (int) point.getScore() ) };
  }
}
