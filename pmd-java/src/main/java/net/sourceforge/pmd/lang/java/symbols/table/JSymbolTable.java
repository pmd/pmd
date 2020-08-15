/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;

/**
 * A symbol table for a particular region of a Java program. Keeps track of the types,
 * values, and methods accessible from their simple name in their extent.
 *
 * <p>Instances of this interface just tie together a few {@link ShadowChain}
 * instances for each interesting namespace in the program.
 *
 * @since 7.0.0
 */
@Experimental
public interface JSymbolTable {

    /**
     * The chain of tables tracking variable names that are in scope here
     * (fields, locals, formals, etc).
     *
     * <p>The following special cases are not handled by variable symbol
     * tables:
     * <ul>
     * <li>The VariableAccess of case labels of a switch on an enum type.
     * For example, in {@code switch (someEnum) { case A: break; }}, {@code A}
     * may be out-of-scope in the outer expression. It is resolved relatively
     * to the type of the tested expression (eg {@code someEnum} here).
     * In other words, {@code variables().resolve("A")} will return a symbol
     * that is not necessarily the actual reference for the enum constant,
     * or no symbol at all. {@link ASTVariableAccess#getSignature()}
     * will be accurate though.
     * </ul>
     */
    ShadowChain<JVariableSig, ScopeInfo> variables();


    /**
     * The chain of tables tracking type names that are in scope here
     * (classes, type params, but not eg primitive types).
     *
     * <p>The following special cases are not handled by type symbol
     * tables:
     * <ul>
     * <li>The type reference of an inner class constructor call. For example,
     * in {@code new Outer().new Inner()}, {@code Inner} may be out-of-scope
     * in the outer expression. It depends on the type of the left hand expression,
     * which may be an arbitrary expression. {@code types().resolve("Inner")} will
     * return a symbol that is not necessarily the actual reference for {@code Outer.Inner},
     * or no symbol at all. {@link ASTClassOrInterfaceType#getTypeMirror()}
     * will be accurate though.
     * </ul>
     */
    ShadowChain<JTypeMirror, ScopeInfo> types();


    /**
     * The chain of tables tracking method names that are in scope here.
     * Constructors are not tracked by this.
     */
    ShadowChain<JMethodSig, ScopeInfo> methods();


}
