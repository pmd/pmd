/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Match implements Comparable<Match> {

    private int tokenCount;
    private Set<Mark> markSet = new TreeSet<>();
    private String label;
    
    public static final Comparator<Match> MATCHES_COMPARATOR = new Comparator<Match>() {
    	public int compare(Match ma, Match mb) {
    		return mb.getMarkCount() - ma.getMarkCount();
    	}
    };
    
    public static final Comparator<Match> LINES_COMPARATOR = new Comparator<Match>() {
    	public int compare(Match ma, Match mb) {
    		return mb.getLineCount() - ma.getLineCount();
    	}
    };
    
    public static final Comparator<Match> LABEL_COMPARATOR = new Comparator<Match>() {
    	public int compare(Match ma, Match mb) {
    		if (ma.getLabel() == null) {
    		    return 1;
    		}
    		if (mb.getLabel() == null) {
    		    return -1;
    		}
    		return mb.getLabel().compareTo(ma.getLabel());
    	}
    };
    
    public static final Comparator<Match> LENGTH_COMPARATOR = new Comparator<Match>() {
        public int compare(Match ma, Match mb) {
            return mb.getLineCount() - ma.getLineCount();
        }
    };

    public Match(int tokenCount, Mark first, Mark second) {
        markSet.add(first);
        markSet.add(second);
        this.tokenCount = tokenCount;
    }

    public Match(int tokenCount, TokenEntry first, TokenEntry second) {
        this(tokenCount, new Mark(first), new Mark(second));
    }

    public int getMarkCount() {
        return markSet.size();
    }

    public int getLineCount() {
        return getMark(0).getLineCount();
    }

    public int getTokenCount() {
        return this.tokenCount;
    }

    public String getSourceCodeSlice() {
        return this.getMark(0).getSourceCodeSlice();
    }

    public Iterator<Mark> iterator() {
        return markSet.iterator();
    }

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

    public String toString() {
        return "Match: " + PMD.EOL + "tokenCount = " + tokenCount + PMD.EOL + "marks = " + markSet.size();
    }

    public Set<Mark> getMarkSet() {
        return markSet;
    }

    public int getEndIndex() {
        return getMark(0).getToken().getIndex() + getTokenCount() - 1;
    }

    public void setMarkSet(Set<Mark> markSet) {
        this.markSet = markSet;
    }

    public void setLabel(String aLabel) {
    	label = aLabel;
    }
    
    public String getLabel() {
    	return label;
    }
    
    public void addTokenEntry(TokenEntry entry){
        markSet.add(new Mark(entry));                
    }
    
    private Mark getMark(int index) {
        Mark result = null;
        int i = 0;
        for (Iterator<Mark> it = markSet.iterator(); it.hasNext() && i < index + 1; ){            
            result = it.next();
            i++;
        }
        return result;
    }
}