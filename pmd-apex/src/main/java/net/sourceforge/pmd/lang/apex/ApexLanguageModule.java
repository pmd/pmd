/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.apex.rule.ApexRuleChainVisitor;

public class ApexLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";
    public static final String[] EXTENSIONS = { "cls", "trigger" };

    public ApexLanguageModule() {
        super(NAME, null, TERSE_NAME, ApexRuleChainVisitor.class, EXTENSIONS);
        addVersion("45", new ApexHandler(), true);
    }
}
