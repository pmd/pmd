/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.ast;

/**
 * Exception indicating that a syntactic error has been found.
 * 
 * @author Pieter_Van_Raemdonck
 * @since Created on 11-jan-2006
 */
public abstract class SyntaxErrorException extends ParseException {
    private int line;
    private String ruleName;

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
