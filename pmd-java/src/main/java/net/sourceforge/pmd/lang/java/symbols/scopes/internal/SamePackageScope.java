/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Resolves top-level types declared in the same package as the analysed
 * compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SamePackageScope extends AbstractExternalScope {

    private static final Logger LOG = Logger.getLogger(SamePackageScope.class.getName());


    /**
     * Builds a new SamePackageScope.
     *
     * @param parent      Parent scope
     * @param loader      ClassLoader used to resolve types from this package
     * @param thisPackage Package name of the current compilation unit, used to check for accessibility
     */
    SamePackageScope(JScope parent, PMDASMClassLoader loader, String thisPackage) {
        super(parent, loader, thisPackage);
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


    @Override
    protected Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName) {

        // account for unnamed package
        String fqcn = thisPackage.isEmpty() ? simpleName : (thisPackage + "." + simpleName);

        try {
            // We know it's accessible, since top-level classes are either public or package private,
            // and we're in the package
            return Optional.ofNullable(classLoader.loadClass(fqcn))
                           .map(t -> new JSymbolicClassReference(this, t));
        } catch (ClassNotFoundException | LinkageError e2) {
            // ignore the exception. We don't know if the classpath is badly configured
            // or if the type was never in this package in the first place
            return Optional.empty();
        }
    }


    @Override
    protected Stream<JMethodReference> resolveMethodNameImpl(String simpleName) {
        return Stream.empty();
    }


    @Override
    protected Optional<JVarReference> resolveValueNameImpl(String simpleName) {
        return Optional.empty();
    }
}
