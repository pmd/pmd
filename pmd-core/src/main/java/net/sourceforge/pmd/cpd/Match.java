/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Match implements Comparable<Match>, Iterable<Mark> {

    private final int minTokenCount;
    private final int maxTokenCount;
    private final List<Mark> marks;

    public static final Comparator<Match> MATCHES_COMPARATOR = (ma, mb) -> mb.getMarkCount() - ma.getMarkCount();

    public static final Comparator<Match> LINES_COMPARATOR = (ma, mb) -> mb.getLineCount() - ma.getLineCount();

    private Match(int min, int max, List<Mark> marks) {
        assert min >= 0 && min <= max && marks.size() >= 2;
        this.minTokenCount = min;
        this.maxTokenCount = max;
        this.marks = marks;
    }

    Match(int tokenCount, Mark first, Mark second) {
        List<Mark> marks = new ArrayList<>(2);
        int cmp = first.compareTo(second);
        // mark list should be sorted
        if (cmp > 0) {
            marks.add(second);
            marks.add(first);
        } else {
            marks.add(first);
            marks.add(second);
        }
        this.marks = Collections.unmodifiableList(marks);
        this.minTokenCount = tokenCount;
        this.maxTokenCount = tokenCount;
    }

    @Deprecated
    Match(int tokenCount, TokenEntry first, TokenEntry second) {
        this(tokenCount, new Mark(first), new Mark(second));
    }

    public int getMarkCount() {
        return marks.size();
    }

    public int getLineCount() {
        return getMark(0).getLocation().getLineCount();
    }

    public int getTokenCount() {
        return minTokenCount;
    }

    public int getMaxTokenCount() {
        return maxTokenCount;
    }

    public int getMinTokenCount() {
        return minTokenCount;
    }

    /**
     * @deprecated Use {@link #getMarks()}
     */
    @Deprecated
    public Set<Mark> getMarkSet() {
        return Collections.unmodifiableSet(new HashSet<>(marks));
    }

    /**
     * Return a sorted list of marks. The list is non-empty.
     */
    public List<Mark> getMarks() {
        return marks;
    }

    @Override
    public Iterator<Mark> iterator() {
        return marks.iterator();
    }

    @Override
    public int compareTo(Match other) {
        int cmp = getFirstMark().compareTo(other.getFirstMark());
        cmp = cmp != 0 ? cmp : getSecondMark().compareTo(other.getSecondMark());
        return cmp;
    }

    public Mark getFirstMark() {
        return getMark(0);
    }

    public Mark getSecondMark() {
        return getMark(1);
    }

    @Override
    public String toString() {
        return "Match: \ntokenCount >= " + minTokenCount + " <= " + maxTokenCount + "\nmarks = " + marks.size();
    }

    public int getEndIndex() {
        return getMark(0).getToken().getLocalIndex() + getTokenCount() - 1;
    }

    private Mark getMark(int index) {
        return marks.get(index);
    }

    static final class MatchBuilder {

        private final List<Mark> marks = new ArrayList<>();

        MatchBuilder addMark(Mark newMark) {
            boolean added = false;
            for (ListIterator<Mark> iterator = marks.listIterator(); iterator.hasNext(); ) {
                Mark mark = iterator.next();
                int cmp = mark.contains(newMark);
                if (cmp > 0) {
                    // new mark contains other
                    if (added) {
                        iterator.remove();
                    } else {
                        iterator.set(newMark);
                        added = true;
                    }
                } else if (cmp < 0) {
                    // there is a mark containing the current one
                    return this;
                }
            }
            if (!added) {
                marks.add(newMark);
            }
            return this;
        }

        @Nullable
        Match build() {
            if (marks.size() < 2) {
                return null;
            }

            IntSummaryStatistics stats = marks.stream().mapToInt(Mark::getLength).summaryStatistics();
            marks.sort(Comparator.naturalOrder());
            return new Match(stats.getMin(), stats.getMax(), Collections.unmodifiableList(marks));
        }

    }

}
