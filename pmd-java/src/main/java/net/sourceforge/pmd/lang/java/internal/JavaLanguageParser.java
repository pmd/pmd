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
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaLanguageParser extends AbstractParser {

    private final int jdkVersion;
    private final boolean preview;
    private JavaParser javaParser;

    public JavaLanguageParser(int jdkVersion, boolean preview, ParserOptions parserOptions) {
        super(parserOptions);
        this.jdkVersion = jdkVersion;
        this.preview = preview;
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
        return new JavaTokenManager(source);
    }

    private JavaParser createJavaParser(Reader source) throws ParseException {
        javaParser = new JavaParser(new JavaCharStream(source));
        javaParser.setJdkVersion(jdkVersion);
        javaParser.setPreview(preview);
        String suppressMarker = getParserOptions().getSuppressMarker();
        if (suppressMarker != null) {
            javaParser.setSuppressMarker(suppressMarker);
        }
        return javaParser;
    }


    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return createJavaParser(source).CompilationUnit();
    }

    @Override
    public Map<Integer, String> getSuppressMap() {
        return javaParser.getSuppressMap();
    }
}
