/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MatchAlgorithm {

    private Map pool; 
    private List code; 
    private List marks;
    private List matches;
    private Map source;
    private Tokens tokens;
    private CPDListener cpdListener;
    private int min;
    private int fileCount;
    
	public MatchAlgorithm(Map sourceCode, Tokens tokens, int min) {
	    this(sourceCode, tokens, min, new CPDNullListener());
	}

    public MatchAlgorithm(Map sourceCode, Tokens tokens, int min, CPDListener listener) {
        this.source = sourceCode;
        this.tokens = tokens;
        this.min = min;
        this.cpdListener = listener;
        code = new ArrayList(tokens.size());
        pool = new HashMap(Math.max(16,tokens.size()/10));
        marks = new ArrayList(tokens.size());
        cpdListener.phaseUpdate(CPDListener.INIT);
		for (Iterator i = tokens.iterator(); i.hasNext();) {
			add((TokenEntry) i.next());
		}
    }

    public void setListener(CPDListener listener) {
        this.cpdListener = listener;
    }

    private void add(TokenEntry token) {
        TokenEntry fw = (TokenEntry)pool.get(token);
        if (fw == null) {
            pool.put(token, token);
            fw = token;
        }
        if (token != TokenEntry.EOF) {
			fileCount++;
            Mark m = new Mark(code.size(), token.getTokenSrcID(), token.getBeginLine());
            marks.add(m);
        } else {
            //filter meaningless marks
			for (int i = 0; i < min - 1 && i < fileCount && marks.size() > 0; i++) {
                marks.remove(marks.size() - 1);
            }
			fileCount = 0;
        }
        code.add(fw);
    }

    public void findMatches() {
        int count = 0;
        for (Iterator iter = pool.keySet().iterator(); iter.hasNext();) {
            TokenEntry token = (TokenEntry) iter.next();
            token.setIdentifier(count++);
        }
        cpdListener.phaseUpdate(CPDListener.HASH);
		//compute the rolling hash
        Map markGroups = new HashMap(marks.size()/2);
		RollingHash rcs = new RollingHash(min, this);
		for (Iterator i = marks.iterator(); i.hasNext();) {
		    Mark m = (Mark)i.next();
		    rcs.compute(m);
		    List l = (ArrayList)markGroups.get(m);
		    if (l == null) {
		        l = new ArrayList();
		        markGroups.put(m, l);
		    }
		    l.add(m);
		}
		marks.clear();
		
		cpdListener.phaseUpdate(CPDListener.MATCH);
		MatchCollector coll = new MatchCollector(this);						
		for (Iterator i = markGroups.values().iterator(); i.hasNext();) {
		    List l = (ArrayList)i.next();
		    if (l.size() > 1) {
		        coll.collect(min, l);
		    }
		    i.remove();
		}

		cpdListener.phaseUpdate(CPDListener.GROUPING);
		matches = coll.getMatches();
		coll = null;
		
        for (Iterator i = matches(); i.hasNext();) {
            Match match = (Match) i.next();
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                Mark mark = (Mark) occurrences.next();
                match.setLineCount(tokens.getLineCount(mark, match));
                if (!occurrences.hasNext()) {
                    int start = mark.getBeginLine();
                    int end = start + match.getLineCount() - 1;
                    SourceCode sourceCode = (SourceCode) source.get(mark.getTokenSrcID());
                    match.setSourceCodeSlice(sourceCode.getSlice(start, end));
                }
            }
        }
        cpdListener.phaseUpdate(CPDListener.DONE);
    }

    public Iterator matches() {
        return matches.iterator();
    }
    
	public TokenEntry tokenAt(int offset, Mark m) {
		return (TokenEntry) code.get(offset + m.getIndexIntoTokenArray());
	}

}
