/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xsl;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.lang.xml.cpd.XmlTokenizer;

/**
 * Created by christoferdutz on 20.09.14.
 */
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
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new XmlTokenizer();
    }
}
