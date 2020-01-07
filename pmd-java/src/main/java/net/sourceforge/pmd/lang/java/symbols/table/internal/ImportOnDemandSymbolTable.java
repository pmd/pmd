/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Scope for imports on demand. Imports-on-demand never shadow anything, so this scope, if it exists,
 * is the top-level non-empty scope. All scope stacks have {@link EmptySymbolTable} as bottom though, for
 * implementation simplicity.
 *
 * @since 7.0.0
 */
final class ImportOnDemandSymbolTable extends AbstractImportSymbolTable {

    private static final Logger LOG = Logger.getLogger(ImportOnDemandSymbolTable.class.getName());

    /** Stores the names of packages and types for which all their types are imported. */
    private final List<String> importedPackagesAndTypes = new ArrayList<>();


    /**
     * Creates a new import-on-demand table.
     *
     * @param parent          Parent table
     * @param helper          Resolve helper
     * @param importsOnDemand List of import-on-demand statements, mustn't be single imports!
     */
    public ImportOnDemandSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper, List<ASTImportDeclaration> importsOnDemand) {
        super(parent, helper);

        for (ASTImportDeclaration anImport : importsOnDemand) {
            if (!anImport.isImportOnDemand()) {
                throw new IllegalArgumentException();
            }

            if (anImport.isStatic()) {
                // Static-Import-on-Demand Declaration
                // A static-import-on-demand declaration allows all accessible static members of a named type to be imported as needed.
                // includes types members, methods & fields

                @Nullable JClassSymbol containerClass = loadClassReportFailure(anImport.getImportedName());
                if (containerClass != null) {
                    // populate the inherited state

                    Map<String, List<JMethodSymbol>> methods = containerClass.getDeclaredMethods()
                                                                             .stream()
                                                                             .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                                             .filter(this::canBeImported)
                                                                             .collect(Collectors.groupingBy(JMethodSymbol::getSimpleName));

                    importedStaticMethods.putAll(methods);

                    containerClass.getDeclaredFields()
                                  .stream()
                                  .filter(f -> Modifier.isStatic(f.getModifiers()))
                                  .filter(this::canBeImported)
                                  .forEach(f -> importedStaticFields.put(f.getSimpleName(), f));

                    containerClass.getDeclaredClasses().stream()
                                  .filter(t -> Modifier.isStatic(t.getModifiers()))
                                  .filter(this::canBeImported)
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
    protected @Nullable JTypeDeclSymbol resolveTypeNameImpl(String simpleName) {

        // Check for static import-on-demand
        @Nullable JTypeDeclSymbol typename = super.resolveTypeNameImpl(simpleName);
        if (typename != null) {
            return typename;
        }

        // This comes from a Type-Import-on-Demand Declaration
        // We'll try to brute force it:
        return importedPackagesAndTypes.stream()
                                       // here 'pack' may be a package or a type name
                                       // but can't be the unnamed package (you can't write "import .*;")
                                       .map(pack -> pack + "." + simpleName)
                                       // ignore the exception. We don't know if the classpath is badly configured
                                       // or if the type was never in this package in the first place
                                       .map(this::loadClassIgnoreFailure)
                                       .filter(Objects::nonNull)
                                       .filter(this::canBeImported)
                                       .findAny()
                                       .orElse(null);
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    boolean isPrunable() {
        return super.isPrunable() && importedPackagesAndTypes.isEmpty();
    }
}
