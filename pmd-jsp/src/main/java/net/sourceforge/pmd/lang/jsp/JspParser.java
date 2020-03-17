/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;

/**
 * Adapter for the JspParser.
 *
 * @deprecated This is internal API, use {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@InternalApi
@Deprecated
public class JspParser extends AbstractParser {

    public JspParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JspTokenManager(source);
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return new net.sourceforge.pmd.lang.jsp.ast.JspParser(new SimpleCharStream(source)).CompilationUnit();
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME
    }
}
