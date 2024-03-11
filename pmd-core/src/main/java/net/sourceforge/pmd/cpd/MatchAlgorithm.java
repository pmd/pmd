/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

class MatchAlgorithm {

    private static final int MOD = 37;
    private int lastMod = 1;

    private final Tokens tokens;
    private final List<TokenEntry> code;
    private final int minTileSize;

    MatchAlgorithm(Tokens tokens, int minTileSize) {
        this.tokens = tokens;
        this.code = tokens.getTokens();
        this.minTileSize = minTileSize;
        for (int i = 0; i < minTileSize; i++) {
            lastMod *= MOD;
        }
    }


    public TokenEntry tokenAt(int offset, TokenEntry m) {
        return code.get(offset + m.getIndex());
    }

    public int getMinimumTileSize() {
        return this.minTileSize;
    }

    public List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager) {
        MatchCollector matchCollector = new MatchCollector(this);
        {
            cpdListener.phaseUpdate(CPDListener.HASH);
            Map<TokenEntry, Object> markGroups = hash();

            cpdListener.phaseUpdate(CPDListener.MATCH);
            markGroups.values()
                      .stream()
                      .filter(it -> it instanceof List)
                      .forEach(it -> {
                          @SuppressWarnings("unchecked")
                          List<TokenEntry> l = (List<TokenEntry>) it;
                          Collections.reverse(l);
                          matchCollector.collect(l);
                      });
            // put markGroups out of scope
        }

        cpdListener.phaseUpdate(CPDListener.GROUPING);
        List<Match> matches = matchCollector.getMatches();
        matches.sort(Comparator.naturalOrder());

        for (Match match : matches) {
            for (Mark mark : match) {
                TokenEntry token = mark.getToken();
                TokenEntry endToken = tokens.getEndToken(token, match);

                mark.setEndToken(endToken);
            }
        }
        cpdListener.phaseUpdate(CPDListener.DONE);
        return matches;
    }

    @SuppressWarnings("PMD.JumbledIncrementer")
    private Map<TokenEntry, Object> hash() {
        int lastHash = 0;
        Map<TokenEntry, Object> markGroups = new HashMap<>(tokens.size());
        for (int i = code.size() - 1; i >= 0; i--) {
            TokenEntry token = code.get(i);
            if (!token.isEof()) {
                int last = tokenAt(minTileSize, token).getIdentifier();
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
                for (int end = Math.max(0, i - minTileSize + 1); i > end; i--) {
                    token = code.get(i - 1);
                    lastHash = MOD * lastHash + token.getIdentifier();
                    if (token.isEof()) {
                        break;
                    }
                }
            }
        }
        return markGroups;
    }
}
