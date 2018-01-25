/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Represents a region in a {@link Document} with the tuple (beginLine, endLine, beginColumn, endColumn).
 */
public interface RegionByLine {

    int getBeginLine();

    int getEndLine();

    int getBeginColumn();

    int getEndColumn();
}
