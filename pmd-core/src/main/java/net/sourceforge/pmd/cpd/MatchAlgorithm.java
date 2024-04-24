/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

class MatchAlgorithm {

    private static final int MOD = 37;

    private final TokenFileSet tokens;
    private final int minTileSize;

    MatchAlgorithm(TokenFileSet tokens, int minTileSize) {
        this.tokens = tokens;
        this.minTileSize = minTileSize;
    }

    boolean tokensMatch(TokenFileSet.SmallTokenEntry fst, TokenFileSet.SmallTokenEntry snd, int offset) {
       return tokens.countDupTokens(fst, snd, offset, 1) == 1;
    }
    int countDupTokens(TokenFileSet.SmallTokenEntry fst, TokenFileSet.SmallTokenEntry snd) {
       return tokens.countDupTokens(fst, snd, 0, Integer.MAX_VALUE);
    }

    public int getMinimumTileSize() {
        return this.minTileSize;
    }

    public List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager) {
        MatchCollector matchCollector = new MatchCollector(this);
        {
            cpdListener.phaseUpdate(CPDListener.HASH);
            Map<?, Object> markGroups = tokens.hashAll(minTileSize, MOD);

            cpdListener.phaseUpdate(CPDListener.MATCH);
            markGroups.values()
                      .stream()
                      .filter(it -> it instanceof List)
                      .forEach(it -> {
                          @SuppressWarnings("unchecked")
                          List<TokenFileSet.SmallTokenEntry> l = (List<TokenFileSet.SmallTokenEntry>) it;
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

     TokenEntry toTokenEntry(TokenFileSet.SmallTokenEntry entry) {
        return tokens.toTokenEntry(entry);
    }

}
