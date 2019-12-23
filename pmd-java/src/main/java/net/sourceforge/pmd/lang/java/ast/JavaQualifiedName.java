/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;


/**
 * Unambiguous identifier for a java method or class. This implementation
 * approaches the qualified name format found in stack traces for example,
 * using a custom format specification (see {@link QualifiedNameFactory#ofString(String)}).
 *
 * <p>Instances of this class are immutable. They can be obtained from the
 * factory methods of {@link QualifiedNameFactory}, or from
 * {@link JavaQualifiableNode#getQualifiedName()} on AST nodes that support it.
 *
 * <p>Class qualified names follow the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-13.html#jls-13.1">binary name spec</a>.
 *
 * <p>Method qualified names don't follow a specification but allow to
 * distinguish overloads of the same method, using parameter types and order.
 *
 * @see JavaTypeQualifiedName
 * @see  JavaOperationQualifiedName
 *
 * @since 5.8.1
 * @author Clément Fournier
 *
 * @deprecated This class and subclasses will be removed in PMD 7.0 for
 *             lack of usefulness. JavaQualifiedName cannot be used to
 *             represent unknown entities, because in Java source, the
 *             only thing we can observe are *canonical* names, which
 *             eg don't exist for local classes, and can be ambiguous
 *             between a member class and a package name.
 *
 *             <p>So you can't build a conformant JavaQualifiedName without
 *             having parsed the source file where the entity is. But since
 *             you have parsed the file, you have much better data structures
 *             than JavaQualifiedName to reflect the content of the program:
 *             you have nodes. So we can do away with this class.
 */
@Deprecated
public abstract class JavaQualifiedName implements QualifiedName {

    // toString cache
    private String toString;
    private int hashCode;



    @Override
    public abstract JavaTypeQualifiedName getClassName();

    /**
     * Returns true if this qualified name identifies a
     * local class.
     *
     * @deprecated Use {@link JavaTypeQualifiedName#isLocalClass()}. Will be removed in 7.0.0
     */
    @Deprecated
    public boolean isLocalClass() {
        return getClassName().isLocalClass();
    }


    /**
     * Get the simple name of the class.
     *
     * @deprecated Use {@link JavaTypeQualifiedName#getClassSimpleName()}. Will be removed in 7.0.0
     */
    @Deprecated
    public String getClassSimpleName() {
        return getClassName().getClassSimpleName();
    }


    /**
     * Returns true if the class represented by this
     * qualified name is in the unnamed package.
     *
     * @deprecated Use {@link JavaTypeQualifiedName#isUnnamedPackage()}. Will be removed in 7.0.0
     */
    @Deprecated
    public boolean isUnnamedPackage() {
        return getClassName().isUnnamedPackage();
    }


    /**
     * Returns the operation specific part of the name. It
     * identifies an operation in its namespace. Returns
     * {@code null} if {@link #isOperation()} returns false.
     *
     * @deprecated Use {@link JavaOperationQualifiedName#getOperation()}. Will be removed in 7.0.0
     *
     * @return The operation string, or {@code null}.
     */
    @Deprecated
    public String getOperation() {
        return null; // overridden by JOperationQName
    }

    /**
     * Returns the packages in order.
     *
     * @deprecated Use {@link JavaTypeQualifiedName#getPackageList()} ()}. Will be removed in 7.0.0
     */
    @Deprecated
    public String[] getPackages() {
        return getClassName().getPackageList().toArray(new String[0]);
    }


    /**
     * Returns the classes in order.
     *
     * @deprecated Use {{@link JavaTypeQualifiedName#getClassList()}. Will be removed in 7.0.0
     */
    @Deprecated
    public String[] getClasses() {
        return getClassName().getClassList().toArray(new String[0]);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JavaQualifiedName that = (JavaQualifiedName) o;
        return Objects.equals(toString(), that.toString())
                && structurallyEquals(that);
    }


    /**
     * Returns true if the given qname is identical to this qname.
     * Performs a structural comparison. Used in the implementation
     * of {@link #equals(Object)} after taking shortcuts.
     *
     * @param qname The other comparand. Can always be casted down
     *              to the subclass type in which this method is overridden
     */
    protected abstract boolean structurallyEquals(JavaQualifiedName qname);


    @Override
    public final int hashCode() {
        if (hashCode == 0) {
            hashCode = buildHashCode();
        }
        return hashCode;
    }


    /**
     * Computes the hashcode. Called once, then cached.
     * Since QualifiedNames are mostly used as the keys
     * of a map, caching the hashcode makes sense.
     */
    protected abstract int buildHashCode();


    /**
     * Returns the string representation of this qualified
     * name. The representation follows the format defined
     * for {@link QualifiedNameFactory#ofString(String)}.
     */
    @Override
    public final String toString() {
        // lazy evaluated
        if (toString == null) {
            toString = buildToString();
        }
        return toString;
    }


    /**
     * @deprecated Use {@link QualifiedNameFactory#ofString(String)}. Will be removed in 7.0.0
     */
    @Deprecated
    public static JavaQualifiedName ofString(String name) {
        return QualifiedNameFactory.ofString(name);
    }


    /**
     * @deprecated Use {@link QualifiedNameFactory#ofClass(Class)}. Will be removed in 7.0.0
     */
    @Deprecated
    public static JavaQualifiedName ofClass(Class<?> clazz) {
        return QualifiedNameFactory.ofClass(clazz);
    }


    /**
     * Construct the toString once. Called only once per instance
     */
    protected abstract String buildToString();
}
