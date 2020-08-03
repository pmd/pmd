/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlParserOptions;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * @deprecated Parser options are deprecated, use {@link XPathRule} directly
 */
@Deprecated
public class XmlXPathRule extends XPathRule {

    @Deprecated
    public static final PropertyDescriptor<Boolean> COALESCING_DESCRIPTOR = XmlParserOptions.COALESCING_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> EXPAND_ENTITY_REFERENCES_DESCRIPTOR = XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> IGNORING_COMMENTS_DESCRIPTOR = XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR = XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> NAMESPACE_AWARE_DESCRIPTOR = XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> VALIDATING_DESCRIPTOR = XmlParserOptions.VALIDATING_DESCRIPTOR;
    @Deprecated
    public static final PropertyDescriptor<Boolean> XINCLUDE_AWARE_DESCRIPTOR = XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR;

    public XmlXPathRule() {
        super.setLanguage(LanguageRegistry.getLanguage(XmlLanguageModule.NAME));
        definePropertyDescriptor(COALESCING_DESCRIPTOR);
        definePropertyDescriptor(EXPAND_ENTITY_REFERENCES_DESCRIPTOR);
        definePropertyDescriptor(IGNORING_COMMENTS_DESCRIPTOR);
        definePropertyDescriptor(IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR);
        definePropertyDescriptor(NAMESPACE_AWARE_DESCRIPTOR);
        definePropertyDescriptor(VALIDATING_DESCRIPTOR);
        definePropertyDescriptor(XINCLUDE_AWARE_DESCRIPTOR);
    }

    @Override
    public ParserOptions getParserOptions() {
        return new XmlParserOptions(this);
    }
}
