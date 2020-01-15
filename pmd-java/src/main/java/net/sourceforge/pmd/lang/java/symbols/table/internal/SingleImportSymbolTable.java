/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.List;

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

    SingleImportSymbolTable(JSymbolTable parent, SymbolTableHelper helper, List<ASTImportDeclaration> singleImports) {
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

                int idx = name.lastIndexOf('.');
                assert idx > 0;
                String className = name.substring(0, idx);

                JClassSymbol containerClass = loadClassReportFailure(anImport, className);

                if (containerClass == null) {
                    // the auxclasspath is wrong
                    // bc static imports can't import toplevel types
                    // already reported
                    continue;
                }

                for (JMethodSymbol jMethodSymbol : containerClass.getDeclaredMethods(simpleName)) {
                    if (Modifier.isStatic(jMethodSymbol.getModifiers()) && canBeImported(jMethodSymbol)) {
                        importMethod(anImport, jMethodSymbol);
                    }
                }


                // check for fields

                JFieldSymbol field = containerClass.getDeclaredField(simpleName);
                if (field != null && Modifier.isStatic(field.getModifiers())) {
                    importField(anImport, field);
                }

                // check for member types

                // We don't use named directly, because if containerClass is itself an inner class then its
                // dot to dollar conversion would have already been handled

                JClassSymbol klass = containerClass.getDeclaredClass(simpleName);
                if (klass != null && Modifier.isStatic(klass.getModifiers()) && canBeImported(klass)) {
                    importType(anImport, klass);
                }

            } else {
                // Single-Type-Import Declaration
                importType(anImport, helper.findSymbolCannotFail(name));
            }
        }
    }


}
