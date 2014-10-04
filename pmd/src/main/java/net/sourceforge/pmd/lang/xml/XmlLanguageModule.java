package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XmlLanguageModule extends BaseLanguageModule {

    public static final String NAME = "XML";
    public static final String TERSE_NAME = "xml";

    public XmlLanguageModule() {
        super(NAME, null, TERSE_NAME, XmlRuleChainVisitor.class, "xml");
        addVersion("", new XmlHandler(), true);
    }

}
