/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.go;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for Go.
 * @deprecated There is no full PMD support for go.
 */
@Deprecated
public class GoLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Golang";
    /** The terse name. */
    public static final String TERSE_NAME = "go";

    /**
     * Create a new instance of Golang Language Module.
     */
    public GoLanguageModule() {
        super(NAME, null, TERSE_NAME, "go");
        addVersion("1", null, true);
    }
}
