/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JModifier.ABSTRACT;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * Groups enum, class, annotation and interface declarations under a common
 * supertype.
 */
public interface ASTAnyTypeDeclaration extends TypeNode, JavaQualifiableNode, AccessNode, FinalizableNode {


    /**
     * @deprecated Use {@link #getBinaryName()}
     */
    @Override
    @Deprecated
    JavaTypeQualifiedName getQualifiedName();



    /**
     * Returns the simple name of this type declaration. Returns null
     * if this is an anonymous class declaration.
     */
    @Nullable
    default String getSimpleName() {
        return getImage();
    }


    /**
     * @deprecated Use {@link #getSimpleName()}
     */
    @Deprecated
    @Override
    String getImage();


    /**
     * Returns the binary name of this type declaration. This
     * is like {@link Class#getName()}.
     */
    // @NotNull
    String getBinaryName();

    /**
     * Returns true if this is an abstract type. Interfaces and annotations
     * types are implicitly abstract.
     */
    @Override
    default boolean isAbstract() {
        return hasModifiers(ABSTRACT);
    }


    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     *
     * @deprecated See {@link TypeKind}
     */
    @Deprecated
    TypeKind getTypeKind();


    /**
     * Returns the enum constants declared by this enum. If this is not
     * an enum declaration, returns an empty stream.
     */
    default NodeStream<ASTEnumConstant> getEnumConstants() {
        return getFirstChildOfType(ASTEnumBody.class).children(ASTEnumConstant.class);
    }


    /**
     * Retrieves the member declarations (fields, methods, classes, etc.) from the body of this type declaration.
     *
     * @return The member declarations declared in this type declaration
     */
    default List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getBody().children(ASTAnyTypeBodyDeclaration.class).toList();
    }


    /**
     * Returns the body of this type declaration.
     */
    default ASTTypeBody getBody() {
        return (ASTTypeBody) getLastChild();
    }

    default List<ASTTypeParameter> getTypeParameters() {
        ASTTypeParameters parameters = getFirstChildOfType(ASTTypeParameters.class);
        if (parameters == null) {
            return Collections.emptyList();
        }

        return parameters.asList();
    }

    /**
     * Returns true if this type declaration is nested inside an interface,
     * class or annotation.
     */
    default boolean isNested() {
        return getParent() instanceof ASTAnyTypeBodyDeclaration;
    }


    /**
     * Returns true if the class is declared inside a block other
     * than the body of another class, or the top level. Anonymous
     * classes are not considered local. Only class declarations
     * can be local. Local classes cannot be static.
     */
    default boolean isLocal() {
        return getParent() instanceof ASTLocalClassStatement;
    }



    /**
     * Returns true if this type is declared at the top-level of a file.
     */
    default boolean isTopLevel() {
        return getParent() instanceof ASTTypeDeclaration;
    }


    /**
     * Returns true if this is an {@linkplain ASTAnonymousClassDeclaration anonymous class declaration}.
     */
    default boolean isAnonymous() {
        return this instanceof ASTAnonymousClassDeclaration;
    }


    /**
     * Returns true if this is an {@linkplain ASTEnumDeclaration enum class declaration}.
     */
    default boolean isEnum() {
        return this instanceof ASTEnumDeclaration;
    }

    /**
     * Returns true if this is an interface type declaration (including
     * annotation types). This is consistent with {@link Class#isInterface()}.
     */
    default boolean isInterface() {
        return false;
    }


    /** Returns true if this is an {@linkplain ASTAnnotationTypeDeclaration annotation type declaration}. */
    default boolean isAnnotation() {
        return this instanceof ASTAnnotationTypeDeclaration;
    }



    /**
     * The kind of type this node declares.
     *
     * @deprecated This is not useful, not adapted to the problem, and
     *     does not scale to changes in the Java language. The only use
     *     of this is to get a name, this can be replaced with {@link PrettyPrintingUtil}.
     *
     *     <p>Besides, the real problem is that
     *     <ul>
     *         <li>enums are also classes
     *         <li>annotations are also interfaces
     *         <li>there are also anonymous classes in PMD 7.0, so this
     *         cannot even be used to downcast safely
     *     </ul>
     *     We can also expect new kinds of type declarations (eg records)
     *     in the future, which will force us to add new constants and aggravates
     *     the problem.
     *
     *     Ultimately, dividing "kinds" with an enum is not adapted.
     *
     *     Same problem with {@link ASTAnyTypeBodyDeclaration.DeclarationKind}
     */
    @Deprecated
    enum TypeKind {
        CLASS, INTERFACE, ENUM, ANNOTATION
    }

}
