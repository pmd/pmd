/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.ast;

/**
 * @author Pieter_Van_Raemdonck
 * @since Created on 11-jan-2006
 */
public class StartAndEndTagMismatchException extends SyntaxErrorException {

    public static final String START_END_TAG_MISMATCH_RULE_NAME
            = "Start and End Tags of an XML Element must match.";

    private int startLine, endLine, startColumn, endColumn;
    private String startTagName, endTagName;

    /**
     * Public constructor.
     *
     * @param startLine
     * @param startColumn
     * @param startTagName
     * @param endLine
     * @param endColumn
     * @param endTagName
     */
    public StartAndEndTagMismatchException(int startLine, int startColumn, String startTagName,
                                           int endLine, int endColumn, String endTagName) {
        super(endLine, START_END_TAG_MISMATCH_RULE_NAME);
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.startTagName = startTagName;

        this.endLine = endLine;
        this.endColumn = endColumn;
        this.endTagName = endTagName;
    }


    /**
     * @return Returns the endColumn.
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * @return Returns the endLine.
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * @return Returns the startColumn.
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * @return Returns the startLine.
     */
    public int getStartLine() {
        return startLine;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return "The start-tag of element \"" + startTagName + "\" (line "
                + startLine + ", column " + startColumn
                + ") does not correspond to the end-tag found: \""
                + endTagName + "\" (line " + endLine
                + ", column " + endColumn + ").";
    }
}
