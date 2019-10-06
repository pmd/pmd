/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;


/**
 * Symbolic version of {@link JClassSymbol}, which doesn't load a type
 * but provides access to its canonical name. It can try building a full type reference,
 * but this may fail. This kind of reference may be used by functions like typeIs() or
 * TypeHelper to test the type in the absence of a complete auxclasspath, but cannot
 * be used properly by type resolution since it needs access to eg supertypes and members.
 *
 * <p>Naturally, anonymous and local classes may not be represented by
 * this symbol.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JResolvableClassSymbol extends JSimpleTypeSymbol, BoundToNode<ASTAnyTypeDeclaration> {


    /**
     * Returns the qualified name representing this class. This is the
     * {@link JClassSymbol#getCanonicalName() canonical name} of the class.
     *
     * @return a qualified name
     */
    String getCanonicalName();


    /**
     * Attempts to convert this reference into the richer {@link JClassSymbol}
     * by loading the class. If the class can't be resolved (incomplete classpath),
     * returns null. Also, maybe the class is already loaded.
     */
    @Nullable
    JClassSymbol loadClass();
}
