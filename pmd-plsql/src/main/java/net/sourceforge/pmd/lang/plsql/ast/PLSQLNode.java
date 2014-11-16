/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

public interface PLSQLNode extends Node, ScopedNode {

	/** Accept the visitor. **/
	Object jjtAccept(PLSQLParserVisitor visitor, Object data);

	/** Accept the visitor. **/
	Object childrenAccept(PLSQLParserVisitor visitor, Object data);
	
	Scope getScope();
	
	void setScope(Scope scope);

}