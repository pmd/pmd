/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import java.util.Optional;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope;


/**
 * Represents a declaration. Abstracts over whether the declaration is in
 * the analysed file or not, using reflection when it's not.
 *
 * <p>This type hierarchy is probably not directly relevant to users writing
 * rules. It's mostly intended to unify the representation of type resolution
 * and symbol analysis. At least for now it's internal.
 *
 * <p>Instances are not shared across compilation units during PMD's analysis,
 * because that would prevent the garbage collection of scopes and nodes.
 *
 * @param <N> Type of AST node that can represent this type of declaration
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public interface JCodeReference<N extends Node> {


    /**
     * Gets the scope in which this declaration was brought into scope.
     * Eg. for a reference to an imported type, this will be an {@link ImportOnDemandScope},
     * or a {@link SingleImportScope}. For a reference to a local variable,
     * this will be the scope in which it was declared.
     *
     * @return the declaration scope
     */
    // TODO prove usefulness of that or remove
    // it may be useful eg to discriminate method parameters from other local variables,
    // but that could also be achieved by setting a boolean constructor parameter
    JScope getDeclaringScope();

    /**
     * Returns the node corresponding to this declaration, if it exists.
     * Some references are references to pieces of source outside of the
     * analysed file and as such, their AST isn't available.
     *
     * @return the AST node representing the declaration, or an empty optional if it doesn't exist
     */
    Optional<N> getBoundNode();


    /**
     * Gets the simple name with which this declaration may be referred to
     * when unqualified, eg the simple name of the class or name of the method.
     *
     * @return the simple name
     */
    String getSimpleName();

    // TODO add type information when TypeDefinitions are reviewed

}
