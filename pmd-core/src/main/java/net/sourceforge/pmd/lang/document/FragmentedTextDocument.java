/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * A text document built as a set of deltas over another document.
 */
final class FragmentedTextDocument extends BaseMappedDocument implements TextDocument {

    private final Chars text;

    private Fragment lastAccessedFragment;

    FragmentedTextDocument(TextDocument base, Fragment firstFragment, Fragment lastFragment) {
        super(base);
        assert firstFragment != lastFragment; // NOPMD
        this.text = toChars(firstFragment, lastFragment);
        this.lastAccessedFragment = firstFragment;
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
    public Chars getText() {
        return text;
    }


    @Override
    public LanguageVersion getLanguageVersion() {
        return base.getLanguageVersion();
    }

    @Override
    protected int localOffsetTransform(int outOffset, boolean inclusive) {
        // caching the last accessed fragment instead of doing
        // a linear search is critical for performance.
        Fragment f = this.lastAccessedFragment;
        if (f == null) {
            return outOffset;
        }

        // Whether the fragment contains the offset we're looking for.
        // Will be true most of the time.
        boolean containsOffset =
            f.outStart() <= outOffset && outOffset < f.outEnd();

        if (!containsOffset) {
            // Slow path, we must search for the fragment
            // This optimisation is important, otherwise we have
            // to search for very long times in some files

            if (f.outEnd() < outOffset) { // search forward
                while (f.next != null && f.outEnd() < outOffset) {
                    f = f.next;
                }
            } else { // search backwards
                while (f.prev != null && outOffset <= f.outStart()) {
                    f = f.prev;
                }
            }
            lastAccessedFragment = f;
        }

        if (inclusive && f.outEnd() == outOffset && f.next != null) {
            // Inclusive means, the offset must correspond to a character in the source document.
            // Here we have to skip forward to the fragment that contains the character, because
            // it's not this one.
            do {
                f = f.next;
            } while (f.next != null && f.outLen() == 0);
        }
        return f.outToIn(outOffset);
    }


    /**
     * A delta from the original text to the translated text. This maps
     * a region of the original document to some new characters.
     */
    static final class Fragment {

        private final Chars chars;

        final @Nullable Fragment prev;
        @Nullable Fragment next;

        private final int inStart;
        private final int inLength;
        private final int outStart;

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
            return inStart() + outOffset - outStart();
        }

        int inToOut(int inOffset) {
            return inOffset - inStart() + outStart();
        }

        @Override
        public String toString() {
            return "Fragment[" + inStart() + ".." + inEnd() + " -> " + outStart() + ".." + outEnd() + "]" + chars;
        }
    }
}
