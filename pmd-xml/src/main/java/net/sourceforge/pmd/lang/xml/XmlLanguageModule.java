/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.cpd.XmlCpdLexer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XmlLanguageModule extends SimpleLanguageModuleBase {
    private static final String ID = "xml";

    public XmlLanguageModule() {
        super(LanguageMetadata.withId(ID).name("XML")
                              .extensions("xml")
                              .addVersion("1.0")
                              .addDefaultVersion("1.1"),
                new XmlHandler());
    }

    public static XmlLanguageModule getInstance() {
        return (XmlLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new XmlCpdLexer();
    }
}
