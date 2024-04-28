/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sourceforge.pmd.util.IteratorUtil;

public class Match implements Comparable<Match>, Iterable<Mark> {

    private int minTokenCount;
    private int maxTokenCount;
    private final List<Mark> markSet = new ArrayList<>();

    public static final Comparator<Match> MATCHES_COMPARATOR = (ma, mb) -> mb.getMarkCount() - ma.getMarkCount();

    public static final Comparator<Match> LINES_COMPARATOR = (ma, mb) -> mb.getLineCount() - ma.getLineCount();

    Match(int tokenCount) {
        this.minTokenCount = tokenCount;
        this.maxTokenCount = tokenCount;
    }

    Match(int tokenCount, Mark first, Mark second) {
        this(tokenCount);
        markSet.add(first);
        markSet.add(second);
    }

    Match(int tokenCount, TokenEntry first, TokenEntry second) {
        this(tokenCount, new Mark(first), new Mark(second));
    }

    void addMark(Mark newMark) {
        boolean added = false;
        for (ListIterator<Mark> iterator = markSet.listIterator(); iterator.hasNext();) {
            Mark mark = iterator.next();
            int cmp = mark.contains(newMark);
            if (cmp > 0) {
                // new mark contains other
                if (added) {
                    iterator.remove();
                } else {
                    iterator.set(newMark);
                    registerMatchWithCount(newMark.getLength());
                    added = true;
                }
            } else if (cmp < 0) {
                // there is a mark containing the current one
                return;
            }
        }
        if (!added) {
            markSet.add(newMark);
            registerMatchWithCount(newMark.getLength());
        }
    }

    public int getMarkCount() {
        return markSet.size();
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

    private void registerMatchWithCount(int tokenCount) {
        this.maxTokenCount = max(this.maxTokenCount, tokenCount);
        this.minTokenCount = min(this.minTokenCount, tokenCount);
    }

    public Set<Mark> getMarkSet() {
        return Collections.unmodifiableSet(new HashSet<>(markSet));
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
        return "Match: \ntokenCount >= " + minTokenCount + " <= " + maxTokenCount + "\nmarks = " + markSet.size();
    }

    public int getEndIndex() {
        return getMark(0).getToken().getLocalIndex() + getTokenCount() - 1;
    }

    private Mark getMark(int index) {
        if (index >= markSet.size()) {
            throw new NoSuchElementException();
        }
        return IteratorUtil.getNth(markSet.iterator(), index);
    }


}
