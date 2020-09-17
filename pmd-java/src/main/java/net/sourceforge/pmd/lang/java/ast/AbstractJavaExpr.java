/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractJavaExpr extends AbstractJavaTypeNode implements ASTExpression {

    private static final Object NOT_COMPUTED = new Object(); // null is sentinel value too

    private int parenDepth;
    private Object constValue = NOT_COMPUTED;

    AbstractJavaExpr(int i) {
        super(i);
    }


    void bumpParenDepth() {
        parenDepth++;
    }

    @Override
    public int getParenthesisDepth() {
        return parenDepth;
    }

    @Override
    public @Nullable Object getConstValue() {
        if (constValue == NOT_COMPUTED) { // NOPMD we want identity semantics
            constValue = buildConstValue();
        }
        return constValue;
    }

    protected @Nullable Object buildConstValue() {
        return acceptVisitor(ConstantFolder.INSTANCE, null);
    }
}
