/*
 * Created on 11-jan-2006
 */
package net.sourceforge.pmd.lang.jsp.ast;

/**
 * @author Pieter_Van_Raemdonck
 *         Exception indicating that a syntactic error has been found.
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
