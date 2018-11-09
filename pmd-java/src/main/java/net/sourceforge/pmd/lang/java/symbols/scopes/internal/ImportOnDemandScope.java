/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;


/**
 * Scope for imports on demand. Precedence is the lowest of all imports so they're put
 * in the highest-level scope.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class ImportOnDemandScope extends AbstractImportScope {


    private static final Logger LOG = Logger.getLogger(ImportOnDemandScope.class.getName());

    private final List<String> importedPackagesAndTypes = new ArrayList<>();


    ImportOnDemandScope(List<ASTImportDeclaration> importsOnDemand) {
        super(JavaLangScope.getInstance());

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

                importedPackagesAndTypes.add(anImport.getPackageName());
            }
        }
    }


    @Override
    protected Optional<JSymbolicClassReference> resolveTypeNameImpl(String simpleName) {
        Optional<JSymbolicClassReference> typename = super.resolveTypeNameImpl(simpleName);
        if (typename.isPresent()) {
            // Here the name comes from a static import-on-demand, so the type is a static nested type
            return typename;
        }

        // This comes from a Type-Import-on-Demand Declaration
        // We'll try to brute force it:
        return importedPackagesAndTypes.stream()
                                       // here 'pack' may be a package or a type name
                                       .map(pack -> pack + "." + simpleName)
                                       .map(fqcn -> {
                                           try {
                                               // the classloader remembers the types it has failed to load so that we need not care
                                               return classLoader.loadClass(fqcn);
                                           } catch (ClassNotFoundException | LinkageError e2) {
                                               // ignore the exception. We don't know if the classpath is badly configured
                                               // or if the type was never in this package in the first place
                                               return null;
                                           }
                                       })
                                       .filter(Objects::nonNull)
                                       .map(c -> new JSymbolicClassReference(this, c))
                                       .findAny();
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
