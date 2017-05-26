/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Defines the Language module for Objective-C
 */
public class ObjectiveCLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link ObjectiveCLanguage} with the default
     * extensions for Objective-C files.
     */
    public ObjectiveCLanguage() {
        super("Objective-C", "objectivec", new ObjectiveCTokenizer(), ".h", ".m");
    }
}
