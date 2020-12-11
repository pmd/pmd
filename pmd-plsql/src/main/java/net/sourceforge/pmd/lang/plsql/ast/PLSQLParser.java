/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.io.Reader;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.util.IOUtil;

public class PLSQLParser extends JjtreeParserAdapter<ASTInput> {

    public PLSQLParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return PLSQLTokenKinds.describe(kind);
            }
        };
    }

    @Override
    public ASTInput parse(String fileName, Reader source) throws ParseException {
        return super.parse(fileName, IOUtil.skipBOM(source));
    }

    @Override
    protected ASTInput parseImpl(CharStream cs, ParserOptions options, String fileName) throws ParseException {
        return new PLSQLParserImpl(cs).Input();
    }

}
