/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Reader;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * Produces an AST from a source file. Instances of this interface must
 * be stateless (which makes them trivially threadsafe).
 *
 * TODO
 *  - Ideally ParserOptions would be an argument to ::parse
 *  - ::parse would also take some more parameters, eg an error collector
 *  - The reader + filename would be a TextDocument
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface Parser {

    /**
     * Get the ParserOptions used by this Parser.
     *
     * @deprecated Parser options should be a parameter to {@link #parse(String, Reader)}
     */
    @Deprecated
    ParserOptions getParserOptions();


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
    RootNode parse(String fileName, Reader source) throws ParseException;


}
