/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MatchAlgorithm {

    private final static int MOD = 37;
    private int lastHash;
    private int lastMod = 1;
    
    private List matches;
    private Map source;
    private Tokens tokens;
    private List code;
    private CPDListener cpdListener;
    private int min;

    public MatchAlgorithm(Map sourceCode, Tokens tokens, int min) {
        this(sourceCode, tokens, min, new CPDNullListener());
    }

    public MatchAlgorithm(Map sourceCode, Tokens tokens, int min, CPDListener listener) {
        this.source = sourceCode;
        this.tokens = tokens;
        this.code = tokens.getTokens();
        this.min = min;
        this.cpdListener = listener;
        for (int i = 0; i < min; i++) {
            lastMod *= MOD;
        }
    }

    public void setListener(CPDListener listener) {
        this.cpdListener = listener;
    }

    public void findMatches() {
        cpdListener.phaseUpdate(CPDListener.HASH);
        Map markGroups = hash();

        cpdListener.phaseUpdate(CPDListener.MATCH);
        MatchCollector coll = new MatchCollector(this);
        for (Iterator i = markGroups.values().iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof List) {
                Collections.reverse((List) o);
                coll.collect(min, (List) o);
            }
            i.remove();
        }

        cpdListener.phaseUpdate(CPDListener.GROUPING);
        matches = coll.getMatches();
        coll = null;

        for (Iterator i = matches(); i.hasNext();) {
            Match match = (Match) i.next();
            for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
                TokenEntry mark = (TokenEntry) occurrences.next();
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

    private Map hash() {
        Map markGroups = new HashMap(tokens.size());
        for (int i = code.size() - 1; i >= 0; i--) {
            TokenEntry token = (TokenEntry) code.get(i);
            if (token != TokenEntry.EOF) {
                int last = tokenAt(min, token).getIdentifier();
                lastHash = MOD * lastHash + token.getIdentifier() - lastMod * last;
				token.setHashCode(lastHash);
                Object o = markGroups.get(token);
                if (o == null) {
                    markGroups.put(token, token);
                } else if (o instanceof TokenEntry) {
                    List l = new ArrayList();
                    l.add(o);
                    l.add(token);
                    markGroups.put(token, l);
                } else {
                    List l = (List) o;
                    l.add(token);
                }
            } else {
                lastHash = 0;
                for (int end = Math.max(0, i - min + 1); i > end; i--) {
                    token = (TokenEntry) code.get(i - 1);
                    lastHash = MOD * lastHash + token.getIdentifier();
                    if (token == TokenEntry.EOF) {
                        break;
                    }
                }
            }
        }
        return markGroups;
    }

    public Iterator matches() {
        return matches.iterator();
    }

    public TokenEntry tokenAt(int offset, TokenEntry m) {
        return (TokenEntry) code.get(offset + m.getIndex());
    }
    
}
