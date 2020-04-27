/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.IOUtil;

/**
 * Adapter for the PLSQLParser.
 *
 * @deprecated This is internal API, use {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@InternalApi
@Deprecated
public class PLSQLParser extends AbstractParser {
    public PLSQLParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new PLSQLTokenManager(IOUtil.skipBOM(source));
    }

    /**
     * Subclass should override this method to modify the PLSQLParser as needed.
     */
    protected net.sourceforge.pmd.lang.plsql.ast.PLSQLParser createPLSQLParser(Reader source) throws ParseException {
        Reader in = IOUtil.skipBOM(source);
        // Wrapped PLSQL AST Parser
        return new net.sourceforge.pmd.lang.plsql.ast.PLSQLParser(in);
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        try {
            String sourcecode = IOUtils.toString(source);
            AbstractTokenManager.setFileName(fileName);
            return createPLSQLParser(new StringReader(sourcecode)).Input(sourcecode);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME
    }
}
