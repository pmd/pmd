/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.matlab;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Implementation of the Matlab Language Module.
 *
 * @deprecated There is no full PMD support for Matlab.
 */
@Deprecated
public class MatlabLanguageModule extends BaseLanguageModule {

    /** The name, that can be used to display the language in UI. */
    public static final String NAME = "Matlab";
    /** The internal name. */
    public static final String TERSE_NAME = "matlab";

    /**
     * Creates a new instance of {@link MatlabLanguageModule} with the default
     * file extensions for Matlab.
     */
    public MatlabLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "m");
        addVersion("", new MatlabHandler(), true);
    }
}
