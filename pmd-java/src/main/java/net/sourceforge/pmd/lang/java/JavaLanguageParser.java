/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 *
 * @deprecated This is internal API, use {@link LanguageVersionHandler#getParser(ParserOptions)}.
 */
@InternalApi
@Deprecated
public class JavaLanguageParser extends AbstractJavaParser {
    private final int jdkVersion;
    private final boolean preview;

    public JavaLanguageParser(int jdkVersion, ParserOptions parserOptions) {
        this(jdkVersion, false, parserOptions);
    }

    public JavaLanguageParser(int jdkVersion, boolean preview, ParserOptions parserOptions) {
        super(parserOptions);
        this.jdkVersion = jdkVersion;
        this.preview = preview;
    }

    @Override
    protected JavaParser createJavaParser(Reader source) throws ParseException {
        JavaParser javaParser = super.createJavaParser(source);
        javaParser.setJdkVersion(jdkVersion);
        javaParser.setPreview(preview);
        return javaParser;
    }
}
