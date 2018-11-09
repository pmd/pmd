/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;


/**
 * Resolves top-level types declared in the same package as the analysed
 * compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SamePackageScope extends AbstractExternalScope {

    private static final Logger LOG = Logger.getLogger(SamePackageScope.class.getName());

    private final String packageName;


    SamePackageScope(ImportOnDemandScope parent, String packageName) {
        super(parent);
        this.packageName = packageName;
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


    @Override
    protected Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName) {

        String fqcn = packageName.isEmpty() ? simpleName : (packageName + "." + simpleName);
        try {
            return Optional.ofNullable(classLoader.loadClass(fqcn))
                           .map(t -> new JSymbolicClassReference(this, t));
        } catch (ClassNotFoundException | LinkageError e2) {
            // ignore the exception. We don't know if the classpath is badly configured
            // or if the type was never in this package in the first place
            return Optional.empty();
        }
    }


    @Override
    protected Iterator<JMethodReference> resolveMethodNameImpl(String simpleName) {
        return emptyIterator();
    }


    @Override
    protected Optional<JVarReference> resolveValueNameImpl(String simpleName) {
        return Optional.empty();
    }
}
