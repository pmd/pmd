/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.xsl;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XslLanguageModule extends SimpleDialectLanguageModuleBase {
    private static final String ID = "xsl";

    public XslLanguageModule() {
        super(LanguageMetadata.withId(ID).name("XSL")
                              .extensions("xsl", "xslt")
                              .addVersion("1.0")
                              .addVersion("2.0")
                              .addDefaultVersion("3.0")
                              .asDialectOf("xml"));
    }

    public static XslLanguageModule getInstance() {
        return (XslLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }
}
