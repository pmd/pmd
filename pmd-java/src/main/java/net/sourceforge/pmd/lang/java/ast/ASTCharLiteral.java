/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.Chars;


/**
 * Represents a character literal. {@link #getConstValue()} allows to
 * retrieve the actual runtime value. Use {@link #getLiteralText()} to
 * retrieve the text.
 */
public final class ASTCharLiteral extends AbstractLiteral implements ASTLiteral {


    ASTCharLiteral(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Gets the char value of this literal.
     */
    @Override
    public @NonNull Character getConstValue() {
        return (Character) super.getConstValue();
    }

    @Override
    public Chars getLiteralText() {
        return super.getLiteralText();
    }
}
