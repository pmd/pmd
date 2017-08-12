/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

/**
 * @author Clément Fournier
 */
class MultifileFacadeBacker {


    private final PackageStats topLevelPackageStats = new PackageStats();


    /** Resets the data structure. Used for tests. */
    void reset() {
        topLevelPackageStats.reset();
    }


    PackageStats getTopLevelPackageStats() {
        return topLevelPackageStats;
    }

}
