/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

public class EcmascriptXPathRule extends XPathRule {

    private static final BooleanProperty RECORDING_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
    private static final BooleanProperty RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;
    private static final EnumeratedProperty<EcmascriptParserOptions.Version> RHINO_LANGUAGE_VERSION = EcmascriptParserOptions.RHINO_LANGUAGE_VERSION;

    public EcmascriptXPathRule() {
	super.setLanguage(Language.ECMASCRIPT);
	definePropertyDescriptor(RECORDING_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RHINO_LANGUAGE_VERSION);
    }

    @Override
    public ParserOptions getParserOptions() {
	return new EcmascriptParserOptions(this);
    }
}
