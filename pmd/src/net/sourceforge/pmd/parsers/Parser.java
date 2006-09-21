package net.sourceforge.pmd.parsers;

import net.sourceforge.pmd.ast.ParseException;

import java.io.Reader;
import java.util.Map;

/**
 * Common interface for calling tree-building parsers or source files.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface Parser {

    /**
     * Parse source code and return the root node of the AST.
     *
     * @param source Reader that provides the source code of a compilation unit
     * @return the root node of the AST that is built from the source code
     * @throws ParseException In case the source code could not be parsed, probably
     *                        due to syntactical errors.
     */
    Object parse(Reader source) throws ParseException;

    Map getExcludeMap();

    void setExcludeMarker(String marker);

}
