/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;


import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

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

    TextDocument build(TextDocument original) {
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

    int inputOffsetAt(int outputOffset) {
        return inputOffsetAt(outputOffset, firstFragment);
    }

    static int inputOffsetAt(int outputOffset, @Nullable Fragment firstFragment) {
        Fragment f = firstFragment;
        if (f == null) {
            return outputOffset;
        }
        while (f.next != null && f.inEnd() < outputOffset) {
            f = f.next;
        }
        return f.outToIn(outputOffset);
    }

    static final class FragmentedTextDocument implements TextDocument {

        private final Fragment firstFragment;
        private final Chars text;
        private final TextDocument base;

        FragmentedTextDocument(TextDocument base, Fragment firstFragment, Fragment lastFragment) {
            assert firstFragment != lastFragment; // NOPMD
            this.firstFragment = firstFragment;
            this.text = toChars(firstFragment, lastFragment);
            this.base = base;
        }

        private static Chars toChars(Fragment firstFragment, Fragment lastFragment) {
            StringBuilder sb = new StringBuilder(lastFragment.outEnd());
            Fragment f = firstFragment;
            while (f != null) {
                f.getChars().appendChars(sb);
                f = f.next;
            }
            return Chars.wrap(sb);
        }

        @Override
        public int translateOffset(int outputOffset) {
            // todo this would be pretty slow when we're in the middle of some escapes
            // we could check save the fragment last accessed to speed it up, and look forwards & backwards
            return base.translateOffset(inputOffsetAt(outputOffset, firstFragment));
        }

        @Override
        public Chars getText() {
            return text;
        }

        @Override
        public long getChecksum() {
            return base.getChecksum();
        }

        @Override
        public LanguageVersion getLanguageVersion() {
            return base.getLanguageVersion();
        }

        @Override
        public String getPathId() {
            return base.getPathId();
        }

        @Override
        public String getDisplayName() {
            return base.getDisplayName();
        }

        @Override
        public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
            return base.createLineRange(startLineInclusive, endLineInclusive);
        }

        @Override
        public Chars sliceOriginalText(TextRegion region) {
            return base.sliceOriginalText(translateRegion(region));
        }

        @Override
        public FileLocation toLocation(TextRegion region) {
            return base.toLocation(translateRegion(region));
        }

        @Override
        public @NonNull TextRegion translateRegion(TextRegion region) {
            return TextRegion.fromBothOffsets(translateOffset(region.getStartOffset()),
                                              translateOffset(region.getEndOffset()));
        }

        @Override
        public void close() throws IOException {
            base.close();
        }
    }

    /**
     * A delta from the original text to the translated text. This maps
     * a region of the original document to some new characters.
     */
    static final class Fragment {

        private final Chars chars;

        final @Nullable Fragment prev;
        @Nullable Fragment next;

        private final int inLength;
        private final int outStart;
        private final int inStart;

        Fragment(@Nullable Fragment prev, int inLength, Chars chars) {
            this.chars = chars;
            this.prev = prev;
            this.inLength = inLength;
            if (prev != null) {
                prev.next = this;
                this.outStart = prev.outEnd();
                this.inStart = prev.inEnd();
            } else {
                this.outStart = 0;
                this.inStart = 0;
            }
        }

        public Chars getChars() {
            return chars;
        }

        int outStart() {
            return outStart;
        }

        int outLen() {
            return chars.length();
        }

        int outEnd() {
            return outStart() + outLen();
        }

        int inStart() {
            return inStart;
        }

        int inLen() {
            return inLength;
        }

        int inEnd() {
            return inStart() + inLen();
        }

        int outToIn(int outOffset) {
            return inStart() + (outOffset - outStart());
        }

        boolean contains(int outOffset) {
            return outStart() <= outOffset && outEnd() > outOffset;
        }

        @Override
        public String toString() {
            return "Fragment[" + inStart() + ".." + inEnd() + " -> " + outStart() + ".." + outEnd() + "]" + chars;
        }
    }
}
