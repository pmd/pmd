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
     * Calls to this method must occur in source order (ie param
     * offsetInInput increases monotonically).
     */
    void recordDelta(int startInInput, int endInInput, Chars translation) {
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
        last = new Fragment(last, prevLen, mainBuf.slice(curOffInInput, prevLen));
        last = new Fragment(last, inLength, translation);
        this.lastFragment = last;
        this.curOffInInput = endInInput;
    }

    FragmentedDocCursor newCursor() {
        if (firstFragment == null) { // no deltas in whole document
            Fragment whole = new Fragment(null, mainBuf.length(), mainBuf);
            return new FragmentedDocCursor(whole);
        } else {
            if (curOffInInput < mainBuf.length()) {
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
