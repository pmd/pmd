/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.ShadowGroup;

// @formatter:off
/**
 * A symbol table for a particular region of a Java program. Keeps track of the types,
 * values, and methods accessible from their simple name in their extent.
 *
 * <p>Each symbol table is linked to a parent table, and keeps track of a particular set
 * of declarations having the same relative precedence. When a symbol table is asked for
 * the meaning of a name in a particular syntactic context (type name, method name, value name),
 * it first determines if it tracks a declaration with a matching name.
 * <ul>
 *      <li>If there is one, it returns the {@link JElementSymbol} representing the entity
 *          the name stands for in the given context;
 *      <li>If there is none, it asks the same question to its parent table recursively
 *          and returns that result.
 * </ul>
 * This allows directly encoding shadowing and hiding mechanisms in the parent-child
 * relationships.
 *
 * @since 7.0.0
 */
// @formatter:on
@Experimental
public interface JSymbolTable {


    /**
     * Returns the parent of this table, that is, the symbol table that will be
     * delegated to if this table doesn't find a declaration.
     *
     * @return a symbol table, or null if this is the top-level symbol table
     */
    default JSymbolTable getParent() {
        return null;
    }

    // note that types and value names can be obscured, but that depends on the syntactic
    // context of the *usage* and is not relevant to the symbol table stack.


    @Nullable
    default JTypeDeclSymbol resolveTypeName(String simpleName) {
        return types().resolveFirst(simpleName);
    }


    ShadowGroup<JVariableSymbol> variables();


    ShadowGroup<JTypeDeclSymbol> types();


    ShadowGroup<JMethodSymbol> methods();


}
