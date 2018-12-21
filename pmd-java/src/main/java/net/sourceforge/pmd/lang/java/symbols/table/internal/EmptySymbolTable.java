/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.internal.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JSimpleTypeDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Dummy empty scope representing the top of all scope stacks.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class EmptySymbolTable implements JSymbolTable {


    private static final EmptySymbolTable INSTANCE = new EmptySymbolTable();


    private EmptySymbolTable() {

    }


    @Override
    public JSymbolTable getParent() {
        return null;
    }


    @Override
    public Optional<? extends JSimpleTypeDeclarationSymbol<?>> resolveTypeName(String simpleName) {
        return Optional.empty();
    }


    @Override
    public Optional<JValueSymbol> resolveValueName(String simpleName) {
        return Optional.empty();
    }


    @Override
    public Stream<JMethodSymbol> resolveMethodName(String simpleName) {
        return Stream.empty();
    }


    /**
     * Returns the shared instance.
     */
    public static EmptySymbolTable getInstance() {
        return INSTANCE;
    }
}
