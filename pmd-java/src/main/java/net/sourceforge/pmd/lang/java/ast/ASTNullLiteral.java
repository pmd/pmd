/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The null literal.
 *
 * <pre class="grammar">
 *
 * NullLiteral ::= "null"
 *
 * </pre>
 */
public final class ASTNullLiteral extends AbstractLiteral implements ASTLiteral {
    ASTNullLiteral(int id) {
        super(id);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isCompileTimeConstant() {
        return false;
    }

    @Override
    public @Nullable Object getConstValue() {
        return null;
    }
}
