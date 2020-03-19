/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import java.io.Reader;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;

/**
 * JSP language parser.
 */
public final class JspParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    public JspParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JspTokenManager(source);
    }

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return JspTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserOptions options) throws ParseException {
        return new JspParserImpl(cs).CompilationUnit();
    }

}
