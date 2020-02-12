/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;

/**
 *
 */
abstract class AbstractReflectedSymbol implements JElementSymbol {

    protected final ReflectionSymFactory symFactory;

    AbstractReflectedSymbol(ReflectionSymFactory symFactory) {
        this.symFactory = symFactory;
    }


    @Override
    public String toString() {
        return SymbolToStrings.REFLECT.toString(this);
    }
}
