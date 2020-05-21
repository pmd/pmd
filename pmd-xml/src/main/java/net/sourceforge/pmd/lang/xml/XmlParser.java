/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import java.io.Reader;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl;
import net.sourceforge.pmd.lang.xml.ast.internal.XmlParserImpl.RootXmlNode;

/**
 * Adapter for the XmlParser.
 */
public class XmlParser extends AbstractParser {

    public XmlParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public RootXmlNode parse(String fileName, Reader source) throws ParseException {
        return new XmlParserImpl((XmlParserOptions) parserOptions).parse(source);
    }

}
