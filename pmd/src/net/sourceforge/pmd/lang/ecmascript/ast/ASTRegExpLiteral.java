/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.RegExpLiteral;

public class ASTRegExpLiteral extends AbstractEcmascriptNode<RegExpLiteral> {
    public ASTRegExpLiteral(RegExpLiteral regExpLiteral) {
	super(regExpLiteral);
	super.setImage(regExpLiteral.getValue());
    }

    public String getFlags() {
	return node.getFlags();
    }
}