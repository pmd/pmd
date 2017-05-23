/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import static net.sourceforge.pmd.lang.java.ast.QualifiableNode.QualifiedName;

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
     * Adds a class to this package if it doesn't exist yet, and returns it.
     *
     * @param className The name of the class
     *
     * @return The ClassStats that was created, or found.
     */
    public ClassStats addClass(String className) {
        if (!classes.containsKey(className)) {
            classes.put(className, new ClassStats());
        }
        return classes.get(className);
    }

    /**
     * Adds a ClassStats to the hierarchy and returns it. The class can be nested.
     *
     * @param qname The qualified name of the class
     *
     * @return
     */
    public ClassStats addClass(QualifiedName qname) {
        PackageStats container = createPathFor(qname);
        ClassStats top = container.addClass(qname.getClasses()[0]);
        String[] classes = qname.getClasses();

        for (int i = 1; i < classes.length; i++) {
            top = top.addClass(classes[i]);
        }

        return top;
    }

    public double getMemo(Metrics.ClassMetricKey key, String qname) {
        // TODO
        // Looks for a memoized result

        return Double.NaN;
    }

    public double getMemo(Metrics.OperationMetricKey key, String qname) {
        // TODO
        // Looks for a memoized result

        return Double.NaN;
    }

    /**
     * Finds the PackageStats for the package passed as parameter.
     *
     * @param qname The qualified name of the resource to fetch the package for.
     *
     * @return The PackageStats describing the named resource, or null if it can't be found
     */
    public PackageStats getSubPackage(QualifiedName qname) {
        if (qname.getPackages() == null) {
            return this; // the toplevel
        }
        String[] packagePath = qname.getPackages();
        PackageStats top = subPackages.get(packagePath[0]);

        for (int i = 1; i < packagePath.length && top != null; i++) {
            top = top.subPackages.get(packagePath[i]);
        }

        return top;
    }


    /**
     * Creates the package hierarchy for this resource if it doesn't exist  and returns the deepest
     * package that contains the resource.
     *
     * @param qname The qualified name of the resource
     *
     * @return The deepest package that contains this resource
     */
    public PackageStats createPathFor(QualifiedName qname) {
        if (qname.getPackages() == null) {
            return this; // the toplevel
        }
        String[] packagePath = qname.getPackages();
        PackageStats top = this;

        for (int i = 0; i < packagePath.length; i++) {
            if (top.subPackages.get(packagePath[i]) == null) {
                top.subPackages.put(packagePath[i], new PackageStats());
            }

            top = top.subPackages.get(packagePath[i]);
        }

        return top;
    }

    public ClassStats getClassStats(String name) {
        return classes.get(name);
    }

    public boolean hasMatchingSig(QualifiedName qname, OperationSigMask sigMask) {
        // TODO
        // navigate to the class in the tree
        // return true if the signature of the qualified name is covered by the
        // mask.
        return true;
    }
}
