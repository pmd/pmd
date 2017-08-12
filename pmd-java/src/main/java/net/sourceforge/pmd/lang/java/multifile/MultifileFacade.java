/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

/**
 * Facade class for multi-file analysis. Stores an instance of a ProjectMirror.
 *
 * @author Clément Fournier
 */
public class MultifileFacade {

    private static final MultifileFacadeBacker FACADE = new MultifileFacadeBacker();


    private MultifileFacade() {

    }

    /** Resets the entire data structure. Used for tests. */
    static void reset() {
        FACADE.reset();
    }


    /**
     * Gets the ProjectMirror instance representing the currently analysed project.
     *
     * @return The project mirror
     */
    public static ProjectMirror getProjectMirror() {
        return FACADE.getTopLevelPackageStats();
    }


    static PackageStats getTopLevelPackageStats() {
        return FACADE.getTopLevelPackageStats();
    }


}
