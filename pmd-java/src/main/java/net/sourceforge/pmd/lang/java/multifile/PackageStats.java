/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


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
    /* default */ ClassStats getClassStats(JavaTypeQualifiedName qname, boolean createIfNotFound) {
        PackageStats container = getSubPackage(qname, createIfNotFound);

        if (container == null) {
            return null;
        }

        String topClassName = qname.getClassList().get(0);
        if (createIfNotFound && container.classes.get(topClassName) == null) {
            container.classes.put(topClassName, new ClassStats());
        }

        ClassStats next = container.classes.get(topClassName);

        if (next == null) {
            return null;
        }

        Iterator<String> it = qname.getClassList().iterator();
        if (it.hasNext()) {
            it.next();
        }

        while (it.hasNext() && next != null) {
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
    private PackageStats getSubPackage(JavaTypeQualifiedName qname, boolean createIfNotFound) {
        if (qname.getPackageList().isEmpty()) {
            return this; // the toplevel
        }

        List<String> packagePath = qname.getPackageList();
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
    public boolean hasMatchingSig(JavaOperationQualifiedName qname, JavaOperationSigMask sigMask) {
        ClassStats clazz = getClassStats(qname.getClassName(), false);

        return clazz != null && clazz.hasMatchingOpSig(qname.getOperation(), sigMask);
    }


    @Override
    public boolean hasMatchingSig(JavaTypeQualifiedName qname, String fieldName, JavaFieldSigMask sigMask) {
        ClassStats clazz = getClassStats(qname, false);

        return clazz != null && clazz.hasMatchingFieldSig(fieldName, sigMask);
    }


    @Override
    public ClassMirror getClassMirror(JavaTypeQualifiedName className) {
        return getClassStats(className, false);
    }

}
