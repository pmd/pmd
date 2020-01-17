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

    private static final int MOD = 37;
    private int lastHash;
    private int lastMod = 1;

    private List<Match> matches;
    private Map<String, SourceCode> source;
    private Tokens tokens;
    private List<TokenEntry> code;
    private CPDListener cpdListener;
    private int min;

    public MatchAlgorithm(Map<String, SourceCode> sourceCode, Tokens tokens, int min) {
        this(sourceCode, tokens, min, new CPDNullListener());
    }

    public MatchAlgorithm(Map<String, SourceCode> sourceCode, Tokens tokens, int min, CPDListener listener) {
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

    public Iterator<Match> matches() {
        return matches.iterator();
    }

    public TokenEntry tokenAt(int offset, TokenEntry m) {
        return code.get(offset + m.getIndex());
    }

    public int getMinimumTileSize() {
        return this.min;
    }

    public void findMatches() {
        cpdListener.phaseUpdate(CPDListener.HASH);
        Map<TokenEntry, Object> markGroups = hash();

        cpdListener.phaseUpdate(CPDListener.MATCH);
        MatchCollector matchCollector = new MatchCollector(this);
        for (Iterator<Object> i = markGroups.values().iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof List) {
                @SuppressWarnings("unchecked")
                List<TokenEntry> l = (List<TokenEntry>) o;
                Collections.reverse(l);
                matchCollector.collect(l);
            }
            i.remove();
        }
        cpdListener.phaseUpdate(CPDListener.GROUPING);
        matches = matchCollector.getMatches();

        for (Match match : matches) {
            for (Mark mark : match) {
                TokenEntry token = mark.getToken();
                int lineCount = tokens.getLineCount(token, match);
                TokenEntry endToken = tokens.getEndToken(token, match);

                mark.setLineCount(lineCount);
                mark.setEndToken(endToken);
                SourceCode sourceCode = source.get(token.getTokenSrcID());
                mark.setSourceCode(sourceCode);
            }
        }
        cpdListener.phaseUpdate(CPDListener.DONE);
    }

    @SuppressWarnings("PMD.JumbledIncrementer")
    private Map<TokenEntry, Object> hash() {
        Map<TokenEntry, Object> markGroups = new HashMap<>(tokens.size());
        for (int i = code.size() - 1; i >= 0; i--) {
            TokenEntry token = code.get(i);
            if (token != TokenEntry.EOF) {
                int last = tokenAt(min, token).getIdentifier();
                lastHash = MOD * lastHash + token.getIdentifier() - lastMod * last;
                token.setHashCode(lastHash);
                Object o = markGroups.get(token);

                // Note that this insertion method is worthwhile since the vast
                // majority
                // markGroup keys will have only one value.
                if (o == null) {
                    markGroups.put(token, token);
                } else if (o instanceof TokenEntry) {
                    List<TokenEntry> l = new ArrayList<>();
                    l.add((TokenEntry) o);
                    l.add(token);
                    markGroups.put(token, l);
                } else {
                    @SuppressWarnings("unchecked")
                    List<TokenEntry> l = (List<TokenEntry>) o;
                    l.add(token);
                }
            } else {
                lastHash = 0;
                for (int end = Math.max(0, i - min + 1); i > end; i--) {
                    token = code.get(i - 1);
                    lastHash = MOD * lastHash + token.getIdentifier();
                    if (token == TokenEntry.EOF) {
                        break;
                    }
                }
            }
        }
        return markGroups;
    }
}
