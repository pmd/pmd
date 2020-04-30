/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * Implements the Xml Language
 *
 */
public class XmlLanguage extends AbstractLanguage {

    /**
     * Creates a new instance of {@link XmlLanguage}
     */
    public XmlLanguage() {
        super("Xml", "xml", new XmlTokenizer(), ".xml");
    }
}
