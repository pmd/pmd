/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Scope for single imports. Has the highest precedence among imports.
 *
 * @since 7.0.0
 */
final class SingleImportSymbolTable extends AbstractImportSymbolTable {

    private static final Logger LOG = Logger.getLogger(SingleImportSymbolTable.class.getName());


    /**
     * Creates a scope for single imports, linking it to its parent, which cares about
     * import on demand declarations.
     *
     * @param parent        Parent table
     * @param helper        Resolve helper
     * @param singleImports Import declarations, must not be on-demand!
     */
    SingleImportSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper, List<ASTImportDeclaration> singleImports) {
        super(parent, helper);

        for (ASTImportDeclaration anImport : singleImports) {
            if (anImport.isImportOnDemand()) {
                throw new IllegalArgumentException();
            }

            String simpleName = anImport.getImportedSimpleName();
            String name = anImport.getImportedName();

            if (anImport.isStatic()) {
                // Single-Static-Import Declaration
                // types, fields or methods having the same name

                String className = name.substring(0, name.lastIndexOf('.'));

                JClassSymbol containerClass = loadClassReportFailure(className);

                if (containerClass == null) {
                    // the auxclasspath is wrong
                    // bc static imports can't import toplevel types
                    // already reported
                    continue;
                }

                List<JMethodSymbol> methods = containerClass.getDeclaredMethods()
                                                            .stream()
                                                            .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                            .filter(this::canBeImported)
                                                            .filter(m -> m.getSimpleName().equals(simpleName))
                                                            .collect(Collectors.toList());

                importedStaticMethods.put(simpleName, methods);

                // check for fields

                JFieldSymbol field = containerClass.getDeclaredField(simpleName);
                if (field != null && Modifier.isStatic(field.getModifiers())) {
                    importedStaticFields.put(simpleName, field);
                }

                // check for member types

                // We don't use named directly, because if containerClass is itself an inner class then its
                // dot to dollar conversion would have already been handled

                containerClass.getDeclaredClasses().stream()
                              .filter(m -> Modifier.isStatic(m.getModifiers()))
                              .filter(this::canBeImported)
                              .filter(m -> m.getSimpleName().equals(simpleName))
                              .findFirst()
                              .ifPresent(imported -> importedTypes.put(simpleName, imported));

            } else {
                // Single-Type-Import Declaration
                importedTypes.put(simpleName, helper.findSymbolCannotFail(name));
            }
        }
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


}
