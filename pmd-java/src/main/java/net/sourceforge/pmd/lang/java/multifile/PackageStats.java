/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ImmutableList;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;


/**
 * Statistics about a package. This recursive data structure mirrors the package structure of the analysed project and
 * stores information about the classes and subpackages it contains. This object provides signature matching utilities
 * to metrics.
 *
 * @author Clément Fournier
 * @see ClassStats
 * @since 6.0.0
 */
final class PackageStats implements ProjectMirror {

    static final PackageStats INSTANCE = new PackageStats();

    private final Map<String, PackageStats> subPackages = new HashMap<>();
    private final Map<String, ClassStats> classes = new HashMap<>();


    /**
     * Default constructor.
     */
    private PackageStats() {
    }


    /**
     * Resets the entire data structure.
     */
    /* default */ void reset() {
        subPackages.clear();
        classes.clear();
    }


    /**
     * Gets the ClassStats corresponding to the named resource. The class can be nested. If the createIfNotFound
     * parameter is set, the method also creates the hierarchy if it doesn't exist.
     *
     * @param qname            The qualified name of the class
     * @param createIfNotFound Create hierarchy if missing
     *
     * @return The new ClassStats, or the one that was found. Can return null only if createIfNotFound is unset
     */
    /* default */ ClassStats getClassStats(JavaQualifiedName qname, boolean createIfNotFound) {
        PackageStats container = getSubPackage(qname, createIfNotFound);

        if (container == null) {
            return null;
        }

        String topClassName = qname.getClassList().head();
        if (createIfNotFound && container.classes.get(topClassName) == null) {
            container.classes.put(topClassName, new ClassStats());
        }

        ClassStats next = container.classes.get(topClassName);

        if (next == null) {
            return null;
        }

        ImmutableList<String> nameClasses = qname.getClassList();

        for (Iterator<String> it = nameClasses.tail().iterator(); it.hasNext() && next != null;) {
            // Delegate search for nested classes to ClassStats
            next = next.getNestedClassStats(it.next(), createIfNotFound);
        }

        return next;
    }


    /**
     * Returns the deepest PackageStats that contains the named resource. If the second parameter is set, creates the
     * missing PackageStats along the way.
     *
     * @param qname            The qualified name of the resource
     * @param createIfNotFound If set to true, the hierarch is created if missing
     *
     * @return The deepest package that contains this resource. Can only return null if createIfNotFound is unset
     */
    private PackageStats getSubPackage(JavaQualifiedName qname, boolean createIfNotFound) {
        if (qname.getPackageList() == null) {
            return this; // the toplevel
        }

        ImmutableList<String> packagePath = qname.getPackageList();
        PackageStats next = this;

        for (Iterator<String> it = packagePath.iterator(); it.hasNext() && next != null;) {
            String currentPackage = it.next();
            if (createIfNotFound && next.subPackages.get(currentPackage) == null) {
                next.subPackages.put(currentPackage, new PackageStats());
            }

            next = next.subPackages.get(currentPackage);
        }

        return next;
    }


    @Override
    public boolean hasMatchingSig(JavaQualifiedName qname, JavaOperationSigMask sigMask) {
        ClassStats clazz = getClassStats(qname, false);

        return clazz != null && clazz.hasMatchingOpSig(qname.getOperation(), sigMask);
    }


    @Override
    public boolean hasMatchingSig(JavaQualifiedName qname, String fieldName, JavaFieldSigMask sigMask) {
        ClassStats clazz = getClassStats(qname, false);

        return clazz != null && clazz.hasMatchingFieldSig(fieldName, sigMask);
    }


    @Override
    public ClassMirror getClassMirror(JavaQualifiedName className) {
        return getClassStats(className, false);
    }

}
