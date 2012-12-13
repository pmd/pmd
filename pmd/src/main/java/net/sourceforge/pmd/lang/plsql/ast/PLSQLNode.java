package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.symboltable.Scope;

public interface PLSQLNode extends Node {

	/** Accept the visitor. **/
	public abstract Object jjtAccept(PLSQLParserVisitor visitor, Object data);

	/** Accept the visitor. **/
	public abstract Object childrenAccept(PLSQLParserVisitor visitor, Object data);
	
	Scope getScope();
	
	void setScope(Scope scope);

}