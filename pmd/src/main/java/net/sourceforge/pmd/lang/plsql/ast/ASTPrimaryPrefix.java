/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/* Generated By:JJTree: Do not edit this line. ASTPrimaryPrefix.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package net.sourceforge.pmd.lang.plsql.ast;

public class ASTPrimaryPrefix extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode{
  public ASTPrimaryPrefix(int id) {
    super(id);
  }

  public ASTPrimaryPrefix(PLSQLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  private boolean usesSelfModifier;

  public void setUsesSelfModifier() {
    usesSelfModifier = true;
  }

  public boolean usesSelfModifier() {
    return this.usesSelfModifier;
  }

}
/* JavaCC - OriginalChecksum=35d49f19f54d584ebf4d8b4f022496c3 (do not edit this line) */
