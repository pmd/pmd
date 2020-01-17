/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaParser extends AbstractParser {

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
    public Node parse(String fileName, Reader source) throws ParseException {
        CharStream charStream = CharStreamFactory.javaCharStream(source, JavaTokenDocument::new);
        JavaParserImpl parser = new JavaParserImpl(charStream);
        String suppressMarker = getParserOptions().getSuppressMarker();
        if (suppressMarker != null) {
            parser.setSuppressMarker(suppressMarker);
        }
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        AbstractTokenManager.setFileName(fileName);
        ASTCompilationUnit acu = parser.CompilationUnit();
        acu.setNoPmdComments(parser.getSuppressMap());
        checker.check(acu);
        return acu;
    }
}
