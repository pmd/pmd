/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import java.lang.reflect.Field;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypesFromReflection;

class ReflectedFieldImpl extends AbstractReflectedSymbol implements JFieldSymbol {

    private final ReflectedClassImpl owner;
    private final Field myField;

    ReflectedFieldImpl(ReflectedClassImpl owner, Field myField) {
        super(owner.factory);
        this.owner = owner;
        this.myField = myField;
    }

    @Override
    public String getSimpleName() {
        return myField.getName();
    }

    @Override
    public boolean isEnumConstant() {
        return myField.isEnumConstant();
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
    public JTypeMirror getTypeMirror(Substitution subst) {
        return TypesFromReflection.fromReflect(getTypeSystem(), myField.getGenericType(), getEnclosingClass().getLexicalScope(), subst);
    }

}
