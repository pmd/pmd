/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Computes style spans for the code area. Quick and dirty implementation, everything's recomputed each time we paint
 * the area.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class CssTextStyler {

    private final CustomCodeArea codeArea;
    private SpansLine line = new SpansLine();


    CssTextStyler(CustomCodeArea codeArea) {
        this.codeArea = codeArea;
    }


    public StyleSpans<Collection<String>> getStyleSpans() {
        return line.toStyleSpans(codeArea.getLength());
    }


    public void clearStyles() {
        line.clear();
    }


    public void style(int beginLine, int beginColumn,
                      int endLine, int endColumn,
                      Set<String> cssClasses) {

        int offset = lengthUntil(beginLine, beginColumn);
        int spanLength = lengthBetween(beginLine, beginColumn, endLine, endColumn);
        SpanBound start = new SpanBound(offset, cssClasses, true);
        SpanBound end = new SpanBound(offset + spanLength, cssClasses, false);


        line.addBounds(start, end);
    }


    /** Length in characters before the specified position. */
    private int lengthUntil(int line, int column) {
        List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
        int length = 0;
        for (int i = 0; i < line - 1; i++) {
            length += paragraphs.get(i).length() + 1;
        }
        return length + column - 1;
    }


    /** Length in characters between the two positions. */
    private int lengthBetween(int l1, int c1, int l2, int c2) {
        int par1 = l1 - 1;
        int par2 = l2 - 1;
        if (l1 == l2) {
            return c2 - c1 + 1;
        } else if (l1 < l2) {
            List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
            int length = paragraphs.get(par1).length() - c1 + 1;
            for (int i = par1 + 1; i < par2; i++) {
                length += paragraphs.get(i).length() + 1;
            }
            return length + c2 + 1;
        } else {
            throw new IllegalArgumentException();
        }
    }


    private class SpansLine {

        private List<SpanBound> spanBounds = new ArrayList<>();


        SpansLine() {
        }


        void clear() {
            spanBounds.clear();
        }


        void addBounds(SpanBound start, SpanBound end) {
            spanBounds.add(start);
            spanBounds.add(end);
        }


        // flattens all the bounds to a collection of style spans.
        StyleSpans<Collection<String>> toStyleSpans(int totalLength) {

            Collections.sort(spanBounds);

            List<String> currentCssClasses = new ArrayList<>();
            int lastOffset = 0;

            StyleSpansBuilder<Collection<String>> builder = new StyleSpansBuilder<>();

            for (SpanBound bound : spanBounds) {
                int lengthFromLastOffset = bound.offset - lastOffset;

                if (bound.isBeginBound) {
                    builder.add(new StyleSpan<>(new HashSet<>(currentCssClasses), lengthFromLastOffset));
                    currentCssClasses.addAll(bound.cssClasses);
                } else {
                    builder.add(new StyleSpan<>(new HashSet<>(currentCssClasses), lengthFromLastOffset));
                    for (String css : bound.cssClasses) { // remove only first
                        currentCssClasses.remove(css);
                    }
                }

                lastOffset = bound.offset;
            }

            if (lastOffset > totalLength) {
                throw new IllegalArgumentException("StyleSpans too long bitch");
            } else if (lastOffset < totalLength) {
                builder.add(new StyleSpan<>(Collections.emptySet(), totalLength - lastOffset));
            }


            return builder.create();


        }

    }


    private class SpanBound implements Comparable<SpanBound> {

        private final Set<String> cssClasses;
        private final boolean isBeginBound;
        private int offset;


        SpanBound(int offset, Set<String> cssClasses, boolean isBeginBound) {
            this.offset = offset;
            this.cssClasses = cssClasses;
            this.isBeginBound = isBeginBound;
        }


        @Override
        public int compareTo(SpanBound o) {
            return Integer.compare(offset, o.offset);
        }


    }


}
