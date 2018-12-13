/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ruby;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language module for Ruby.
 *
 * @deprecated There is no full PMD support for Ruby.
 */
@Deprecated
public class RubyLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Ruby";
    /** The terse name. */
    public static final String TERSE_NAME = "ruby";

    /**
     * Creates a new Ruby Language Module instance.
     */
    public RubyLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "rb", "cgi", "class");
        addVersion("", null, true);
    }
}
