/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSimpleTypeReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Dummy empty scope representing the top of all scope stacks.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class EmptyScope implements JScope {


    private static final EmptyScope INSTANCE = new EmptyScope();


    private EmptyScope() {

    }


    /**
     * Returns the shared instance.
     */
    public static EmptyScope getInstance() {
        return INSTANCE;
    }


    @Override
    public JScope getParent() {
        return null;
    }


    @Override
    public Optional<? extends JSimpleTypeReference<?>> resolveTypeName(String simpleName) {
        return Optional.empty();
    }


    @Override
    public Optional<JVarReference> resolveValueName(String simpleName) {
        return Optional.empty();
    }


    @Override
    public Stream<JMethodReference> resolveMethodName(String simpleName) {
        return Stream.empty();
    }
}
