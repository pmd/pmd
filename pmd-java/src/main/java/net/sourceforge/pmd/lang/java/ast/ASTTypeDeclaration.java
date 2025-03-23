/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.JModifier.ABSTRACT;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;


/**
 * Groups class, enum, record, annotation and interface declarations under a common
 * supertype.
 *
 * <pre class="grammar">
 *
 * TypeDeclaration ::= {@link ASTClassDeclaration ClassDeclaration}
 *                   | {@link ASTAnonymousClassDeclaration AnonymousClassDeclaration}
 *                   | {@link ASTEnumDeclaration EnumDeclaration}
 *                   | {@link ASTAnnotationTypeDeclaration AnnotationTypeDeclaration}
 *                   | {@link ASTRecordDeclaration RecordDeclaration}
 *                   | {@link ASTImplicitClassDeclaration ImplicitClassDeclaration}
 *
 * </pre>
 *
 * <p>Note: In PMD 6, there was a node with this name (ASTTypeDeclaration) which was a top-level wrapper
 * node around type declarations. This node has been removed in PMD 7 and the name has been reused.
 */
public interface ASTTypeDeclaration
    extends TypeNode,
        ModifierOwner,
            TypeParamOwnerNode,
            ASTBodyDeclaration,
            ASTTopLevelDeclaration,
            JavadocCommentOwner {

    @Override
    @NonNull
    JClassSymbol getSymbol();


    /**
     * Returns the {@linkplain JClassType#getGenericTypeDeclaration() generic type declaration}
     * of the declared type. Note that for {@linkplain ASTAnonymousClassDeclaration anonymous classes},
     * this returns a class type whose symbol is the actual anonymous
     * class. Eg {@code new Runnable() { void foo() { } void run() { } }}
     * would present both methods, ie not just be a {@code Runnable}.
     * The {@link ASTConstructorCall} would have type {@code Runnable}
     * though, not the anonymous class.
     */
    @Override
    @NonNull JClassType getTypeMirror();


    /**
     * Returns the simple name of this type declaration. Returns the
     * empty string if this is an anonymous class declaration.
     */
    @NonNull
    String getSimpleName();

    /**
     * Returns the name of the package in which this class is declared.
     */
    default String getPackageName() {
        return getRoot().getPackageName();
    }


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
    String getCanonicalName();


    /**
     * Returns true if this is an abstract type. Interfaces and annotations
     * types are implicitly abstract.
     */
    default boolean isAbstract() {
        return hasModifiers(ABSTRACT);
    }

    /**
     * Returns true if this type is static. Only inner types can be static.
     */
    default boolean isStatic() {
        return hasModifiers(JModifier.STATIC);
    }

    default boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }

    /**
     * Returns the enum constants declared by this enum. If this is not
     * an enum declaration, returns an empty stream.
     */
    default NodeStream<ASTEnumConstant> getEnumConstants() {
        return firstChild(ASTEnumBody.class).children(ASTEnumConstant.class);
    }


    /**
     * Returns the record components declared by this class. If this is not
     * a record declaration, returns null.
     */
    default @Nullable ASTRecordComponentList getRecordComponents() {
        return null;
    }


    /**
     * Retrieves the member declarations (fields, methods, classes, etc.)
     * from the body of this type declaration.
     */
    default NodeStream<ASTBodyDeclaration> getDeclarations() {
        return getBody().toStream();
    }

    /**
     * Returns the declarations of a particular type.
     *
     * @param klass   Type of the declarations
     * @param <T>Type of the declarations
     */
    default <T extends ASTBodyDeclaration> NodeStream<T> getDeclarations(Class<? extends T> klass) {
        return getDeclarations().filterIs(klass);
    }


    /**
     * Returns the operations declared in this class (methods and constructors).
     */
    default NodeStream<ASTExecutableDeclaration> getOperations() {
        return getDeclarations().filterIs(ASTExecutableDeclaration.class);
    }


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
     * Returns true if this is an {@linkplain ASTImplicitClassDeclaration implicit class declaration}.
     */
    default boolean isUnnamedToplevelClass() {
        return this instanceof ASTImplicitClassDeclaration;
    }

    /**
     * Returns true if this is an {@linkplain ASTEnumDeclaration enum class declaration}.
     */
    default boolean isEnum() {
        return this instanceof ASTEnumDeclaration;
    }

    /**
     * Returns true if this is an {@linkplain ASTRecordDeclaration record class declaration}.
     */
    default boolean isRecord() {
        return this instanceof ASTRecordDeclaration;
    }

    /**
     * Returns true if this is an interface type declaration (including
     * annotation types). This is consistent with {@link Class#isInterface()}.
     */
    default boolean isInterface() {
        return false;
    }

    /**
     * Returns true if this is a regular class declaration (not an enum,
     * not a record, not an interface or annotation, not an implicit class).
     * Note that eg {@link JClassSymbol#isClass()} counts records and enums in, just
     * like {@link #isInterface()} counts annotations in.
     */
    default boolean isRegularClass() {
        return false;
    }


    /**
     * Returns true if this is a regular interface declaration (not an annotation).
     * Note that {@link #isInterface()} counts annotations in.
     */
    default boolean isRegularInterface() {
        return false;
    }


    /** Returns true if this is an {@linkplain ASTAnnotationTypeDeclaration annotation type declaration}. */
    default boolean isAnnotation() {
        return this instanceof ASTAnnotationTypeDeclaration;
    }


    /**
     * Returns the list of interfaces implemented by this class, or
     * extended by this interface. Returns null if no such list is declared.
     */
    default @NonNull NodeStream<ASTClassType> getSuperInterfaceTypeNodes() {
        return ASTList.orEmptyStream(isInterface() ? firstChild(ASTExtendsList.class)
                                                   : firstChild(ASTImplementsList.class));
    }

    /**
     * Returns the superclass type node if this node is a class
     * declaration and explicitly declares an {@code extends}
     * clause. Superinterfaces of an interface are not considered.
     * Note that enum or record declarations never have an explicit
     * superclass type node. Anonymous class declarations have such
     * a type node if the class being created is a class, otherwise,
     * it is an interface, and the superclass is implicitly object,
     * so this method returns null. Enum constants with a subclass
     * body will return null.
     */
    default @Nullable ASTClassType getSuperClassTypeNode() {
        if (isInterface()) {
            return null;
        }
        return ASTList.singleOrNull(firstChild(ASTExtendsList.class));
    }

    /**
     * Return the explicit permits list if there is one. Note
     * that the permitted subtypes list may be implicit and inferred
     * from subtypes found in the current compilation unit. Use
     * {@link JClassSymbol#getPermittedSubtypes()} for an API
     * that works in all cases.
     */
    default @Nullable ASTPermitsList getPermitsClause() {
        return firstChild(ASTPermitsList.class);
    }
}
