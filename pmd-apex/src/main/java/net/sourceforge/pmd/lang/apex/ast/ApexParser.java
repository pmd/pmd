/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.util.IOUtil;

import com.google.summit.SummitAST;
import com.google.summit.ast.CompilationUnit;

/**
 * @deprecated Internal API
 */
@InternalApi
@Deprecated
public class ApexParser {
    protected final ApexParserOptions parserOptions;

    private Map<Integer, String> suppressMap;

    public ApexParser(ApexParserOptions parserOptions) {
        ApexJorjeLogging.disableLogging();
        this.parserOptions = parserOptions;
    }

    public CompilationUnit parseApex(final String sourceCode) throws ParseException {
        return SummitAST.INSTANCE.parseAndTranslate(sourceCode, null);
    }

    public ApexNode<?> parse(final Reader reader) {
        try {
            final String sourceCode = IOUtil.readToString(reader);
            final CompilationUnit astRoot = parseApex(sourceCode);
            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(sourceCode, parserOptions);
            suppressMap = treeBuilder.getSuppressMap();

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            return treeBuilder.build(astRoot);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public Map<Integer, String> getSuppressMap() {
        return suppressMap;
    }
}
