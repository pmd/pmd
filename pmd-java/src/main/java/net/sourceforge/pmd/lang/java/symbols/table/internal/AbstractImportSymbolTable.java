/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Base class for tables tracking import declarations.
 *
 * <p>Rules for shadowing of imports: bottom of https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.4.1
 *
 * <p>The simplest way to implement that is to layer the imports into several tables.
 * See doc on {@link JSymbolTable} about the higher level scopes of the scope stacks.
 *
 * @since 7.0.0
 */
abstract class AbstractImportSymbolTable extends AbstractSymbolTable {

    final Map<String, JClassSymbol> importedTypes = new HashMap<>();
    final Map<String, List<JMethodSymbol>> importedStaticMethods = new HashMap<>();
    final Map<String, JFieldSymbol> importedStaticFields = new HashMap<>();

    /**
     * Constructor with the parent table and the auxclasspath classloader.
     *
     * @param parent Parent table
     * @param helper Resolve helper
     */
    AbstractImportSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper) {
        super(parent, helper);
    }


    @Override
    protected @Nullable JTypeDeclSymbol resolveTypeNameImpl(String simpleName) {
        return importedTypes.get(simpleName);
    }


    @Override
    protected Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName) {
        return importedStaticMethods.getOrDefault(simpleName, Collections.emptyList()).stream();
    }


    @Override
    protected @Nullable JValueSymbol resolveValueNameImpl(String simpleName) {
        return importedStaticFields.get(simpleName);
    }


    /**
     * Returns true if the given element can be imported in the current file
     * (it's visible & accessible). This is not a general purpose accessibility
     * check and is only appropriate for imports.
     *
     *
     * <p>We consider protected members inaccessible outside of the package they were declared in,
     * which is an approximation but won't cause problems in practice.
     * In an ACU in another package, the name is accessible only inside classes that inherit
     * from the declaring class. But inheriting from a class makes its static members
     * accessible via simple name too. So this will actually be picked up by some other symbol table
     * when in the subclass. Usages outside of the subclass would have made the compilation failed.
     */
    protected boolean canBeImported(JAccessibleElementSymbol member) {
        int modifiers = member.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return true;
        } else if (Modifier.isPrivate(modifiers)) {
            return false;
        } else {
            // then it's package private, or protected
            return myResolveHelper.getThisPackage().equals(member.getPackageName());
        }
    }


    @Override
    boolean isPrunable() {
        return importedTypes.isEmpty()
            && importedStaticFields.isEmpty()
            && importedStaticMethods.isEmpty();
    }
}
