/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

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

/**
 * Adapter for the VfParser.
 *
 * @deprecated This is internal API, use {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@Deprecated
@InternalApi
public class VfParser extends AbstractParser {

    public VfParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new VfTokenManager(source);
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return new net.sourceforge.pmd.lang.vf.ast.VfParser(new VfSimpleCharStream(source)).CompilationUnit();
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return new HashMap<>(); // FIXME
    }
}
