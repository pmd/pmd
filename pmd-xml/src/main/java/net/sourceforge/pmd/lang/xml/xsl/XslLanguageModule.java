/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.xsl;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.lang.xml.cpd.XmlCpdLexer;

/**
 * This language module is deprecated. XSL is now a dialect of XML.
 * @deprecated Since 7.13.0. Use @link{XslDialectModule} instead.
 */
@Deprecated
public class XslLanguageModule extends SimpleLanguageModuleBase {
    private static final String ID = "xsl";

    public XslLanguageModule() {
        super(LanguageMetadata.withId(ID).name("XSL")
                              .extensions("xsl", "xslt")
                              .addVersion("1.0")
                              .addVersion("2.0")
                              .addDefaultVersion("3.0"),
                new XmlHandler());
    }

    public static XslLanguageModule getInstance() {
        return (XslLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new XmlCpdLexer();
    }
}
