/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;

/**
 * Adapter for the VtlParser.
 */
public class VtlParser extends JjtreeParserAdapter<ASTTemplate> {

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(VtlTokenKinds.TOKEN_NAMES) {

        @Override
        public JavaccToken createToken(JavaccTokenDocument self, int kind, CharStream cs, @Nullable String image) {
            String realImage = image == null ? cs.getTokenImage() : image;
            if (kind == VtlTokenKinds.ESCAPE_DIRECTIVE) {
                realImage = escapedDirective(realImage);
            }

            return super.createToken(self, kind, cs, realImage);
        }

        private String escapedDirective(String strImage) {
            int iLast = strImage.lastIndexOf("\\");
            String strDirective = strImage.substring(iLast + 1);
            return strImage.substring(0, iLast / 2) + strDirective;
        }
    };

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
    }

    @Override
    protected ASTTemplate parseImpl(CharStream cs, ParserTask task) throws ParseException {
        return new VtlParserImpl(cs).Template().makeTaskInfo(task);
    }


}
