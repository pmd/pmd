/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;

/**
 * JSP language parser.
 */
public final class JspParser extends JjtreeParserAdapter<ASTCompilationUnit> {

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
    protected ASTCompilationUnit parseImpl(CharStream cs, String suppressMarker, LanguageVersion languageVersion) throws ParseException {
        return new JspParserImpl(cs).CompilationUnit().setLanguageVersion(languageVersion);
    }

}
