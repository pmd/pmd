/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for T-SQL.
 * @deprecated There is no full PMD support for T-SQL.
 */
@Deprecated
public class TSqlLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "T-SQL";
    /** The terse name. */
    public static final String TERSE_NAME = "tsql";

    /**
     * Create a new instance of TSql Language Module.
     */
    public TSqlLanguageModule() {
        super(NAME, null, TERSE_NAME, "tsql");
        addVersion("1", null, true);
    }
}
