/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Comparator;
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
    private String label;
    
    public static final Comparator MatchesComparator = new Comparator() {
    	public int compare(Object a, Object b) {
    		Match ma = (Match)a;
    		Match mb = (Match)b;
    		return mb.getMarkCount() - ma.getMarkCount();
    	}
    };
    
    public static final Comparator LinesComparator = new Comparator() {
    	public int compare(Object a, Object b) {
    		Match ma = (Match)a;
    		Match mb = (Match)b;
    		
    		return mb.getLineCount() - ma.getLineCount();
    	}
    };
    
    public static final Comparator LabelComparator = new Comparator() {
    	public int compare(Object a, Object b) {
    		Match ma = (Match)a;
    		Match mb = (Match)b;
    		if (ma.getLabel() == null) return 1;
    		if (mb.getLabel() == null) return -1;
    		return mb.getLabel().compareTo(ma.getLabel());
    	}
    };
    
    public static final Comparator LengthComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            Match m1 = (Match) o1;
            Match m2 = (Match) o2;
            return m2.getLineCount() - m1.getLineCount();
        }
    };
    
    public static class MatchCode {

        private int first;
        private int second;

        public MatchCode() {
        }

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
        return "Match: " + PMD.EOL + "tokenCount = " + tokenCount + PMD.EOL + "marks = " + markSet.size();
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
        return marks[1].getIndex() + getTokenCount() - 1;
    }

    public void setMarkSet(Set markSet) {
        this.markSet = markSet;
    }

    public void setLabel(String aLabel) {
    	label = aLabel;
    }
    
    public String getLabel() {
    	return label;
    }
}