package net.sourceforge.pmd.rules.codesize;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for type declarations.
 * 
 * @author Jason Bennett
 */
public class NcssTypeCount extends AbstractNcssCount {

  /**
   * Count type declarations. This includes classes as well as enums and
   * annotations.
   */
  public NcssTypeCount() {
    super( ASTTypeDeclaration.class );
  }

  public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

    if ( !node.isNested() ) {
      return super.visit( node, data );
    }

    return countNodeChildren( node, data );
  }

  public Object visit(ASTConstructorDeclaration node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTExplicitConstructorInvocation node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTEnumDeclaration node, Object data) {
    /*
     * If the enum is a type in and of itself, don't count its declaration
     * twice.
     */
    if ( node.jjtGetParent() instanceof ASTTypeDeclaration ) {
      Integer nodeCount = countNodeChildren( node, data );
      int count = nodeCount.intValue() - 1;
      return new Integer( count );
    }
    return countNodeChildren( node, data );
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTInitializer node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTFieldDeclaration node, Object data) {
    return NumericConstants.ONE;
  }

  protected void makeViolations(RuleContext ctx, Set p) {
    Iterator points = p.iterator();
    while ( points.hasNext() ) {
      DataPoint point = (DataPoint) points.next();
      addViolation( ctx, point.getNode(),
          String.valueOf( (int) point.getScore() ) );
    }
  }

}
