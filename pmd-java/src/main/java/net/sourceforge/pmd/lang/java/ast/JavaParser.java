/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaCharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    private final LanguageLevelChecker<?> checker;

    public JavaParser(LanguageLevelChecker<?> checker, ParserOptions parserOptions) {
        super(parserOptions);
        this.checker = checker;
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JavaTokenManager(source);
    }


    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaTokenDocument(fullText);
    }

    @Override
    protected CharStream newCharStream(JavaccTokenDocument tokenDocument) {
        return new JavaCharStream(tokenDocument);
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserOptions options) throws ParseException {
        JavaParserImpl parser = new JavaParserImpl(cs);
        String suppressMarker = options.getSuppressMarker();
        if (suppressMarker != null) {
            parser.setSuppressMarker(suppressMarker);
        }
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        ASTCompilationUnit acu = parser.CompilationUnit();
        acu.setTokenDocument(cs.getTokenDocument());
        acu.setNoPmdComments(parser.getSuppressMap());
        checker.check(acu);
        return acu;
    }
}
