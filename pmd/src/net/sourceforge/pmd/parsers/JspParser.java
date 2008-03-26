/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.parsers;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.jsp.ast.JspCharStream;

/**
 * Adapter for the JspParser.
 */
public class JspParser extends AbstractParser {

    public TokenManager getTokenManager(Reader source) {
	return new JspTokenManager(source);
    }

    public Object parse(Reader source) throws ParseException {
	return new net.sourceforge.pmd.jsp.ast.JspParser(new JspCharStream(source)).CompilationUnit();
    }

    public Map<Integer, String> getExcludeMap() {
	return new HashMap<Integer, String>(); // FIXME
    }
}
