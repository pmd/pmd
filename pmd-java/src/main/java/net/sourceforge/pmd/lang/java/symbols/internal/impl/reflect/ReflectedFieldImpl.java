/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Field;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;

class ReflectedFieldImpl extends AbstractReflectedSymbol implements JFieldSymbol {

    private final ReflectedClassImpl owner;
    private final Field myField;

    ReflectedFieldImpl(ReflectedClassImpl owner, Field myField) {
        super(owner.symFactory);
        this.owner = owner;
        this.myField = myField;
    }

    @Nullable
    @Override
    public ASTVariableDeclaratorId getDeclaration() {
        return null;
    }

    @Override
    public String getSimpleName() {
        return myField.getName();
    }

    @NonNull
    @Override
    public JClassSymbol getEnclosingClass() {
        return owner;
    }

    @Override
    public int getModifiers() {
        return myField.getModifiers();
    }


    @Override
    public boolean equals(Object o) {
        return SymbolEquality.FIELD.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.FIELD.hash(this);
    }
}
