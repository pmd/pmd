/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

abstract class AbstractReflectedSymbol implements JElementSymbol {

    protected final SymbolFactory factory;

    AbstractReflectedSymbol(SymbolFactory symFactory) {
        AssertionUtil.requireParamNotNull("symFactory", symFactory);
        this.factory = symFactory;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return factory.getTypeSystem();
    }

    @Override
    public String toString() {
        return SymbolToStrings.REFLECT.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.hash(this);
    }
}
