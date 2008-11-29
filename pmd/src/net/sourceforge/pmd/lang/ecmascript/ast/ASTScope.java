/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Scope;

public class ASTScope extends AbstractEcmascriptNode<Scope> {
    public ASTScope(Scope scope) {
	super(scope);
    }

    @Override
    public String toString() {
	// Note: Rather see these as 'Block' elements in XPath
	//return "Block";
	return super.toString();
    }
}
