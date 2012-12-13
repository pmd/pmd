/**
 *  Methods required to be considered as an executable piece of code.
 */
package net.sourceforge.pmd.lang.plsql.ast;


/**
 *
 * @author sturton
 */
public interface OracleObject extends PLSQLNode {

  /**
   * Gets the name of the executable: named thus to match {@link net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration}.
   *
   * @return a String representing the name of the method
   */
  String getObjectName();
  
}
