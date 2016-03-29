/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.SwiftTokenizer;

/**
 * Language implementation for Swift
 */
public class SwiftLanguage extends AbstractLanguage {

    /**
     * Creates a new Swift Language instance.
     */
    public SwiftLanguage() {
        super("Swift", "swift", new SwiftTokenizer(), ".swift");
    }
}
