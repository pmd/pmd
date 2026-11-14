/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

/**
 * Adapter for the XmlParser.
 * @since 7.30.0
 */
public class XmlParser implements Parser {
    @Override
    public XmlRootNode parse(ParserTask task) throws ParseException {
        return new XmlParserImpl().parse(task);
    }
}
