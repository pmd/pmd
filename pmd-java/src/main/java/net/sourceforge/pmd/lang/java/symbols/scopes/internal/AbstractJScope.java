/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Iterator;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
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


    protected Optional<JSymbolicClassReference> delegateResolveTypeName(String simpleName) {
        return parent.resolveTypeName(simpleName);
    }


    protected Iterator<JMethodReference> delegateResolveMethodName(String simpleName) {
        return parent.resolveMethodName(simpleName);
    }


    protected Optional<JVarReference> delegateResolveValueName(String simpleName) {
        return parent.resolveValueName(simpleName);
    }


}
