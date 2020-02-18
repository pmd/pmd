/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Reader;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Produces an AST from a source file. Instances of this interface must
 * be stateless (which makes them trivially threadsafe).
 *
 * TODO
 *  - Ideally ParserOptions would be an argument to ::parse
 *  - ::parse would also take some more parameters, eg an error collector
 *  - The reader + filename would be a TextDocument
 *  - Remove TokenManager from here. Only JavaCC implementations support that,
 *    and it's barely used.
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
     */
    TokenManager getTokenManager(String fileName, Reader source);


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


}
