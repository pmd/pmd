/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions.Version;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public abstract class AbstractEcmascriptRule extends AbstractRule
        implements EcmascriptParserVisitor, ImmutableLanguage {

    private static final PropertyDescriptor<Boolean> RECORDING_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
    private static final PropertyDescriptor<Boolean> RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;
    private static final PropertyDescriptor<Version> RHINO_LANGUAGE_VERSION = EcmascriptParserOptions.RHINO_LANGUAGE_VERSION;

    public AbstractEcmascriptRule() {
        super.setLanguage(LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME));
        // Rule-specific parser options are not supported. What do we do?
        definePropertyDescriptor(RECORDING_COMMENTS_DESCRIPTOR);
        definePropertyDescriptor(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR);
        definePropertyDescriptor(RHINO_LANGUAGE_VERSION);
    }

    @Override
    public ParserOptions getParserOptions() {
        return new EcmascriptParserOptions(this);
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

}
