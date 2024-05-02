/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A source code match between two or more {@link Mark}s. The match may not be exact
 * between all marks of the same match, but all of them share a prefix at least as
 * long as the minimum tile size.
 */
@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal") // would be binary incompatible, todo for PMD 8
public class Match implements Comparable<Match>, Iterable<Mark> {

    private final int minTokenCount;
    private final int maxTokenCount;
    private final List<Mark> marks;

    public static final Comparator<Match> MATCHES_COMPARATOR = Comparator.comparingInt(it -> it.getMarks().size());

    public static final Comparator<Match> LINES_COMPARATOR = Comparator.comparingInt(Match::getLineCount);

    private Match(int min, int max, List<Mark> marks) {
        assert min >= 0 && min <= max && marks.size() >= 2;
        this.minTokenCount = min;
        this.maxTokenCount = max;
        this.marks = marks;
    }

    /**
     * Build a match for 2 marks.
     */
    static Match of(Mark first, Mark second) {
        List<Mark> marks;
        int cmp = first.compareTo(second);
        // mark list should be sorted
        if (cmp > 0) {
            marks = Arrays.asList(second, first);
        } else {
            marks = Arrays.asList(first, second);
        }

        int min = Math.min(first.getLength(), second.getLength());
        int max = Math.max(first.getLength(), second.getLength());

        return new Match(min, max, Collections.unmodifiableList(marks));
    }

    /**
     * Return the number of marks in this match.
     */
    public int getMarkCount() {
        return marks.size();
    }

    /**
     * Return the number of lines spanned by the first mark. The number of lines may differ between
     */
    public int getLineCount() {
        return marks.get(0).getLineCount();
    }

    /**
     * @deprecated This returns {@link #getMinTokenCount()}
     */
    @Deprecated
    public int getTokenCount() {
        return getMinTokenCount();
    }

    /**
     * The maximum token count of any mark within this match.
     */
    public int getMaxTokenCount() {
        return maxTokenCount;
    }

    /**
     * The minimum token count of any mark within this match.
     */
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
        return marks.get(0);
    }

    public Mark getSecondMark() {
        return marks.get(1);
    }

    @Override
    public String toString() {
        return "Match: \ntokenCount >= " + minTokenCount + " <= " + maxTokenCount + "\nmarks = " + marks.size();
    }

    /**
     * @deprecated Use methods on individual Marks.
     * */
    @Deprecated
    public int getEndIndex() {
        return getFirstMark().getEndTokenIndex();
    }

    static final class MatchBuilder {

        private final List<Mark> marks = new ArrayList<>();

        /** Add a mark to the match. Checks for overlap with already present marks and prunes the smaller marks. */
        MatchBuilder addMark(Mark newMark) {
            boolean added = false;
            for (ListIterator<Mark> iterator = marks.listIterator(); iterator.hasNext();) {
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
