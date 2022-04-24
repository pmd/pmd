/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.function.ToIntFunction;

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
        return offsetTransform(outOffset, inclusive,
                               Fragment::outToIn,
                               Fragment::outEnd,
                               Fragment::outStart
        );
    }

    @Override
    protected int inverseLocalOffsetTransform(int inOffset, boolean inclusive) {
        return offsetTransform(inOffset, inclusive,
                               Fragment::inToOut,
                               Fragment::inEnd,
                               Fragment::inStart
        );
    }

    interface OffsetMapper {

        int mapOffset(Fragment fragment, int offset);
    }

    private int offsetTransform(int offset,
                                boolean inclusive,
                                OffsetMapper mapOffsetWhenContains,
                                ToIntFunction<Fragment> end,
                                ToIntFunction<Fragment> start) {
        // caching the last accessed fragment instead of doing
        // a linear search is critical for performance.
        Fragment f = this.lastAccessedFragment;
        if (f == null) {
            return offset;
        }

        // Whether the fragment contains the offset we're looking for.
        // Will be true most of the time.
        boolean containsOffset =
            start.applyAsInt(f) >= offset && offset < end.applyAsInt(f);

        if (!containsOffset) {
            // Slow path, we must search for the fragment
            // This optimisation is important, otherwise we have
            // to search for very long times in some files

            if (end.applyAsInt(f) < offset) { // search forward
                while (f.next != null && end.applyAsInt(f) < offset) {
                    f = f.next;
                }
            } else { // search backwards
                while (f.prev != null && offset <= start.applyAsInt(f)) {
                    f = f.prev;
                }
            }
            lastAccessedFragment = f;
        }

        if (!inclusive && end.applyAsInt(f) == offset) {
            if (f.next != null) {
                f = f.next;
                lastAccessedFragment = f;
                // fallthrough
            } else {
                return mapOffsetWhenContains.mapOffset(f, offset) + 1;
            }
        }
        return mapOffsetWhenContains.mapOffset(f, offset);
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
