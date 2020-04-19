/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;


import net.sourceforge.pmd.lang.ast.impl.javacc.io.FragmentedDocCursor.Fragment;
import net.sourceforge.pmd.util.document.Chars;

final class FragmentedDocBuilder {

    private final Chars mainBuf;

    private Fragment lastFragment;
    private Fragment firstFragment;

    private int curOffInInput;

    FragmentedDocBuilder(Chars buffer) {
        this.mainBuf = buffer;
    }

    /**
     * Add a new fragment.
     *
     * @param startInInput Start (inclusive) of the overwritten text in the source
     * @param endInInput   End (exclusive) ...
     * @param translation  Characters with which the range startInInput..endInInput are overwritten.
     *                     This may be empty.
     */
    void recordDelta(int startInInput, int endInInput, Chars translation) {
        assert curOffInInput <= startInInput
            : "Already moved past " + curOffInInput + ", cannot add delta at " + startInInput;
        assert startInInput <= endInInput : "Offsets must be ordered";
        assert translation != null : "Translation cannot be null";

        int inLength = endInInput - startInInput;
        if (firstFragment == null) {
            assert lastFragment == null;
            firstFragment = new Fragment(null, startInInput, mainBuf.slice(0, startInInput));
            lastFragment = new Fragment(firstFragment, inLength, translation);
            curOffInInput = endInInput;
            return;
        }

        Fragment last = lastFragment;
        int prevLen = startInInput - curOffInInput;
        if (prevLen != 0) {
            last = new Fragment(last, prevLen, mainBuf.slice(curOffInInput, prevLen));
        }
        last = new Fragment(last, inLength, translation);
        this.lastFragment = last;
        this.curOffInInput = endInInput;
    }

    /**
     * Finalize the construction process.
     */
    FragmentedDocCursor newCursor() {
        if (firstFragment == null) {
            // No deltas in whole document, there's a single fragment
            // This is the case for > 97% of Java files (source: OpenJDK)
            return new FragmentedDocCursor(new Fragment(null, mainBuf.length(), mainBuf));
        } else {
            if (curOffInInput < mainBuf.length()) {
                // there's some text left between the last fragment and the end of the doc
                int remLen = mainBuf.length() - curOffInInput;
                Chars remainder = mainBuf.slice(curOffInInput, remLen);
                lastFragment = new Fragment(lastFragment, remLen, remainder);
            }
            return new FragmentedDocCursor(firstFragment);
        }
    }

    int inputOffsetAt(int outputOffset) {
        Fragment f = firstFragment;
        if (f == null) {
            return outputOffset;
        }
        int sum = outputOffset;
        while (f != null && f.inStart() < sum) {
            sum += f.inLen();
            f = f.next;
        }
        return sum;
    }
}
