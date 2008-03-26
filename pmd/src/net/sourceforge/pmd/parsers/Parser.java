/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.parsers;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.ast.ParseException;

/**
 * Common interface for calling tree-building parsers or source files.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
// FUTURE Parser implementations need to be moved into Language specific packages
public interface Parser {
    /**
     * Get a TokenManager for the given source.
     * @param source Reader that provides the source code to tokenize.
     * @return A TokenManager for reading token.
     */
    TokenManager getTokenManager(Reader source);

    /**
     * Parse source code and return the root node of the AST.
     *
     * @param source Reader that provides the source code of a compilation unit
     * @return the root node of the AST that is built from the source code
     * @throws ParseException In case the source code could not be parsed, probably
     *                        due to syntactical errors.
     */
    Object parse(Reader source) throws ParseException;

    // TODO Document
    Map<Integer, String> getExcludeMap();

    // TODO Document
    String getExcludeMarker();

    // TODO Document
    void setExcludeMarker(String marker);
}
