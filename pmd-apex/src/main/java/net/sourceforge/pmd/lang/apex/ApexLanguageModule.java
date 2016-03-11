/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.BaseLanguageModule;

public class ApexLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    public ApexLanguageModule() {
    	super(NAME, null, TERSE_NAME, null, "class");
    	addVersion("", new ApexHandler(), true);
    }
}
