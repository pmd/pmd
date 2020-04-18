/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;


class LineTracker {

    private int[] lineOffsets = new int[200];
    private int cur;

    void recordLine(int offset) {
        if (cur == lineOffsets.length) {
            int[] newOffsets = new int[this.lineOffsets.length + 200];
            System.arraycopy(lineOffsets, 0, newOffsets, 0, lineOffsets.length);
            this.lineOffsets = newOffsets;
        }
        lineOffsets[cur++] = offset;
    }
}
