package net.sourceforge.pmd.lang.xsl;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XslLanguageModule extends BaseLanguageModule {

    public static final String NAME = "XSL";

    public XslLanguageModule() {
        super(NAME, null, "xsl", XmlRuleChainVisitor.class, "xsl", "xslt");
        addVersion("", new XmlHandler(), true);
    }

}
