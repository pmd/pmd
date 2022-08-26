/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.util.CollectionUtil;

public class ApexLanguageModule extends BaseLanguageModule {
    private static final String FIRST_EXTENSION = "cls";
    private static final String[] REMAINING_EXTENSIONS = {"trigger"};

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";
    public static final String[] EXTENSIONS = CollectionUtil.listOf(FIRST_EXTENSION, REMAINING_EXTENSIONS).toArray(new String[0]);

    public ApexLanguageModule() {
        super(NAME, null, TERSE_NAME, FIRST_EXTENSION, REMAINING_EXTENSIONS);
        // addVersion(String.valueOf((int) Version.CURRENT.getExternal()), new ApexHandler(), true);
        // TODO(b/243906862)
        addVersion("0", new ApexHandler(), true);
    }
}
