/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.refs.JSimpleTypeDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.refs.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Base implementation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractSymbolTable implements JSymbolTable {

    /** Additional info about the context. */
    protected final SymbolTableResolveHelper myResolveHelper;
    private final JSymbolTable myParent;


    /**
     * Constructor with just the parent table.
     *
     * @param parent Parent table
     * @param helper Resolve helper
     */
    AbstractSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper) {
        this.myParent = parent;
        this.myResolveHelper = helper;
    }


    @Override
    public final JSymbolTable getParent() {
        return myParent;
    }


    protected abstract Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeNameImpl(String simpleName);


    protected abstract Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName);


    protected abstract Optional<JValueSymbol> resolveValueNameImpl(String simpleName);


    @Override
    public final Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeName(String simpleName) {
        Optional<? extends JSimpleTypeDeclarationSymbol<?>> result = resolveTypeNameImpl(simpleName);
        return result.isPresent() ? result : myParent.resolveTypeName(simpleName);
    }


    @Override
    public final Optional<JValueSymbol> resolveValueName(String simpleName) {
        Optional<JValueSymbol> result = resolveValueNameImpl(simpleName);
        return result.isPresent() ? result : myParent.resolveValueName(simpleName);
    }


    @Override
    public final Stream<JMethodSymbol> resolveMethodName(String simpleName) {
        return Stream.concat(resolveMethodNameImpl(simpleName), myParent.resolveMethodName(simpleName));
    }


    /** Gets a logger, used to have a different logger for different scopes. */
    protected abstract Logger getLogger();


    /**
     * Tries to load a class and logs it if it is not found.
     *
     * @param fqcn Binary name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    protected final Class<?> loadClass(String fqcn) {
        return myResolveHelper.loadClass(fqcn, e -> getLogger().log(Level.FINE, e, () -> "Failed loading class " + fqcn + "with an incomplete classpath."));
    }


    /**
     * Tries to load a class, not logging failure.
     *
     * @param fqcn Binary name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    protected final Class<?> loadClassIgnoreFailure(String fqcn) {
        return myResolveHelper.loadClass(fqcn, e -> {
        });
    }


}
