/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

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
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.refs.JResolvableClassDeclarationSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Scope for imports on demand. Imports-on-demand never shadow anything, so this scope, if it exists,
 * is the top-level non-empty scope. All scope stacks have {@link EmptySymbolTable} as bottom though, for
 * implementation simplicity.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class ImportOnDemandSymbolTable extends AbstractImportSymbolTable {

    private static final Logger LOG = Logger.getLogger(ImportOnDemandSymbolTable.class.getName());

    /** Stores the names of packages and types for which all their types are imported. */
    private final List<String> importedPackagesAndTypes = new ArrayList<>();


    /**
     * Creates a new import-on-demand scope. Automatically linked to the {@link JavaLangSymbolTable}.
     *
     * @param parent          Parent table
     * @param classLoader     Analysis classloader
     * @param importsOnDemand List of import-on-demand statements, mustn't be single imports!
     * @param thisPackage     Package name of the current compilation unit, used to check for accessibility
     */
    public ImportOnDemandSymbolTable(JSymbolTable parent, ClassLoader classLoader, List<ASTImportDeclaration> importsOnDemand, String thisPackage) {
        super(parent, classLoader, thisPackage);

        for (ASTImportDeclaration anImport : importsOnDemand) {
            if (!anImport.isImportOnDemand()) {
                throw new IllegalArgumentException();
            }

            if (anImport.isStatic()) {
                // Static-Import-on-Demand Declaration
                // A static-import-on-demand declaration allows all accessible static members of a named type to be imported as needed.
                // includes types members, methods & fields

                Class<?> containerClass = loadClass(anImport.getImportedName());
                if (containerClass != null) {
                    // populate the inherited state

                    Map<String, List<JMethodSymbol>> methods = Arrays.stream(containerClass.getDeclaredMethods())
                                                                     .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                                     .filter(this::isAccessible)
                                                                     .map(JMethodSymbol::new)
                                                                     .collect(Collectors.groupingBy(JMethodSymbol::getSimpleName));

                    importedStaticMethods.putAll(methods);

                    Arrays.stream(containerClass.getDeclaredFields())
                          .filter(f -> Modifier.isStatic(f.getModifiers()))
                          .filter(this::isAccessible)
                          .map(JFieldSymbol::new)
                          .forEach(f -> importedStaticFields.put(f.getSimpleName(), f));

                    Arrays.stream(containerClass.getDeclaredClasses())
                          .filter(t -> Modifier.isStatic(t.getModifiers()))
                          .filter(this::isAccessible)
                          .map(JResolvableClassDeclarationSymbol::new)
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
    protected Optional<JResolvableClassDeclarationSymbol> resolveTypeNameImpl(String simpleName) {

        // Check for static import-on-demand
        Optional<JResolvableClassDeclarationSymbol> typename = super.resolveTypeNameImpl(simpleName);
        if (typename.isPresent()) {
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
                                       .filter(this::isAccessible)
                                       .map(JResolvableClassDeclarationSymbol::new)
                                       .findAny();
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
