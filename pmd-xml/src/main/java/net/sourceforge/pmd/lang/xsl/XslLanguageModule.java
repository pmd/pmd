/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xsl;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.xml.XmlHandler;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class XslLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "XSL";
    public static final String TERSE_NAME = "xsl";

    public XslLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("xsl", "xslt"), new XmlHandler());
    }

}
