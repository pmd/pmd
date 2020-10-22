/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
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
    protected JavaccTokenDocument newDocument(String fullText) {
        return new VmTokenDocument(fullText);
    }

    @Override
    protected ASTTemplate parseImpl(CharStream cs, ParserOptions options) throws ParseException {
        return new VmParserImpl(cs).Template();
    }


    private static class VmTokenDocument extends JavaccTokenDocument {

        VmTokenDocument(String fullText) {
            super(fullText);
        }

        @Override
        protected @Nullable String describeKindImpl(int kind) {
            return VmTokenKinds.describe(kind);
        }

        @Override
        public JavaccToken createToken(int kind, CharStream cs, @Nullable String image) {
            String realImage = image == null ? cs.GetImage() : image;
            if (kind == VmTokenKinds.ESCAPE_DIRECTIVE) {
                realImage = escapedDirective(realImage);
            }

            return super.createToken(kind, cs, realImage);
        }

        private String escapedDirective(String strImage) {
            int iLast = strImage.lastIndexOf("\\");
            String strDirective = strImage.substring(iLast + 1);
            return strImage.substring(0, iLast / 2) + strDirective;
        }

    }

}
