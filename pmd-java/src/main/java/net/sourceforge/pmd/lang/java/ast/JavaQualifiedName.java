/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.QualifiedName;


/**
 * Unambiguous identifier for a java method or class. This implementation
 * approaches the qualified name format found in stack traces for example,
 * using a custom format specification (see {@link QualifiedNameFactory#ofString(String)}).
 *
 * <p>Instances of this class are immutable. They can be obtained from the
 * factory methods of this class, or from {@link JavaQualifiableNode#getQualifiedName()}
 * on AST nodes that support it.
 *
 * <p>Class qualified names follow the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-13.html#jls-13.1">binary name spec</a>.
 *
 * <p>Method qualified names don't follow a specification but allow to
 * distinguish overloads of the same method, using parameter types and order.
 */
// TODO split into subclasses for class, field, method
// TODO load a Class<?> from class qname, Method from method qname, etc
public final class JavaQualifiedName implements QualifiedName {


    /** Local index value for when the class is not local. */
    static final int NOTLOCAL_PLACEHOLDER = -1;
    // since we prepend each time, these lists are in the reversed order (innermost elem first).
    // we use ImmutableList.reverse() to get them in their usual, user-friendly order
    // TODO packages is not shared! if we used a dedicated visitor, we could make this happen
    private final ImmutableList<String> packages; // unnamed package == Nil
    private final ImmutableList<String> classes;
    /**
     * Local indices of the parents and of this class, in order.
     * They can be zipped with the {@link #classes} array.
     *
     * <p>If a class is not local, its local index is {@link #NOTLOCAL_PLACEHOLDER}.
     */
    private final ImmutableList<Integer> localIndices;
    private final String operation;
    private final boolean isLambda;
    // toString cache
    private String toString;
    private final int hashCode;


    JavaQualifiedName(ImmutableList<String> packages, ImmutableList<String> classes, ImmutableList<Integer> localIndices, String operation, boolean isLambda) {
        this.packages = packages;
        this.classes = classes;
        this.localIndices = localIndices;
        this.operation = operation;
        this.isLambda = isLambda;
        this.hashCode= Objects.hash(packages, classes, localIndices, operation, isLambda);
    }


    @Override
    public boolean isClass() {
        return !classes.isEmpty() && operation == null;
    }


    @Override
    public boolean isOperation() {
        return operation != null;
    }


    /**
     * Returns true if this qualified name identifies a lambda expression.
     */
    public boolean isLambda() {
        return isLambda;
    }


    /**
     * Returns true if this qualified name identifies a
     * local class.
     */
    public boolean isLocalClass() {
        return localIndices.head() != NOTLOCAL_PLACEHOLDER;
    }


    /**
     * Returns true if this qualified name identifies an
     * anonymous class.
     */
    public boolean isAnonymousClass() {
        return !isLocalClass() && StringUtils.isNumeric(getClassSimpleName());
    }


    /**
     * Get the simple name of the class.
     */
    public String getClassSimpleName() {
        return classes.head();
    }


    /**
     * Returns true if the class represented by this
     * qualified name is in the unnamed package.
     */
    public boolean isUnnamedPackage() {
        return packages.isEmpty();
    }


    /**
     * Returns the packages in outer-to-inner order. This
     * is specific to Java's package structure. If the
     * outer class is in the unnamed package, returns an
     * empty list.
     *
     * <p>{@literal @NotNull}
     *
     * @return The packages.
     */
    public ImmutableList<String> getPackages() {
        return packages.reverse();
    }


    /**
     * Returns the class specific part of the name. It
     * identifies a class in the namespace it's declared
     * in. If the class is nested inside another, then
     * the list returned contains all enclosing classes
     * in order, from outermost to innermost.
     *
     * <p>{@literal @NotNull}
     *
     * @return The class names.
     */
    public ImmutableList<String> getClasses() {
        return classes.reverse();
    }


    /**
     * Returns the operation specific part of the name. It
     * identifies an operation in its namespace. Returns
     * {@code null} if {@link #isOperation()} returns false.
     *
     * @return The operation string, or {@code null}.
     */
    public String getOperation() {
        return operation;
    }


    @Override
    public JavaQualifiedName getClassName() {
        if (isClass()) {
            return this;
        }

        return new JavaQualifiedName(packages, classes, localIndices, null, false);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JavaQualifiedName that = (JavaQualifiedName) o;
        return Objects.equals(toString(), that.toString())
                && isLambda == that.isLambda
                && Objects.equals(operation, that.operation)
                && Objects.equals(packages, that.packages)
                && Objects.equals(classes, that.classes)
                && Objects.equals(localIndices, that.localIndices);
    }


    @Override
    public int hashCode() {
        return hashCode;
    }


    /**
     * Returns the string representation of this qualified
     * name. The representation follows the format defined
     * for {@link QualifiedNameFactory#ofString(String)}.
     */
    @Override
    public String toString() {
        // lazy evaluated
        if (toString == null) {
            toString = buildToString();
        }
        return toString;
    }


    // Construct the toString. Called only once per instance
    private String buildToString() {
        StringBuilder sb = new StringBuilder();

        for (String aPackage : packages.reverse()) {
            sb.append(aPackage).append('.');
        }

        // this in the normal order
        ImmutableList<String> reversed = classes.reverse();
        sb.append(reversed.head());
        for (Entry<String, Integer> classAndLocalIdx : reversed.tail().zip(localIndices.reverse().tail())) {
            sb.append('$');

            if (classAndLocalIdx.getValue() != NOTLOCAL_PLACEHOLDER) {
                sb.append(classAndLocalIdx.getValue());
            }

            sb.append(classAndLocalIdx.getKey());
        }

        if (isOperation()) {
            sb.append('#').append(operation);
        }

        return sb.toString();
    }

    // These factories are kept here because getClasses and getPackages return
    // a reversed list. Not calling them from QualifiedNameFactory avoids a
    // reverse operation

    /**
     * Builds the name of an operation from its parent and operation string.
     *
     * @param parent    Qualified name of the parent class
     * @param operation Operation name
     *
     * @return A new qualified name
     */
    static JavaQualifiedName operationName(JavaQualifiedName parent, String operation, boolean isLambda) {
        return new JavaQualifiedName(parent.packages, parent.classes, parent.localIndices, operation, isLambda);
    }


    /**
     * Builds the name of a class that's not in the outer level (that is, it's
     * either local, anonymous, nested or inner).
     *
     * @param parent     Qualified name of the parent class
     * @param className  Name of the new class
     * @param localIndex Locality index
     *
     * @return A new qualified name
     */
    static JavaQualifiedName notOuterClassName(JavaQualifiedName parent, String className, int localIndex) {
        return new JavaQualifiedName(parent.packages,
                                     parent.classes.prepend(className),
                                     parent.localIndices.prepend(localIndex),
                                     null,
                                     false);
    }

}
