/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;


import net.sourceforge.pmd.util.document.FragmentedTextDocument.Fragment;

public final class FragmentedDocBuilder {

    private final Chars mainBuf;
    private final TextDocument original;

    private Fragment lastFragment;
    private Fragment firstFragment;

    private int curOffInInput;

    public FragmentedDocBuilder(TextDocument original) {
        this.mainBuf = original.getText();
        this.original = original;
    }

    public Chars inputChars() {
        return mainBuf;
    }

    /**
     * Add a new fragment.
     *
     * @param startInInput Start (inclusive) of the overwritten text in the source
     * @param endInInput   End (exclusive) ...
     * @param translation  Characters with which the range startInInput..endInInput are overwritten.
     *                     This may be empty.
     */
    public void recordDelta(int startInInput, int endInInput, Chars translation) {
        assert curOffInInput <= startInInput : "Already moved past " + curOffInInput + ", cannot add delta at " + startInInput;
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

    public TextDocument build() {
        if (firstFragment == null) {
            // No deltas in whole document, there's a single fragment
            // This is the case for > 97% of Java files (source: OpenJDK)
            return original;
        } else {
            if (curOffInInput < mainBuf.length()) {
                // there's some text left between the last fragment and the end of the doc
                int remLen = mainBuf.length() - curOffInInput;
                Chars remainder = mainBuf.slice(curOffInInput, remLen);
                lastFragment = new Fragment(lastFragment, remLen, remainder);
            }
            return new FragmentedTextDocument(original, firstFragment, lastFragment);
        }
    }

    public int inputOffsetAt(int outputOffset) {
        return FragmentedTextDocument.inputOffsetAt(outputOffset, firstFragment, true);
    }

}
