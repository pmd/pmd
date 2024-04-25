/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

class MatchAlgorithm {

    private static final int MOD = 37;

    MatchAlgorithm() {
    }


    static List<Match> findMatches(@NonNull CPDListener cpdListener, SourceManager sourceManager, TokenFileSet tokens, int minTileSize) {
        MatchCollector matchCollector = new MatchCollector();
        cpdListener.phaseUpdate(CPDListener.HASH);
        List<List<TokenFileSet.SmallTokenEntry>> markGroups = tokens.hashAll(minTileSize, MOD);
        System.gc();

        cpdListener.phaseUpdate(CPDListener.MATCH);
        markGroups.stream().forEach(group -> {
            matchCollector.collect(group, tokens, minTileSize);
        });

        cpdListener.phaseUpdate(CPDListener.DONE);
        return matchCollector.getMatches();
    }

}
