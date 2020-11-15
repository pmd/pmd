/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl.RootXmlNode;

/**
 * Adapter for the XmlParser.
 */
public class XmlParser implements Parser {
    private final XmlParserOptions parserOptions;

    public XmlParser(XmlParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }

    @Override
    public RootXmlNode parse(ParserTask task) throws ParseException {
        return new XmlParserImpl(parserOptions).parse(task);
    }

}
