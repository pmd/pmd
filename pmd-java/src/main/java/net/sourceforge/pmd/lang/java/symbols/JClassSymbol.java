/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymEnum;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;


/**
 * Abstraction over a {@link Class} instance. This is not a type, it's
 * the *declaration* of a type. For example, a class symbol representing
 * a generic class can provide access to the formal type parameters, but
 * the symbol does not represent a specific parametrization of a type.
 *
 * <p>Class symbols represent the full range of types represented by {@link Class}:
 * classes, interfaces, arrays, and primitives. This excludes type variables,
 * intersection types, parameterized types, wildcard types, etc., which are only
 * compile-time constructs.
 *
 * <p>Class symbols are used to back {@link JClassType}, {@link JArrayType},
 * and {@link JPrimitiveType}. See {@link JTypeMirror#getSymbol()}.
 *
 * @since 7.0.0
 */
public interface JClassSymbol extends JTypeDeclSymbol,
                                      JTypeParameterOwnerSymbol,
                                      BoundToNode<ASTAnyTypeDeclaration> {


    /**
     * Returns the binary name of this type, as specified by the JLS:
     * <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.1">the JLS</a>.
     * For array types this returns the binary name of the component followed by "[]".
     * This differs from {@link Class#getName()}, which for array types outputs an
     * <i>internal name</i>.
     *
     * <p>For example:
     * <pre>{@code
     * int.class.getName() == "int"
     * int[].class.getName() == "[I"
     * String.class.getName() == "java.lang.String"
     * String[].class.getName() == "[Ljava.lang.String;"
     * }</pre>
     * whereas
     * <pre>{@code
     * symbolOf(int.class).getBinaryName() == "int"
     * symbolOf(int[].class).getBinaryName() == "int[]"
     * symbolOf(String.class).getBinaryName() == "java.lang.String"
     * symbolOf(String[].class).getBinaryName() == "java.lang.String[]"
     * }</pre>
     */
    @NonNull
    String getBinaryName();


    /**
     * Returns the simple name of this class, as specified by
     * {@link Class#getCanonicalName()}.
     */
    @Nullable
    String getCanonicalName();


    /**
     * Returns the method or constructor this symbol is declared in, if
     * it represents a {@linkplain #isLocalClass() local class declaration}.
     *
     * <p>Notice, that this returns null also if this class is local to
     * a class or instance initializer.
     */
    @Nullable JExecutableSymbol getEnclosingMethod();

    @Override
    default JTypeParameterOwnerSymbol getEnclosingTypeParameterOwner() {
        JExecutableSymbol enclosingMethod = getEnclosingMethod();
        return enclosingMethod != null ? enclosingMethod : getEnclosingClass();
    }


    /**
     * Returns the member classes declared directly in this class.
     *
     * @see Class#getDeclaredClasses()
     */
    List<JClassSymbol> getDeclaredClasses();


    /** Returns a class with the given name defined in this class. */
    @Nullable
    default JClassSymbol getDeclaredClass(String name) {
        for (JClassSymbol klass : getDeclaredClasses()) {
            if (klass.nameEquals(name)) {
                return klass;
            }
        }
        return null;
    }


    /**
     * Returns the methods declared directly in this class.
     * <i>This excludes bridges and other synthetic methods.</i>
     *
     * <p>For an array type T[], to the difference of {@link Class},
     * this method returns a one-element list with the {@link Object#clone()}
     * method, as if declared like so: {@code public final T[] clone() {...}}.
     *
     * @see Class#getDeclaredMethods()
     */
    List<JMethodSymbol> getDeclaredMethods();


    /**
     * Returns the constructors declared by this class.
     * <i>This excludes synthetic constructors.</i>
     *
     * <p>For an array type T[], and to the difference of {@link Class},
     * this should return a one-element list with a constructor
     * having the same modifiers as the array type, and a single
     * {@code int} parameter.
     *
     * @see Class#getDeclaredConstructors()
     */
    List<JConstructorSymbol> getConstructors();


    /**
     * Returns the fields declared directly in this class.
     * <i>This excludes synthetic fields.</i>
     *
     * <p>For arrays, and to the difference of {@link Class},
     * this should return a one-element list with the
     * {@code public final int length} field.
     *
     * @see Class#getDeclaredFields()
     */
    List<JFieldSymbol> getDeclaredFields();


    /** Returns a field with the given name defined in this class. */
    @Nullable
    default JFieldSymbol getDeclaredField(String name) {
        for (JFieldSymbol field : getDeclaredFields()) {
            if (field.nameEquals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns a list with all enum constants. If this symbol does
     * not represent an enum, returns an empty set. The returned list
     * is a subset of {@link #getDeclaredFields()}. The order of fields
     * denotes the normal order of enum constants.
     */
    default @NonNull List<JFieldSymbol> getEnumConstants() {
        return Collections.emptyList();
    }


    /** Returns the list of super interface types, under the given substitution. */
    List<JClassType> getSuperInterfaceTypes(Substitution substitution);


    /** Returns the superclass type, under the given substitution. */
    @Nullable JClassType getSuperclassType(Substitution substitution);


    /**
     * Returns the superclass symbol if it exists. Returns null if this
     * class represents the class {@link Object}, or a primitive type.
     * If this symbol is an interface, returns the symbol for {@link Object}.
     */
    @Nullable
    JClassSymbol getSuperclass();


    /** Returns the direct super-interfaces of this class or interface symbol. */
    List<JClassSymbol> getSuperInterfaces();


    default boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }


    /** Returns the component symbol, returns null if this is not an array. */
    @Nullable
    JTypeDeclSymbol getArrayComponent();


    boolean isArray();

    boolean isPrimitive();

    boolean isEnum();

    boolean isRecord();

    boolean isAnnotation();

    boolean isLocalClass();

    boolean isAnonymousClass();

    /**
     * Return the simple names of all annotation attributes. If this
     * is not an annotation type, return an empty set.
     */
    default PSet<String> getAnnotationAttributeNames() {
        return HashTreePSet.empty();
    }

    /**
     * Return the default value of the attribute if this is an annotation type
     * with a default. Return null if this is not an annotation type, if there
     * is no such attribute, or the attribute has no default value. If the name
     * is in the {@linkplain  #getAnnotationAttributeNames() attribute name set},
     * then the null return value can only mean that the attribute exists but has
     * no default value.
     *
     * @param attrName Attribute name
     */
    default @Nullable SymbolicValue getDefaultAnnotationAttributeValue(String attrName) {
        if (!isAnnotation()) {
            return null;
        }
        for (JMethodSymbol m : getDeclaredMethods()) {
            if (m.nameEquals(attrName) && m.isAnnotationAttribute()) {
                return m.getDefaultAnnotationValue(); // nullable
            }
        }
        return null;
    }

    /**
     * Returns the retention policy of this annotation, if this is an
     * annotation symbol. Otherwise returns null.
     */
    default @Nullable RetentionPolicy getAnnotationRetention() {
        if (!isAnnotation()) {
            return null;
        }
        return Optional.ofNullable(getDeclaredAnnotation(Retention.class))
                       .map(annot -> annot.getAttribute("value"))
                       .filter(value -> value instanceof SymEnum)
                       .map(value -> ((SymEnum) value).toEnum(RetentionPolicy.class))
                       .orElse(RetentionPolicy.CLASS);
    }

    /**
     * Return whether annotations of this annotation type apply to the
     * given construct, as per the {@link Target} annotation. Return
     * false if this is not an annotation.
     */
    default boolean annotationAppliesTo(ElementType elementType) {
        if (!isAnnotation()) {
            return false;
        }
        SymAnnot target = getDeclaredAnnotation(Target.class);
        if (target == null) {
            // If an @Target meta-annotation is not present on an annotation type T,
            // then an annotation of type T may be written as a modifier
            // for any declaration except a type parameter declaration.
            return elementType != ElementType.TYPE_PARAMETER;
        }
        return target.attributeContains("value", elementType).isTrue();
    }

    // todo isSealed + getPermittedSubclasses
    //  (isNonSealed is not so useful I think)

    /**
     * This returns true if this is not an interface, primitive or array.
     */
    default boolean isClass() {
        return !isInterface() && !isArray() && !isPrimitive();
    }


    /**
     * Returns the toplevel class containing this class. If this is a
     * toplevel class, returns this.
     */
    default @NonNull JClassSymbol getNestRoot() {
        JClassSymbol e = this;
        while (e.getEnclosingClass() != null) {
            e = e.getEnclosingClass();
        }
        return e;
    }


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitClass(this, param);
    }
}
