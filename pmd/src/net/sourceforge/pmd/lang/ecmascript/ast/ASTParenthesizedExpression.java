/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ParenthesizedExpression;

public class ASTParenthesizedExpression extends AbstractEcmascriptNode<ParenthesizedExpression> {
    public ASTParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
	super(parenthesizedExpression);
    }
}
