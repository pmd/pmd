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
    private int lineCount;
    private Set<TokenEntry> markSet = new TreeSet<TokenEntry>();    
    private String code;    
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

    public Match(int tokenCount, TokenEntry first, TokenEntry second) {
        markSet.add(first);
        markSet.add(second);
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

    public Iterator<TokenEntry> iterator() {
        return markSet.iterator();
    }

    public int compareTo(Match other) {
        int diff = other.getTokenCount() - getTokenCount();
        if (diff != 0) {
            return diff;
        }
        return getFirstMark().getIndex() - other.getFirstMark().getIndex();
    }

    public TokenEntry getFirstMark() {
        return getMark(0);
    }

    public TokenEntry getSecondMark() {
        return getMark(1);
    }

    public String toString() {
        return "Match: " + PMD.EOL + "tokenCount = " + tokenCount + PMD.EOL + "marks = " + markSet.size();
    }

    public Set<TokenEntry> getMarkSet() {
        return markSet;
    }

    public int getEndIndex() {
        return getMark(0).getIndex() + getTokenCount() - 1;
    }

    public void setMarkSet(Set<TokenEntry> markSet) {
        this.markSet = markSet;
    }

    public void setLabel(String aLabel) {
    	label = aLabel;
    }
    
    public String getLabel() {
    	return label;
    }
    
    public void addTokenEntry(TokenEntry entry){
        markSet.add(entry);                
    }
    
    private TokenEntry getMark(int index) {
        TokenEntry result = null;
        int i = 0;
        for (Iterator<TokenEntry> it = markSet.iterator(); it.hasNext() && i < index + 1; ){            
            result = it.next();
            i++;
        }
        return result;
    }
}