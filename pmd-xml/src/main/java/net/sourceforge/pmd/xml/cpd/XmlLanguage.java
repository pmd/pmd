/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.xml.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;

public class XmlLanguage extends AbstractLanguage {

    public XmlLanguage() {
        super(XmlLanguageModule.NAME, XmlLanguageModule.TERSE_NAME, new XmlTokenizer(), XmlLanguageModule.EXTENSIONS);
    }
}
