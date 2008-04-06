/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Adapter for the JspParser.
 */
public class JspParser extends AbstractParser {

    public TokenManager getTokenManager(Reader source) {
	return new JspTokenManager(source);
    }

    public Node parse(Reader source) throws ParseException {
	return new net.sourceforge.pmd.lang.jsp.ast.JspParser(new JavaCharStream(source)).CompilationUnit();
    }

    public Map<Integer, String> getExcludeMap() {
	return new HashMap<Integer, String>(); // FIXME
    }
}
