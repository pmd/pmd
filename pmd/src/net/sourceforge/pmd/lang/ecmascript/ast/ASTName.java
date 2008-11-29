/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Name;

public class ASTName extends AbstractEcmascriptNode<Name> {
    public ASTName(Name name) {
	super(name);
	super.setImage(name.getIdentifier());
    }

    public boolean isLocalName() {
	return node.isLocalName();
    }

    public boolean isGlobalName() {
	return !node.isLocalName();
    }
}
