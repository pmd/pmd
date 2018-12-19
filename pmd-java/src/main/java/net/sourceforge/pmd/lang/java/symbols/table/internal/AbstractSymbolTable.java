/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Optional;
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

    private final JSymbolTable parent;


    /**
     * Constructor with just the parent table.
     *
     * @param parent Parent table
     */
    AbstractSymbolTable(JSymbolTable parent) {
        this.parent = parent;
    }


    @Override
    public final JSymbolTable getParent() {
        return parent;
    }


    protected abstract Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeNameImpl(String simpleName);


    protected abstract Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName);


    protected abstract Optional<JValueSymbol> resolveValueNameImpl(String simpleName);


    @Override
    public final Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeName(String simpleName) {
        Optional<? extends JSimpleTypeDeclarationSymbol<?>> result = resolveTypeNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveTypeName(simpleName);
    }


    @Override
    public final Optional<JValueSymbol> resolveValueName(String simpleName) {
        Optional<JValueSymbol> result = resolveValueNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveValueName(simpleName);
    }


    @Override
    public final Stream<JMethodSymbol> resolveMethodName(String simpleName) {
        // TODO prevents methods with override-equivalent signatures to occur more than once in the stream?
        return Stream.concat(resolveMethodNameImpl(simpleName), parent.resolveMethodName(simpleName));
    }


}
