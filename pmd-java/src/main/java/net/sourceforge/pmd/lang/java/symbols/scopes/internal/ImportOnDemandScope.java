/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;


/**
 * Scope for imports on demand. Precedence is lower than for other imports so they're put
 * in a higher-level scope.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class ImportOnDemandScope extends AbstractImportScope {


    private static final Logger LOG = Logger.getLogger(ImportOnDemandScope.class.getName());


    // TODO PackageScope
    ImportOnDemandScope(JScope parent, List<ASTImportDeclaration> importsOnDemand) {
        super(parent);

        for (ASTImportDeclaration anImport : importsOnDemand) {

            if (anImport.isStatic()) {
                // Static-Import-on-Demand Declaration
                // A static-import-on-demand declaration allows all accessible static members of a named type to be imported as needed.
                // includes types members, methods & fields

                Class<?> containerClass = loadClass(anImport.getImportedName());
                if (containerClass != null) {

                    Map<String, List<JMethodReference>> methods = Arrays.stream(containerClass.getDeclaredMethods())
                                                                        .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                                        .map(m -> new JMethodReference(this, m))
                                                                        .collect(Collectors.groupingBy(JMethodReference::getSimpleName));

                    importedStaticMethods.putAll(methods);

                    Arrays.stream(containerClass.getDeclaredFields())
                          .filter(f -> Modifier.isStatic(f.getModifiers()))
                          .map(f -> new JFieldReference(this, f))
                          .forEach(f -> importedStaticFields.put(f.getSimpleName(), f));

                    Arrays.stream(containerClass.getDeclaredClasses())
                          .filter(t -> Modifier.isStatic(t.getModifiers()))
                          .map(t -> new JSymbolicClassReference(this, t))
                          .forEach(t -> importedTypes.put(t.getSimpleName(), t));
                }

                // can't be resolved sorry

            } else {
                // Type-Import-on-Demand Declaration
                // This is of the kind <packageName>.*;

                // TODO explore the resource directories? use Reflections library?
                // https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection

            }
        }
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
