/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.swift.SwiftLanguageModule;

/**
 * Language implementation for Swift
 */
public class SwiftLanguage extends AbstractLanguage {

    /**
     * Creates a new Swift Language instance.
     */
    public SwiftLanguage() {
        super(SwiftLanguageModule.NAME, SwiftLanguageModule.TERSE_NAME, new SwiftTokenizer(), SwiftLanguageModule.EXTENSIONS);
    }
}
