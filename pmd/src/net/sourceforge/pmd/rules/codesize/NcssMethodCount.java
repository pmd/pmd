package net.sourceforge.pmd.rules.codesize;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Non-commented source statement counter for methods.
 * 
 * @author Jason Bennett
 */
public class NcssMethodCount extends AbstractNcssCount {

  /**
   * Count the size of all non-constructor methods.
   */
  public NcssMethodCount() {
    super( ASTMethodDeclaration.class );
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    return super.visit( node, data );
  }

  protected void makeViolations(RuleContext ctx, Set p) {
    Iterator points = p.iterator();
    while ( points.hasNext() ) {
      DataPoint point = (DataPoint) points.next();
      addViolation( ctx, point.getNode(), new String[] {
          ( (ASTMethodDeclaration) point.getNode() ).getMethodName(),
          String.valueOf( (int) point.getScore() ) } );
    }
  }

}
