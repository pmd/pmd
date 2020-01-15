/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;


/**
 * Scope for imports on demand. Imports-on-demand never shadow anything, so this scope, if it exists,
 * is the top-level non-empty scope. All scope stacks have {@link EmptySymbolTable} as bottom though, for
 * implementation simplicity.
 *
 * @since 7.0.0
 */
final class ImportOnDemandSymbolTable extends AbstractImportSymbolTable {

    /** Stores the names of packages and types for which all their types are imported. */
    private final Map<String, ASTImportDeclaration> importedPackagesAndTypes = new HashMap<>();


    /**
     * @param importsOnDemand List of import-on-demand statements, mustn't be single imports!
     */
    ImportOnDemandSymbolTable(JSymbolTable parent, SymbolTableHelper helper, List<ASTImportDeclaration> importsOnDemand) {
        super(parent, helper);

        for (ASTImportDeclaration anImport : importsOnDemand) {
            assert anImport.isImportOnDemand() : "Expected import on demand: " + anImport;

            if (anImport.isStatic()) {
                // Static-Import-on-Demand Declaration
                // A static-import-on-demand declaration allows all accessible static members of a named type to be imported as needed.
                // includes types members, methods & fields

                @Nullable JClassSymbol containerClass = loadClassReportFailure(anImport, anImport.getImportedName());
                if (containerClass != null) {
                    // populate the inherited state

                    for (JMethodSymbol m : containerClass.getDeclaredMethods()) {
                        if (Modifier.isStatic(m.getModifiers()) && canBeImported(m)) {
                            importMethod(anImport, m);
                        }
                    }

                    for (JFieldSymbol f : containerClass.getDeclaredFields()) {
                        if (Modifier.isStatic(f.getModifiers()) && canBeImported(f)) {
                            importField(anImport, f);
                        }
                    }

                    for (JClassSymbol t : containerClass.getDeclaredClasses()) {
                        if (Modifier.isStatic(t.getModifiers()) && canBeImported(t)) {
                            importType(anImport, t);
                        }
                    }
                }

                // can't be resolved sorry

            } else {
                // Type-Import-on-Demand Declaration
                // This is of the kind <packageName>.*;

                importedPackagesAndTypes.put(anImport.getPackageName(), anImport);
            }
        }
    }


    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {

        // Check for static import-on-demand
        @Nullable ResolveResult<JTypeDeclSymbol> typename = super.resolveTypeNameImpl(simpleName);
        if (typename != null) {
            return typename;
        }

        // This comes from a Type-Import-on-Demand Declaration
        // We'll try to brute force it:
        for (Entry<String, ASTImportDeclaration> it : importedPackagesAndTypes.entrySet()) { // here 'pack' may be a package or a type name
            // but can't be the unnamed package (you can't write "import .*;")
            String name = it.getKey() + "." + simpleName;
            // ignore the exception. We don't know if the classpath is badly configured
            // or if the type was never there in the first place
            JClassSymbol sym = loadClassIgnoreFailure(name);
            if (sym != null && canBeImported(sym)) {
                return new ClassResolveResult(sym, this, it.getValue());
            }
        }
        return null;
    }


    @Override
    boolean isPrunable() {
        return super.isPrunable() && importedPackagesAndTypes.isEmpty();
    }
}
