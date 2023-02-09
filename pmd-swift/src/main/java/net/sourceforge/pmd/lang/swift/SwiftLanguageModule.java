/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Language Module for Swift
 */
public class SwiftLanguageModule extends SimpleLanguageModuleBase {

    /** The name. */
    public static final String NAME = "Swift";
    /** The terse name. */
    public static final String TERSE_NAME = "swift";

    /**
     * Create a new instance of Swift Language Module.
     */
    public SwiftLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("swift")
                              .addVersion("4.2")
                              .addVersion("5.0")
                              .addVersion("5.1")
                              .addVersion("5.2")
                              .addVersion("5.3")
                              .addVersion("5.4")
                              .addVersion("5.5")
                              .addVersion("5.6")
                              .addDefaultVersion("5.7"),
                new SwiftHandler());
    }
}
