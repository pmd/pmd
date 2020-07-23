/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xsl;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlHandler;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XslLanguageModule extends BaseLanguageModule {

    public static final String NAME = "XSL";
    public static final String TERSE_NAME = "xsl";

    public XslLanguageModule() {
        super(NAME, null, TERSE_NAME, "xsl", "xslt");
        addVersion("", new XmlHandler(), true);
    }
}
