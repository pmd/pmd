/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

/**
 * Interface to create JDK-appropriate parsers.
 *
 * @author Tom Copeland
 */
public interface TargetJDKVersion {
    /**
     * Creates a parser.
     *
     * @param in the stream to parser
     * @return a parser for the input stream
     */
    public JavaParser createParser(InputStream in);

    /**
     * Creates a parser.
     *
     * @param in an input stream reader
     * @return a parser for the stream read by the stream reader
     */
    public JavaParser createParser(Reader in);

    public String getVersionString();
}
