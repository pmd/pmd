/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.ParserOptions;

/**
 * Adapter for the JavaParser, using Java 1.6 grammar.
 */
public class Java16Parser extends Java15Parser {

    public Java16Parser(ParserOptions parserOptions) {
	super(parserOptions);
    }
}
