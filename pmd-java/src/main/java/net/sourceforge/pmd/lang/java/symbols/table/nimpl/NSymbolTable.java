/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;


public interface NSymbolTable extends JSymbolTable {


    @Override
    @Nullable
    default ResolveResult<JTypeDeclSymbol> resolveTypeName(String simpleName) {
        return resolveCompat(simpleName, types());
    }


    @Override
    default List<JMethodSymbol> resolveMethodName(String simpleName) {
        return methods().resolve(simpleName);
    }


    @Override
    @Nullable
    default ResolveResult<JVariableSymbol> resolveValueName(String simpleName) {
        return resolveCompat(simpleName, variables());
    }


    @Override
    default JSymbolTable getParent() {
        return null;
    }


    ShadowGroup<JVariableSymbol> variables();


    ShadowGroup<JTypeDeclSymbol> types();


    ShadowGroup<JMethodSymbol> methods();


    static <T extends JElementSymbol> ResolveResult<T> resolveCompat(String simpleName, ShadowGroup<T> shadowGroup) {
        T resolve = shadowGroup.resolveFirst(simpleName);
        return resolve == null ? null : () -> resolve;
    }

}
