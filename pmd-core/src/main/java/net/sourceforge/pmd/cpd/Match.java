/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.util.IteratorUtil;

public class Match implements Comparable<Match>, Iterable<Mark> {

    private final int tokenCount;
    private final Set<Mark> markSet = new TreeSet<>();

    public static final Comparator<Match> MATCHES_COMPARATOR = (ma, mb) -> mb.getMarkCount() - ma.getMarkCount();

    public static final Comparator<Match> LINES_COMPARATOR = (ma, mb) -> mb.getLineCount() - ma.getLineCount();


    Match(int tokenCount, Mark first, Mark second) {
        markSet.add(first);
        markSet.add(second);
        this.tokenCount = tokenCount;
    }

    Match(int tokenCount, TokenEntry first, TokenEntry second) {
        this(tokenCount, new Mark(first), new Mark(second));
    }

    void addMark(TokenEntry entry) {
        markSet.add(new Mark(entry));
    }

    public int getMarkCount() {
        return markSet.size();
    }

    public int getLineCount() {
        return getMark(0).getLocation().getLineCount();
    }

    public int getTokenCount() {
        return this.tokenCount;
    }


    public Set<Mark> getMarkSet() {
        return Collections.unmodifiableSet(markSet);
    }

    @Override
    public Iterator<Mark> iterator() {
        return markSet.iterator();
    }

    @Override
    public int compareTo(Match other) {
        int diff = other.getTokenCount() - getTokenCount();
        if (diff != 0) {
            return diff;
        }
        return getFirstMark().compareTo(other.getFirstMark());
    }

    public Mark getFirstMark() {
        return getMark(0);
    }

    public Mark getSecondMark() {
        return getMark(1);
    }

    @Override
    public String toString() {
        return "Match: \ntokenCount = " + tokenCount + "\nmarks = " + markSet.size();
    }

    public int getEndIndex() {
        return getMark(0).getToken().getIndex() + getTokenCount() - 1;
    }

    private Mark getMark(int index) {
        if (index >= markSet.size()) {
            throw new NoSuchElementException();
        }
        return IteratorUtil.getNth(markSet.iterator(), index);
    }
}
