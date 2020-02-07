/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import java.io.Reader;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;

/**
 * Adapter for the VmParser.
 */
public class VmParser extends JjtreeParserAdapter<ASTTemplate> {

    public VmParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(final Reader source) {
        return new VmTokenManager(source);
    }

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return VmTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTTemplate parseImpl(CharStream cs, ParserOptions options) throws ParseException {
        return new VmParserImpl(cs).Template();
    }

}
