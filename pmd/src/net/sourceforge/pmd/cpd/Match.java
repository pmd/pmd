/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Match implements Comparable {

    private int tokenCount;
    private int lineCount;
    private Set markSet = new TreeSet();
    private TokenEntry[] marks = new TokenEntry[2];
    private String code;
    private MatchCode mc;

    public static class MatchCode {

        private int first;
        private int second;

        public MatchCode() {}

        public MatchCode(TokenEntry m1, TokenEntry m2) {
            first = m1.getIndex();
            second = m2.getIndex();
        }

        public int hashCode() {
            return first + 37 * second;
        }

        public boolean equals(Object other) {
            MatchCode mc = (MatchCode) other;
            return mc.first == first && mc.second == second;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public void setSecond(int second) {
            this.second = second;
        }

    }

    public Match(int tokenCount, TokenEntry first, TokenEntry second) {
        markSet.add(first);
        markSet.add(second);
        marks[0] = first;
        marks[1] = second;
        this.tokenCount = tokenCount;
    }

    public int getMarkCount() {
        return markSet.size();
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public String getSourceCodeSlice() {
        return this.code;
    }

    public void setSourceCodeSlice(String code) {
        this.code = code;
    }

    public Iterator iterator() {
        return markSet.iterator();
    }

    public int compareTo(Object o) {
        Match other = (Match) o;
        int diff = other.getTokenCount() - getTokenCount();
        if (diff != 0) {
            return diff;
        }
        return other.getFirstMark().getIndex() - getFirstMark().getIndex();
    }
    
    public TokenEntry getFirstMark() {
        return marks[0];
    }
    
    public TokenEntry getSecondMark() {
        return marks[1];
    }

    public String toString() {
        return "Match:\r\ntokenCount = " + tokenCount + "\r\nmarks = " + markSet.size();
    }

    public Set getMarkSet() {
        return markSet;
    }

    public MatchCode getMatchCode() {
        if (mc == null) {
            mc = new MatchCode(marks[0], marks[1]);
        }
        return mc;
    }
    
    public int getEndIndex() {
        return marks[1].getIndex() + getTokenCount() -1;
    }

    public void setMarkSet(Set markSet) {
        this.markSet = markSet;
    }

}