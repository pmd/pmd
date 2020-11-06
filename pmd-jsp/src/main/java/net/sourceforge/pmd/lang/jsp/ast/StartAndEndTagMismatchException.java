/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * @author Pieter_Van_Raemdonck
 * @since Created on 11-jan-2006
 *
 * @deprecated for removal with PMD 7.0.0. Use {@link ParseException} instead.
 */
@Deprecated
public class StartAndEndTagMismatchException extends SyntaxErrorException {

    private static final long serialVersionUID = 5434485938487458692L;

    public static final String START_END_TAG_MISMATCH_RULE_NAME = "Start and End Tags of an XML Element must match.";

    private final int startLine;
    private final int endLine;
    private final int startColumn;
    private final int endColumn;
    private final String startTagName;
    private final String endTagName;

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
    public StartAndEndTagMismatchException(int startLine, int startColumn, String startTagName, int endLine,
            int endColumn, String endTagName) {
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return "The start-tag of element \"" + startTagName + "\" (line " + startLine + ", column " + startColumn
                + ") does not correspond to the end-tag found: \"" + endTagName + "\" (line " + endLine + ", column "
                + endColumn + ").";
    }
}
