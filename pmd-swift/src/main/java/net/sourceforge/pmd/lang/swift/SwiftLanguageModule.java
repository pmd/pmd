/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for Swift
 *
 * @deprecated There is no full PMD support for Swift.
 */
@Deprecated
public class SwiftLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Swift";
    /** The terse name. */
    public static final String TERSE_NAME = "swift";

    /**
     * Create a new instance of Swift Language Module.
     */
    public SwiftLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "swift");
        addVersion("", null, true);
    }
}
