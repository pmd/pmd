/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xsl;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XslLanguageModule extends SimpleLanguageModuleBase {

    private static final Language INSTANCE = new XslLanguageModule();

    public XslLanguageModule() {
        super(LanguageMetadata.withId("xsl").name("XSL")
                              .extensions("xsl", "xslt")
                              .addVersion("1.0")
                              .addVersion("2.0")
                              .addDefaultVersion("3.0"),
                new XmlHandler());
    }

    public static Language getInstance() {
        return INSTANCE;
    }
}
