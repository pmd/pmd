/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.php;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for PHP.
 *
 * @deprecated There is no full PMD support for PHP.
 */
@Deprecated
public class PhpLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "PHP: Hypertext Preprocessor";
    /** The terse name. */
    public static final String TERSE_NAME = "php";

    /**
     * Create a new instance of the PHP Language Module.
     */
    public PhpLanguageModule() {
        super(NAME, "PHP", TERSE_NAME, null, "php", "class");
        addVersion("", null, true);
    }

}
