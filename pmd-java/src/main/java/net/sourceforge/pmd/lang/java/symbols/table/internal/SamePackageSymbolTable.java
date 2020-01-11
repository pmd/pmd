/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;


/**
 * Resolves top-level types declared in the same package as the analysed
 * compilation unit.
 *
 * @since 7.0.0
 */
final class SamePackageSymbolTable extends AbstractSymbolTable {

    private final @Nullable ASTPackageDeclaration packageDeclaration;

    SamePackageSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper, @Nullable ASTPackageDeclaration packageDeclaration) {
        super(parent, helper);
        this.packageDeclaration = packageDeclaration;
    }


    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        // We know it's accessible, since top-level classes are either public or package private,
        // and we're in the package
        // We ignore load exceptions. We don't know if the classpath is badly configured
        // or if the type was never in this package in the first place
        JClassSymbol jClassSymbol = loadClassIgnoreFailure(myResolveHelper.prependPackageName(simpleName));
        return jClassSymbol == null ? null
                                    : new ClassResolveResult(jClassSymbol, this, packageDeclaration);
    }
}
