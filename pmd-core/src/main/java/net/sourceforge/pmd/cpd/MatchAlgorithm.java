/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

class MatchAlgorithm {

    private static final int MOD = 37;

    private final TokenFileSet tokens;
    private final int minTileSize;

    MatchAlgorithm(TokenFileSet tokens, int minTileSize) {
        this.tokens = tokens;
        this.minTileSize = minTileSize;
    }

    boolean previousTokenEquals(TokenFileSet.SmallTokenEntry fst, TokenFileSet.SmallTokenEntry snd) {
//       return tokens.isPreviousTokenEqual(fst,snd);
        return fst.prevToken == snd.prevToken;
    }
    int countDupTokens(TokenFileSet.SmallTokenEntry fst, TokenFileSet.SmallTokenEntry snd) {
       return tokens.countDupTokens(fst, snd);
    }

    public int getMinimumTileSize() {
        return this.minTileSize;
    }

    public List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager) {
        MatchCollector matchCollector = new MatchCollector(this);
        {
            cpdListener.phaseUpdate(CPDListener.HASH);
            List<List<TokenFileSet.SmallTokenEntry>> markGroups = tokens.hashAll(minTileSize, MOD);

            cpdListener.phaseUpdate(CPDListener.MATCH);
            markGroups.stream().parallel().forEach(matchCollector::collect);
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
