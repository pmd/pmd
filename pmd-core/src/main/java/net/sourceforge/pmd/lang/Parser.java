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
     *
     * @param fileName
     *            The file name being parsed (may be <code>null</code>).
     * @param source
     *            Reader that provides the source code to tokenize.
     * @return A TokenManager for reading token.
     * @deprecated For removal in 7.0.0
     */
    @Deprecated
    TokenManager getTokenManager(String fileName, Reader source);


    /**
     * Indicates if this parser can actual parse, or if it can only tokenize.
     *
     * @deprecated With 7.0.0, all parsers will be able to parse and
     *     this method will be removed. Note that in the meantime, you
     *     probably still need to check this method.
     */
    @Deprecated
    boolean canParse();

    /**
     * Parse source code and return the root node of the AST.
     *
     * @param fileName
     *            The file name being parsed (may be <code>null</code>).
     * @param source
     *            Reader that provides the source code of a compilation unit
     * @return the root node of the AST that is built from the source code
     * @throws ParseException
     *             In case the source code could not be parsed, probably due to
     *             syntactical errors.
     */
    Node parse(String fileName, Reader source) throws ParseException;

    /**
     * Returns the map of line numbers to suppression / review comments.
     * Only single line comments are considered, that start with the configured
     * "suppressMarker", which by default is "PMD". The text after the
     * suppressMarker is used as a "review comment" and included in this map.
     *
     * <p>
     * This map is later used to determine, if a violation is being suppressed.
     * It is suppressed, if the line of the violation is contained in this suppress map.
     *
     * @return map of the suppress lines with the corresponding review comments.
     *
     * @deprecated With 7.0.0, this method will be removed. To support
     *    suppressing with suppress markers, this method is still needed in PMD 6.
     */
    @Deprecated
    Map<Integer, String> getSuppressMap();
}
