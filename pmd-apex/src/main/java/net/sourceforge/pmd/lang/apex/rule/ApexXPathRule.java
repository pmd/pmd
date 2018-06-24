/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.renderers.CodeClimateRule;

public class ApexXPathRule extends XPathRule implements CodeClimateRule {

    public ApexXPathRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ApexLanguageModule.NAME));
        definePropertyDescriptor(CODECLIMATE_CATEGORIES);
        definePropertyDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER);
        definePropertyDescriptor(CODECLIMATE_BLOCK_HIGHLIGHTING);
    }

    @Override
    public ParserOptions getParserOptions() {
        return new ApexParserOptions();
    }
}
