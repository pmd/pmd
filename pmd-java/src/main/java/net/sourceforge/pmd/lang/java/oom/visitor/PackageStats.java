/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.QualifiedName;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.Metrics;


/**
 * Package statistics. This recursive data structure mirrors the package structure of the analysed
 * project and stores information about the classes and subpackages it contains.
 *
 * @author Clément Fournier
 * @see ClassStats
 */
public class PackageStats {

    private Map<String, PackageStats> subPackages = new HashMap<>();
    private Map<String, ClassStats> classes = new HashMap<>();

    public PackageStats() {

    }


    /**
     * Gets the ClassStats corresponding to the named resource. The class can be nested. If the
     * createIfNotFound parameter is set, the method also creates the hierarchy if it doesn't exist.
     *
     * @param qname            The qualified name of the class
     * @param createIfNotFound Create hierarchy if missing
     *
     * @return The new ClassStats, or the one that was found. Can return null only if
     * createIfNotFound is unset.
     */
    public ClassStats getClassStats(QualifiedName qname, boolean createIfNotFound) {
        PackageStats container = getSubPackage(qname, createIfNotFound);

        if (container == null) {
            return null;
        }

        String topClassName = qname.getClasses()[0];
        if (createIfNotFound && classes.get(topClassName) == null) {
            classes.put(topClassName, new ClassStats());
        }

        ClassStats next = classes.get(topClassName);

        if (next == null) {
            return null;
        }

        String[] classes = qname.getClasses();

        for (int i = 1; i < classes.length && next != null; i++) {
            // Delegate search for nested classes to ClassStats
            next = next.getNestedClassStats(classes[i], createIfNotFound);
        }

        return next;
    }


    /**
     * Returns the deepest PackageStats that contains the named resource. If the second parameter is
     * set, creates the missing PackageStats along the way.
     *
     * @param qname            The qualified name of the resource
     * @param createIfNotFound If set to true, the hierarch is created if non existent
     *
     * @return The deepest package that contains this resource. Can only return null if
     * createIfNotFound is unset.
     */
    public PackageStats getSubPackage(QualifiedName qname, boolean createIfNotFound) {
        if (qname.getPackages() == null) {
            return this; // the toplevel
        }

        String[] packagePath = qname.getPackages();
        PackageStats next = this;

        for (int i = 0; i < packagePath.length && next != null; i++) {
            if (createIfNotFound && next.subPackages.get(packagePath[i]) == null) {
                next.subPackages.put(packagePath[i], new PackageStats());
            }

            next = next.subPackages.get(packagePath[i]);
        }

        return next;
    }


    public double getMemo(Metrics.ClassMetricKey key, QualifiedName qname) {
        // TODO
        // Looks for a memoized result

        return Double.NaN;
    }

    public double getMemo(Metrics.OperationMetricKey key, QualifiedName qname) {
        // TODO
        // Looks for a memoized result

        return Double.NaN;
    }

    public boolean hasMatchingSig(QualifiedName qname, OperationSigMask sigMask) {
        // TODO
        // navigate to the class in the tree
        // return true if the signature of the qualified name is covered by the
        // mask.
        return true;
    }
}
