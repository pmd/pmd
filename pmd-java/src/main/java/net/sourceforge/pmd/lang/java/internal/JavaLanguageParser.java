/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaTokenManager;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaLanguageParser extends AbstractParser {

    private final LanguageLevelChecker<?> checker;
    private JavaParser javaParser;

    JavaLanguageParser(LanguageLevelChecker<?> checker, ParserOptions parserOptions) {
        super(parserOptions);
        this.checker = checker;
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JavaTokenManager(source);
    }

    private JavaParser createJavaParser(Reader source) throws ParseException {
        javaParser = new JavaParser(new JavaCharStream(source));
        javaParser.setJdkVersion(checker.getJdkVersion());
        javaParser.setPreview(checker.isPreviewEnabled());
        String suppressMarker = getParserOptions().getSuppressMarker();
        if (suppressMarker != null) {
            javaParser.setSuppressMarker(suppressMarker);
        }
        return javaParser;
    }


    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        ASTCompilationUnit acu = createJavaParser(source).CompilationUnit();
        checker.check(acu);
        return acu;
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return javaParser.getSuppressMap();
    }
}
