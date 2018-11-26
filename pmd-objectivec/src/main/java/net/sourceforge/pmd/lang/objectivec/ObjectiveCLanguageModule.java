/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.objectivec;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Implementation of the Objective-C Language Module.
 *
 * @deprecated There is no full PMD support for Objective-C.
 */
@Deprecated
public class ObjectiveCLanguageModule extends BaseLanguageModule {

    /** The name, that can be used to display the language in UI. */
    public static final String NAME = "Objective-C";
    /** The internal name. */
    public static final String TERSE_NAME = "objectivec";

    /**
     * Creates a new instance of {@link ObjectiveCLanguageModule} with the
     * default file extensions for Objective-C.
     */
    public ObjectiveCLanguageModule() {
        super(NAME, null, TERSE_NAME, null, "h", "m");
        addVersion("", new ObjectiveCHandler(), true);
    }
}
