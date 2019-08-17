/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.antlr.AntlrRuleChainVisitor;

/**
 * Language Module for Swift
 */
public class SwiftLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Swift";
    /** The terse name. */
    public static final String TERSE_NAME = "swift";

    /**
     * Create a new instance of Swift Language Module.
     */
    public SwiftLanguageModule() {
        super(NAME, null, TERSE_NAME, AntlrRuleChainVisitor.class, "swift");
        addVersion("", new SwiftHandler(), true);
    }
}
