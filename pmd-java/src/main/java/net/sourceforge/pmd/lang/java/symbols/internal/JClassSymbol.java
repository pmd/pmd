/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.List;
import java.util.Optional;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * Represents a class or interface declaration. This is not exactly a type! This corresponds more
 * closely to a Class instance, meaning it can declare type parameters, but not instantiate them,
 * etc. Type definitions will probably use this internally, but they're not equivalent to this.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JClassSymbol extends JAccessibleDeclarationSymbol<ASTAnyTypeDeclaration>, JTypeParameterOwnerSymbol {


    JavaTypeQualifiedName getFqcn();


    Optional<Class<?>> getClassObject();


    List<JClassSymbol> getDeclaredClasses();


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
