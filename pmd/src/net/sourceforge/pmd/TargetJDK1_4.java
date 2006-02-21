/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserTokenManager;

import java.io.InputStream;
import java.io.Reader;

/**
 * This is an implementation of {@link net.sourceforge.pmd.TargetJDKVersion} for
 * JDK 1.4.
 *
 * @author Tom Copeland
 */
public class TargetJDK1_4 implements TargetJDKVersion {

    /**
     * @see net.sourceforge.pmd.TargetJDKVersion#createParser(InputStream)
     */
    public JavaParser createParser(InputStream in) {
        return new JavaParser(new JavaCharStream(in));
    }

    /**
     * @see net.sourceforge.pmd.TargetJDKVersion#createParser(Reader)
     */
    public JavaParser createParser(Reader in) {
        return new JavaParser(new JavaCharStream(in));
    }

    /**
     * Creates a token manager for the parser.
     *
     * @param in the reader for which to create a token manager
     * @return a token manager
     */
    public JavaParserTokenManager createJavaParserTokenManager(Reader in) {
        return new JavaParserTokenManager(new JavaCharStream(in));
    }

    public String getVersionString() {
        return "1.4";
    }

}
