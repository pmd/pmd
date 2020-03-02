/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JModifier.ABSTRACT;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;


/**
 * Groups class, enum, record, annotation and interface declarations under a common
 * supertype.
 */
public interface ASTAnyTypeDeclaration
    extends TypeNode,
            JavaQualifiableNode,
            AccessNode,
            TypeParamOwnerNode,
            ASTBodyDeclaration,
            ASTTopLevelDeclaration,
            FinalizableNode {

    @Override
    @NonNull
    JClassSymbol getSymbol();

    /**
     * Returns the simple name of this type declaration. Returns the
     * empty string if this is an anonymous class declaration.
     */
    @NonNull
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
    @NonNull
    String getBinaryName();


    /**
     * Returns the canonical name of this class, if it exists.
     * Otherwise returns null. This is like {@link Class#getCanonicalName()}.
     *
     * <p>A canonical name exists if all enclosing types have a
     * canonical name, and this is neither a local class nor an
     * anonymous class. For example:
     *
     * <pre>{@code
     * package p;
     *
     * public class A { // p.A
     *     class M { // p.A.M
     *         {
     *             class Local { // null, local class
     *                class M2 {} // null, member of a local class
     *             }
     *
     *             new Local() { // null, anonymous class
     *                class M2 {} // null, member of an anonymous class
     *             };
     *         }
     *     }
     *
     * }
     * }</pre>
     *
     *
     * So non-local/anonymous classes declared
     * somewhere in a local/anonymous class also have no loc
     */
    @Nullable
    default String getCanonicalName() {
        if (isAnonymous() || isLocal()) {
            return null;
        }

        ASTAnyTypeDeclaration encl = getEnclosingType();
        if (encl == null) {
            return getBinaryName(); // toplevel
        }

        String enclCanon = encl.getCanonicalName();
        return enclCanon == null ? null : enclCanon + '.' + getSimpleName();
    }


    /**
     * Returns true if this is an abstract type. Interfaces and annotations
     * types are implicitly abstract.
     */
    @Override
    default boolean isAbstract() {
        return hasModifiers(ABSTRACT);
    }


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
    default NodeStream<ASTBodyDeclaration> getDeclarations() {
        return getBody().getDeclarations();
    }


    /**
     * @deprecated Use {@link #getBinaryName()}
     */
    @Override
    @Deprecated
    JavaTypeQualifiedName getQualifiedName();

    /**
     * Returns the body of this type declaration.
     */
    default ASTTypeBody getBody() {
        return (ASTTypeBody) getLastChild();
    }

    /**
     * Returns true if this type declaration is nested inside an interface,
     * class or annotation.
     */
    default boolean isNested() {
        return getParent() instanceof ASTTypeBody;
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
        return getParent() instanceof ASTCompilationUnit;
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
     * Returns the interfaces implemented by this class, or
     * extended by this interface. Returns an empty list if
     * none is specified.
     */
    default List<ASTClassOrInterfaceType> getSuperInterfaces() {

        Iterable<ASTClassOrInterfaceType> it = isInterface()
                                               ? getFirstChildOfType(ASTExtendsList.class)
                                               : getFirstChildOfType(ASTImplementsList.class);

        return it == null ? Collections.emptyList() : IteratorUtil.toList(it.iterator());
    }
}
