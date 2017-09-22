/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.Reader;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaLanguageParser extends AbstractJavaParser {
    private final int jdkVersion;

    public JavaLanguageParser(int jdkVersion, ParserOptions parserOptions) {
        super(parserOptions);
        this.jdkVersion = jdkVersion;
    }

    @Override
    protected JavaParser createJavaParser(Reader source) throws ParseException {
        JavaParser javaParser = super.createJavaParser(source);
        javaParser.setJdkVersion(jdkVersion);
        return javaParser;
    }
}
