/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.IOUtil;

/**
 * Adapter for the PLSQLParser.
 */
public class PLSQLParser extends AbstractParser {

    /*
     * Wrapped PLSQL AST parser
     */
    private net.sourceforge.pmd.lang.plsql.ast.PLSQLParser parser;

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
        parser = new net.sourceforge.pmd.lang.plsql.ast.PLSQLParser(in);
        return parser;
    }

    public boolean canParse() {
        return true;
    }

    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return createPLSQLParser(source).Input();
    }

    public Map<Integer, String> getSuppressMap() {
        return new HashMap<Integer, String>(); // FIXME
    }
}
