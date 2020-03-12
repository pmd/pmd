/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * Exception indicating that a syntactic error has been found.
 *
 * @author Pieter_Van_Raemdonck
 * @since Created on 11-jan-2006
 *
 * @deprecated for removal with PMD 7.0.0. Use {@link ParseException} instead.
 */
@Deprecated
public abstract class SyntaxErrorException extends ParseException {
    private static final long serialVersionUID = -6702683724078264059L;

    private final int line;
    private final String ruleName;

    /**
     * @param line
     * @param ruleName
     */
    public SyntaxErrorException(int line, String ruleName) {
        super();
        this.line = line;
        this.ruleName = ruleName;
    }

    /**
     * @return Returns the line.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return Returns the ruleName.
     */
    public String getRuleName() {
        return ruleName;
    }
}
