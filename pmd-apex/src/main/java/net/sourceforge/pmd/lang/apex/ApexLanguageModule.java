package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.apex.rule.ApexRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class ApexLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Apex";
    public static final String TERSE_NAME = "apex";

    public ApexLanguageModule() {
        super(NAME, null, TERSE_NAME, ApexRuleChainVisitor.class, "js");
        addVersion("3", new ApexHandler(), true);
    }

}
