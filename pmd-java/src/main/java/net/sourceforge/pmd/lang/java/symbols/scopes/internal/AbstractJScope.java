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
 * Base implementation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractJScope implements JScope {

    private final JScope parent;


    /**
     * Constructor with just the parent scope.
     *
     * @param parent Parent scope
     */
    AbstractJScope(JScope parent) {
        this.parent = parent;
    }


    @Override
    public JScope getParent() {
        return parent;
    }


    protected abstract Optional<? extends JSimpleTypeReference<?>> resolveTypeNameImpl(String simpleName);


    protected abstract Stream<JMethodReference> resolveMethodNameImpl(String simpleName);


    protected abstract Optional<JVarReference> resolveValueNameImpl(String simpleName);


    @Override
    public Optional<? extends JSimpleTypeReference<?>> resolveTypeName(String simpleName) {
        Optional<? extends JSimpleTypeReference<?>> result = resolveTypeNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveTypeName(simpleName);
    }


    @Override
    public final Optional<JVarReference> resolveValueName(String simpleName) {
        Optional<JVarReference> result = resolveValueNameImpl(simpleName);
        return result.isPresent() ? result : parent.resolveValueName(simpleName);
    }


    @Override
    public final Stream<JMethodReference> resolveMethodName(String simpleName) {
        // TODO prevents methods with override-equivalent signatures to occur more than once in the stream
        return Stream.concat(resolveMethodNameImpl(simpleName), parent.resolveMethodName(simpleName));
    }


}
