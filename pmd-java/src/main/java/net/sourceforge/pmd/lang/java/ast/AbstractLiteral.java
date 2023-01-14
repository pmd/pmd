/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.document.Chars;

/**
 * @author Clément Fournier
 */
abstract class AbstractLiteral extends AbstractJavaExpr implements ASTLiteral {

    AbstractLiteral(int i) {
        super(i);
    }

    @Override
    public final Chars getText() {
        // literals are exactly one token
        return getFirstToken().getImageCs();
    }

    @Override
    public boolean isCompileTimeConstant() {
        return true; // note: NullLiteral overrides this to false
    }
}
