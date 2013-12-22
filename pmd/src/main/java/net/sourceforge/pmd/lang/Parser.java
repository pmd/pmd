/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Common interface for calling tree-building parsers or source files.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface Parser {
    /**
     * Get the ParserOptions used by this Parser.
     */
    ParserOptions getParserOptions();
    
    /**
     * Get a TokenManager for the given source.
     * @param fileName The file name being parsed (may be <code>null</code>).
     * @param source Reader that provides the source code to tokenize.
     * @return A TokenManager for reading token.
     */
    TokenManager getTokenManager(String fileName, Reader source);

    /**
     * Indicates if this parser can actual parse, or if it can only tokenize.
     */
    boolean canParse();

    /**
     * Parse source code and return the root node of the AST.
     *
     * @param fileName The file name being parsed (may be <code>null</code>).
     * @param source Reader that provides the source code of a compilation unit
     * @return the root node of the AST that is built from the source code
     * @throws ParseException In case the source code could not be parsed, probably
     *                        due to syntactical errors.
     */
    Node parse(String fileName, Reader source) throws ParseException;

    // TODO Document
    Map<Integer, String> getSuppressMap();
}
