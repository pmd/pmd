/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.ParserOptions;

/**
 * Adapter for the JavaParser, using Java 1.7 grammar.
 */
public class Java17Parser extends Java15Parser {

    public Java17Parser(ParserOptions parserOptions) {
	super(parserOptions);
    }
}
