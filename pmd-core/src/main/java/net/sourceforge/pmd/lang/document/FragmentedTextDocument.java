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
    Fragment f = getLastAccessedFragment();

    if (f != null && f.contains(outOffset)) {
        return f.outToIn(outOffset);
    }

    f = searchForFragment(outOffset, f);
    setLastAccessedFragment(f);

    if (inclusive && f.isAtEnd(outOffset)) {
        f = skipEmptyFragments(f);
    }

    return f.outToIn(outOffset);
}

private Fragment getLastAccessedFragment() {
    return this.lastAccessedFragment;
}

private void setLastAccessedFragment(Fragment f) {
    this.lastAccessedFragment = f;
}

private Fragment searchForFragment(int outOffset, Fragment f) {
    if (f == null || f.outEnd() < outOffset) {
        // search forward
        while (f != null && f.outEnd() < outOffset) {
            f = f.next;
        }
    } else {
        // search backwards
        while (f.prev != null && outOffset <= f.outStart()) {
            f = f.prev;
        }
    }
    return f;
}

private Fragment skipEmptyFragments(Fragment f) {
    while (f.next != null && f.outLen() == 0) {
        f = f.next;
    }
    return f;
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
