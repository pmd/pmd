/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Comparator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

class MatchAlgorithm {

    private static final int MOD = 37;

    MatchAlgorithm() {
    }


    static List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager, TokenFileSet tokens, int minTileSize) {
        MatchCollector matchCollector = new MatchCollector();
        {
            cpdListener.phaseUpdate(CPDListener.HASH);
            List<List<TokenFileSet.SmallTokenEntry>> markGroups = tokens.hashAll(minTileSize, MOD);
            System.gc();

            cpdListener.phaseUpdate(CPDListener.MATCH);
            markGroups.stream().parallel().forEach(group -> matchCollector.collect(group, tokens, minTileSize));
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

}
