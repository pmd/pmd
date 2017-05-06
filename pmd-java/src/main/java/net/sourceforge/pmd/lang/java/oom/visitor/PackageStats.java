/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Package statistics. This recursive data structure mirrors the package structure of the analysed project and stores
 * information about the classes and subpackages it contains.
 *
 * @author Clément Fournier
 * @see ClassStats
 */
public class PackageStats {

    private Map<String, PackageStats> subPackages = new HashMap<>();
    private Map<String, ClassStats> classes = new HashMap<>();

    public PackageStats getSubPackage(String[] qname, int index) {
        // ...
        // recursive navigation method
        return null;
    }

    public ClassStats getClassStats(String name) {
        return classes.get(name);
    }

    public boolean hasMatchingSig(String qname, OperationSigMask sigMask) {
        // navigate to the class in the tree
        // return true if the signature of the qualified name is covered by the
        // mask.
        return true;
    }
}
