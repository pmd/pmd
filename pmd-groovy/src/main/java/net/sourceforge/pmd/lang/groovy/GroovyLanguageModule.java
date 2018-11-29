/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.groovy;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for Groovy
 *
 * @deprecated There is no full PMD support for Groovy.
 */
@Deprecated
public class GroovyLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Groovy";
    /** The terse name. */
    public static final String TERSE_NAME = "groovy";

    /**
     * Create a new instance of Groovy Language Module.
     */
    public GroovyLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "groovy");
        addVersion("", null, true);
    }
}
