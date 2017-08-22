/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

/**
 * Facade class for multi-file analysis. Stores an instance of a ProjectMirror.
 *
 * @author Clément Fournier
 */
final class MultifileFacade {

    private static final MultifileFacadeBacker FACADE = new MultifileFacadeBacker();


    private MultifileFacade() {

    }


    /** Resets the entire data structure. Used for tests. */
    static void reset() {
        FACADE.reset();
    }


    /**
     * Gets the PackageStats instance representing the currently analysed project.
     *
     * @return The project mirror
     */
    static PackageStats getTopLevelPackageStats() {
        return FACADE.getTopLevelPackageStats();
    }


}
