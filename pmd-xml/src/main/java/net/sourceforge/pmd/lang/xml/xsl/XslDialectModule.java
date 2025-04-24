/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.xsl;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 *
 * <p>Since PMD 7.13.0 this is a dialect of XML. Before that, XSL was a language module on its own.
 * @since 7.13.0
 */
public class XslDialectModule extends SimpleDialectLanguageModuleBase {
    private static final String ID = "xsl";

    public XslDialectModule() {
        super(LanguageMetadata.withId(ID).name("XSL")
                              .extensions("xsl", "xslt")
                              .addVersion("1.0")
                              .addVersion("2.0")
                              .addDefaultVersion("3.0")
                              .asDialectOf("xml"));
    }

    public static XslDialectModule getInstance() {
        return (XslDialectModule) LanguageRegistry.PMD.getLanguageById(ID);
    }
}
