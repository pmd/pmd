/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cs;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for C#
 *
 * @deprecated There is no full PMD support for c#.
 */
@Deprecated
public class CsLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "C#";
    /** The terse name. */
    public static final String TERSE_NAME = "cs";

    /**
     * Create a new instance of C# Language Module.
     */
    public CsLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "cs");
        addVersion("", null, true);
    }
}
