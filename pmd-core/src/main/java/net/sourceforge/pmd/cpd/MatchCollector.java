/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class MatchCollector {

    private final Map<Integer, List<Match>> matchTree = new TreeMap<>();

    private final Map<Integer, Set<Integer>> tokenMatchSets = new HashMap<>();

    private final MatchAlgorithm ma;

    MatchCollector(MatchAlgorithm ma) {
        this.ma = ma;
    }

    public void collect(List<TokenEntry> marks) {
        // first get a pairwise collection of all maximal matches
        int skipped;
        for (int i = 0; i < marks.size() - 1; i += skipped + 1) {
            skipped = 0;
            TokenEntry mark1 = marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                TokenEntry mark2 = marks.get(j);
                int diff = mark1.getIndex() - mark2.getIndex();
                if (-diff < ma.getMinimumTileSize()) {
                    // self-repeating sequence such as ABBABBABB with min 6,
                    // will match 2 against any other occurrence of ABBABB
                    // avoid duplicate overlapping reports by skipping it on the next outer loop
                    skipped++;
                    continue;
                }
                if (hasPreviousDupe(mark1, mark2)) {
                    continue;
                }

                // "match too small" check
                int dupes = countDuplicateTokens(mark1, mark2);
                if (dupes < ma.getMinimumTileSize()) {
                    continue;
                }
                // both blocks overlap
                if (diff + dupes >= 1) {
                    continue;
                }
                reportMatch(mark1, mark2, dupes);
            }
        }
    }

    private void reportMatch(TokenEntry mark1, TokenEntry mark2, int dupes) {
        /*
         * Check if the match is previously know. This can happen when a snippet is duplicated more than once.
         * If A, B and C are identical snippets, MatchAlgorithm will find the matching pairs:
         *  - AB
         *  - AC
         *  - BC
         * It should be reduced to a single match with 3 marks
         */
        if (tokenMatchSets.computeIfAbsent(mark1.getIndex(), (i) -> new HashSet<>()).contains(mark2.getIndex())) {
            return;
        }

        // This may not be a "new match", but actually a sub-match of a larger one.
        // always rely on the lowest mark index, as that's the order in which process them
        final int lowestKey = tokenMatchSets.get(mark1.getIndex()).stream().reduce(mark1.getIndex(), Math::min);

        List<Match> matches = matchTree.computeIfAbsent(lowestKey, (i) -> new ArrayList<>());
        Iterator<Match> matchIterator = matches.iterator();
        while (matchIterator.hasNext()) {
            Match m = matchIterator.next();

            // Check all other marks
            for (Mark otherMark : m.getMarkSet()) {
                TokenEntry otherEnd = otherMark.getToken();
                if (otherEnd.getIndex() == mark1.getIndex()) {
                    continue;
                }

                // does the new match supersedes this one?
                if (otherEnd.getIndex() < mark2.getIndex() && otherEnd.getIndex() + m.getTokenCount() >= mark2.getIndex() + dupes) {
                    // this match is embedded in the previous one… ignore it.
                    return;
                } else if (mark2.getIndex() < otherEnd.getIndex() && mark2.getIndex() + dupes >= otherEnd.getIndex() + m.getTokenCount()) {
                    // the new match is longer and overlaps with the old one - replace it
                    matchIterator.remove();
                    break;
                } else if (dupes == m.getTokenCount()) {
                    // we found yet another exact match of the same snippet. Roll it together

                    // Add this adjacency to all combinations
                    m.iterator().forEachRemaining(other -> registerTokenMatch(other.getToken(), mark2));

                    m.addMark(mark2);
                    return;
                }
            }
        }

        // this is a new match, add it
        matches.add(new Match(dupes, mark1, mark2));

        // add matches in both directions
        registerTokenMatch(mark1, mark2);
    }

    private void registerTokenMatch(TokenEntry mark1, TokenEntry mark2) {
        tokenMatchSets.computeIfAbsent(mark1.getIndex(), (i) -> new HashSet<>()).add(mark2.getIndex());
        tokenMatchSets.computeIfAbsent(mark2.getIndex(), (i) -> new HashSet<>()).add(mark1.getIndex());
    }

    List<Match> getMatches() {
        return matchTree.values().stream().reduce(new ArrayList<>(), (acc, matches) -> {
            acc.addAll(matches);
            return acc;
        });
    }

    private boolean hasPreviousDupe(TokenEntry mark1, TokenEntry mark2) {
        return mark1.getIndex() != 0 && !matchEnded(ma.tokenAt(-1, mark1), ma.tokenAt(-1, mark2));
    }

    private int countDuplicateTokens(TokenEntry mark1, TokenEntry mark2) {
        int index = 0;
        while (!matchEnded(ma.tokenAt(index, mark1), ma.tokenAt(index, mark2))) {
            index++;
        }
        return index;
    }

    private boolean matchEnded(TokenEntry token1, TokenEntry token2) {
        return token1.getIdentifier() != token2.getIdentifier()
                || token1.isEof()
                || token2.isEof();
    }
}
