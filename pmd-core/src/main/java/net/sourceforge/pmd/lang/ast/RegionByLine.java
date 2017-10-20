/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Represents a region in a source file bounded by a (beginLine, beginColumn) and (endLine, endColumn).
 */
public interface RegionByLine {

    /**
     * Gets the line where the region begins
     * @return a non-negative integer containing the begin line
     */
    int getBeginLine();

    /**
     * Gets the line where the region ends
     * @return a non-negative integer containing the end line
     */
    int getEndLine();

    /**
     * Gets the column offset from the start of the begin line where the region begins
     * @return a non-negative integer containing the begin column
     */
    int getBeginColumn();

    /**
     * Gets the column offset from the start of the end line where the region ends
     * @return a non-negative integer containing the begin column
     */
    int getEndColumn();
}
