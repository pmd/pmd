/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;


/**
 * Represents a class or interface declaration. This is not a type! This corresponds
 * closely to a {@link Class} instance. It's the *declaration* of a type.
 *
 * <p>Unlike {@link Class} this interface isn't used to represent either
 * array types or primitive types.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JClassSymbol extends JSimpleTypeSymbol,
                                      JTypeParameterOwnerSymbol,
                                      BoundToNode<ASTAnyTypeDeclaration> {

    /**
     * Returns the fully qualified name of this class, as specified by
     * {@link Class#getName()}.
     */
    String getName();

    /**
     * Returns the simple name of this class, as specified by
     * {@link Class#getSimpleName()}.
     */
    @Override
    String getSimpleName();

    /**
     * Returns the simple name of this class, as specified by
     * {@link Class#getCanonicalName()}.
     */
    String getCanonicalName();


    /**
     * Returns the reflected class this node represents, if it's on the auxclasspath.
     * Ideally this shouldn't be used, and the symbol API should reflect everything
     * there is to know about a class.
     */
    @Nullable
    Class<?> getClassObject();


    List<JClassSymbol> getDeclaredClasses();


    List<JMethodSymbol> getDeclaredMethods();


    List<JConstructorSymbol> getConstructors();


    boolean isStrict();


    boolean isAbstract();


    default boolean isInterface() {
        return getTypeKind() == TypeKind.INTERFACE;
    }


    default boolean isEnum() {
        return getTypeKind() == TypeKind.ENUM;
    }


    default boolean isAnnotation() {
        return getTypeKind() == TypeKind.ANNOTATION;
    }


    default boolean isClass() {
        return getTypeKind() == TypeKind.CLASS;
    }



    ASTAnyTypeDeclaration.TypeKind getTypeKind();


    /**
     * Returns true if this declaration is declared final.
     */
    boolean isFinal();


    /**
     * Returns true if this declaration is static.
     */
    boolean isStatic();
}
