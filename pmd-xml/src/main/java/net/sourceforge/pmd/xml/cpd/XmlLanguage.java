/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.xml.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

public class XmlLanguage extends AbstractLanguage {

    public XmlLanguage() {
        super("Xml", "xml", new XmlTokenizer(), ".xml");
    }
}
