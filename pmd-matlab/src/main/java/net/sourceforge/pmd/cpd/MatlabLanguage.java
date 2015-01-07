/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


/**
 * Defines the Language module for Matlab
 */
public class MatlabLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link MatlabLanguage} with the default extensions for matlab files.
     */
    public MatlabLanguage() {
        super("Matlab", "matlab", new MatlabTokenizer(), ".m");
    }
}
