/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Base class for import scopes.
 *
 * <p>Rules for shadowing of imports: bottom of https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.4.1
 *
 * <p>The simplest way to implement that is to layer the imports into several scopes.
 * Here are the highest-level scopes of a compilation unit:
 * <ul>
 * <li> {@link JavaLangScope}
 * <li> {@link ImportOnDemandScope}: never shadow anything, is shadowed by everything (see javadoc for why it's not the root)
 * <li> {@link SamePackageScope}: shadow imports-on-demands, is shadowed by single imports and lower
 * <li> {@link SingleImportScope}: shadows all of the above, is shadowed by type definitions of this compilation unit
 * </ul>
 *
 * These all have scope
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractImportScope extends AbstractExternalScope {

    // Accessibility of imports is not checked, but:
    // * inaccessible single type imports or imports of static members from an inaccessible type
    //   may not occur, since compilation would have failed
    // * single imports of inaccessible members may occur (eg static protected members), but their *use*
    //   will have been prohibited by the compiler so normally we should not be querying for them
    //   later-on
    // Imports-on-demand will check the package tree.

    final Map<String, JSymbolicClassReference> importedTypes = new HashMap<>();
    final Map<String, List<JMethodReference>> importedStaticMethods = new HashMap<>();
    final Map<String, JFieldReference> importedStaticFields = new HashMap<>();


    /**
     * Constructor with the parent scope and the auxclasspath classloader.
     * Used to build the top-level scope.
     *
     * @param parent      Parent scope
     * @param classLoader ClassLoader used to resolve e.g. import-on-demand
     * @param thisPackage Package name of the current compilation unit, used to check for accessibility
     */
    AbstractImportScope(JScope parent, PMDASMClassLoader classLoader, String thisPackage) {
        super(parent, classLoader, thisPackage);
    }


    @Override
    protected Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName) {
        return Optional.ofNullable(importedTypes.get(simpleName));
    }


    @Override
    protected Stream<JMethodReference> resolveMethodNameImpl(String simpleName) {
        return importedStaticMethods.getOrDefault(simpleName, Collections.emptyList()).stream();
    }


    @Override
    protected Optional<JVarReference> resolveValueNameImpl(String simpleName) {
        return Optional.ofNullable(importedStaticFields.get(simpleName));
    }
}
