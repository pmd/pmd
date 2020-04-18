/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Adapter for the VmParser.
 */
public class VmParser extends JjtreeParserAdapter<ASTTemplate> {

    @Override
    protected JavaccTokenDocument newDocumentImpl(TextDocument fullText) {
        return new VmTokenDocument(fullText);
    }

    @Override
    protected ASTTemplate parseImpl(CharStream cs, ParserTask task) throws ParseException {
        return new VmParserImpl(cs).Template().addTaskInfo(task);
    }


    private static class VmTokenDocument extends JavaccTokenDocument {

        VmTokenDocument(TextDocument fullText) {
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
