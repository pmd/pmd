/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;

/**
 * Implementation of LanguageVersionHandler for the XML.
 */
public class XmlHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser() {
        return new XmlParser();
    }

}
