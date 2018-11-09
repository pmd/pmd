/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JVarReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Base class for import scopes.
 *
 * <p>Rules for shadowing of imports: bottom of https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.4.1
 *
 * <p>The simplest way to implement that is to layer the imports into several scopes.
 * Here are the highest-level scopes of a compilation unit:
 * <ul>
 * <li> {@link JavaLangScope}: actually is defined as
 * <li> {@link ImportOnDemandScope}: never shadow anything
 * <li> PackageTypesScope:
 * <li> {@link SingleImportScope}
 * </ul>
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractImportScope extends AbstractExternalScope {

    // Accessibility is not handled! But:
    // * inaccessible single type imports or imports of static members from an inaccessible type
    //   may not occur, since compilation would have failed
    // * imports of inaccessible members may occur (eg static protected members), but their *use*
    //   will have been prohibited by the compiler so normally we should not be querying for them
    //   later-on

    final Map<String, JSymbolicClassReference> importedTypes = new HashMap<>();
    final Map<String, List<JMethodReference>> importedStaticMethods = new HashMap<>();
    final Map<String, JFieldReference> importedStaticFields = new HashMap<>();


    AbstractImportScope(JScope parent) {
        super(parent);
    }


    @Override
    protected Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName) {
        return Optional.ofNullable(importedTypes.get(simpleName));
    }


    @Override
    protected Iterator<JMethodReference> resolveMethodNameImpl(String simpleName) {
        return importedStaticMethods.getOrDefault(simpleName, Collections.emptyList()).iterator();
    }


    @Override
    protected Optional<JVarReference> resolveValueNameImpl(String simpleName) {
        return Optional.ofNullable(importedStaticFields.get(simpleName));
    }
}
