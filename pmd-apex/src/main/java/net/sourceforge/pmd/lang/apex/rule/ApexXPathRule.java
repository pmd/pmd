/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

public class ApexXPathRule extends XPathRule {

    private static final BooleanProperty RECORDING_COMMENTS_DESCRIPTOR = ApexParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
    private static final BooleanProperty RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR = ApexParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;
    private static final EnumeratedProperty<ApexParserOptions.Version> RHINO_LANGUAGE_VERSION = ApexParserOptions.RHINO_LANGUAGE_VERSION;

    public ApexXPathRule() {
	super.setLanguage(LanguageRegistry.getLanguage(ApexLanguageModule.NAME));
	definePropertyDescriptor(RECORDING_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RHINO_LANGUAGE_VERSION);
    }

    @Override
    public ParserOptions getParserOptions() {
	return new ApexParserOptions(this);
    }
}
