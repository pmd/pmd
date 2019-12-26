/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;


/**
 * Specialises {@link JavaQualifiedName} for type names.
 *
 * @author Clément Fournier
 * @since 6.1.0
 *
 * @deprecated See {@link JavaQualifiedName}
 */
@Deprecated
public final class JavaTypeQualifiedName extends JavaQualifiedName {

    /** Local index value for when the class is not local. */
    static final int NOTLOCAL_PLACEHOLDER = -1;

    // since we prepend each time, these lists are in the reversed order (innermost elem first).
    // we use ImmutableList.reverse() to get them in their usual, user-friendly order

    private final ImmutableList<String> packages; // unnamed package == Nil
    private final ImmutableList<String> classes;

    /**
     * Local indices of the parents and of this class, in order.
     * They can be zipped with the {@link #classes} list.
     *
     * <p>If a class is not local, its local index is {@link #NOTLOCAL_PLACEHOLDER}.
     */
    private final ImmutableList<Integer> localIndices;

    private Class<?> representedType;
    private boolean typeLoaded;

    private final ClassLoader classLoader;

    JavaTypeQualifiedName(ImmutableList<String> packages, ImmutableList<String> classes, ImmutableList<Integer> localIndices, ClassLoader classLoader) {
        Objects.requireNonNull(packages);
        Objects.requireNonNull(classes);
        Objects.requireNonNull(localIndices);

        if (classes.isEmpty() || localIndices.size() != classes.size()) {
            throw new IllegalArgumentException("Error building a type qualified name");
        }

        this.packages = packages;
        this.classes = classes;
        this.localIndices = localIndices;

        this.classLoader = classLoader; // classLoader may be null
    }


    @Override
    public JavaTypeQualifiedName getClassName() {
        return this;
    }


    @Override
    protected boolean structurallyEquals(JavaQualifiedName qname) {
        JavaTypeQualifiedName that = (JavaTypeQualifiedName) qname;
        return this.packages.equals(that.packages)
                && this.classes.equals(that.classes)
                && this.localIndices.equals(that.localIndices);
    }


    @Override
    protected int buildHashCode() {
        return Objects.hash(packages, classes, localIndices);
    }


    @Override
    public boolean isClass() {
        return true;
    }


    @Override
    public boolean isOperation() {
        return false;
    }


    /**
     * Returns true if this qualified name identifies a
     * local class.
     */
    @Override
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
    @Override
    public String getClassSimpleName() {
        return classes.head();
    }


    /**
     * Returns true if the class represented by this
     * qualified name is in the unnamed package.
     */
    @Override
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
    public List<String> getPackageList() {
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
    public List<String> getClassList() {
        return classes.reverse();
    }


    /**
     * Gets the Class instance identified by this qualified name.
     *
     * @return A class instance, or null if the classloader threw a {@link ClassNotFoundException}
     *     or {@link LinkageError} while trying to load the class.
     */
    public Class<?> getType() {
        synchronized (this) {
            if (!typeLoaded) {
                typeLoaded = true;
                try {
                    representedType = loadType();
                } catch (ClassNotFoundException | LinkageError e) {
                    representedType = null;
                    //TODO: report missing/broken type in auxclasspath
                }
            }
            return representedType;
        }
    }


    /**
     * Gets the Class instance identified by this qualified name.
     *
     * @return A class instance
     *
     * @throws ClassNotFoundException if the class is not found
     */
    private Class<?> loadType() throws ClassNotFoundException {
        if (classLoader != null) {
            // hence why the toString should follow binary name specification
            return classLoader.loadClass(getBinaryName());
        }
        return null;
    }


    /**
     * Returns the binary name of the type identified by this qualified name.
     * The binary name can be used to load a {@link Class} using a {@link ClassLoader}.
     * Contrary to this method, {@link #toString()} is not guaranteed to return
     * a binary name. For most purposes, you should avoid using this method
     * directly and use {@link #getType()} instead. Just don't build a
     * dependency on the toString if you want a binary name.
     *
     * @return The binary name of the type identified by this qualified name
     */
    public String getBinaryName() {
        return toString();
    }


    @Override
    protected String buildToString() {
        StringBuilder sb = new StringBuilder();

        for (String aPackage : packages.reverse()) {
            sb.append(aPackage).append('.');
        }

        // this in the normal order
        ImmutableList<String> reversed = classes.reverse();
        sb.append(reversed.head());
        for (Entry<String, Integer> classAndLocalIdx : reversed.tail().zip(localIndices.reverse().tail())) {
            sb.append('$');

            if (classAndLocalIdx.getValue() != JavaTypeQualifiedName.NOTLOCAL_PLACEHOLDER) {
                sb.append(classAndLocalIdx.getValue());
            }

            sb.append(classAndLocalIdx.getKey());
        }

        return sb.toString();
    }
}
