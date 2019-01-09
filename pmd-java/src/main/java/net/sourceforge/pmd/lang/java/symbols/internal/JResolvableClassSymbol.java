/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Optional;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * Symbolic version of {@link JClassSymbol}, which doesn't load a type
 * but provides access to its FQCN. It can try building a full type reference,
 * but this may fail. This kind of reference may be used by functions like typeIs() or
 * TypeHelper to test the type in the absence of a complete auxclasspath, but cannot
 * be used properly by type resolution since it needs access to eg supertypes and members.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JResolvableClassSymbol extends JSimpleTypeSymbol, BoundToNode<ASTAnyTypeDeclaration> {


    /**
     * Returns the qualified name representing this class.
     *
     * @return a qualified name
     */
    JavaTypeQualifiedName getQualifiedName();


    /**
     * Attempts to convert this reference into the richer {@link JClassSymbol}
     * by loading the class. If the class can't be resolved (incomplete classpath),
     * returns an empty optional.
     */
    Optional<JClassSymbol> loadClass();
}
