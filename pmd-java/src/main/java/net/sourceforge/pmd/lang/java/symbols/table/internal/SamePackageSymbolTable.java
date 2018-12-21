/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.internal.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JResolvableClassDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Resolves top-level types declared in the same package as the analysed
 * compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SamePackageSymbolTable extends AbstractSymbolTable {

    private static final Logger LOG = Logger.getLogger(SamePackageSymbolTable.class.getName());


    /**
     * Builds a new SamePackageScope.
     *
     * @param parent      Parent table
     * @param helper Resolve helper
     */
    public SamePackageSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper) {
        super(parent, helper);
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


    @Override
    protected Optional<JResolvableClassDeclarationSymbol> resolveTypeNameImpl(String simpleName) {

        // account for unnamed package
        String fqcn = myResolveHelper.getThisPackage().isEmpty()
                      ? simpleName
                      : myResolveHelper.getThisPackage() + "." + simpleName;

        // We know it's accessible, since top-level classes are either public or package private,
        // and we're in the package
        // We ignore load exceptions. We don't know if the classpath is badly configured
        // or if the type was never in this package in the first place
        return Optional.ofNullable(loadClassIgnoreFailure(fqcn)).map(JResolvableClassDeclarationSymbol::new);
    }


    @Override
    protected Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName) {
        return Stream.empty();
    }


    @Override
    protected Optional<JValueSymbol> resolveValueNameImpl(String simpleName) {
        return Optional.empty();
    }
}
