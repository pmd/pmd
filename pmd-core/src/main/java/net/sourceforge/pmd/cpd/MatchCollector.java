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

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;

class MatchCollector {

    private final Map<Integer, List<Match>> matchTree = new TreeMap<>();

    private final Map<Integer, Set<Integer>> tokenMatchSets = new HashMap<>();

    private final MatchAlgorithm ma;

    MatchCollector(MatchAlgorithm ma) {
        this.ma = ma;
    }

    public void collect(List<SmallTokenEntry> marks) {
        // first get a pairwise collection of all maximal matches
        int skipped;
        for (int i = 0; i < marks.size() - 1; i += skipped + 1) {
            skipped = 0;
            SmallTokenEntry mark1 = marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                SmallTokenEntry mark2 = marks.get(j);
                boolean sameFile = mark1.fileId == mark2.fileId;
                int diff = mark1.indexInFile - mark2.indexInFile;
                if (sameFile && -diff < ma.getMinimumTileSize()) {
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
                int dupes = ma.countDupTokens(mark1, mark2);
                if (dupes < ma.getMinimumTileSize()) {
                    continue;
                }
                // both blocks overlap
                if (sameFile && diff + dupes >= 1) {
                    continue;
                }

                reportMatch(ma.toTokenEntry(mark1), ma.toTokenEntry(mark2), dupes);
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
        if (tokenMatchSets.computeIfAbsent(mark1.getIndex(), HashSet::new).contains(mark2.getIndex())) {
            return;
        }

        // This may not be a "new match", but actually a sub-match of a larger one.
        // always rely on the lowest mark index, as that's the order in which process them
        final int lowestKey = tokenMatchSets.get(mark1.getIndex()).stream().reduce(mark1.getIndex(), Math::min);

        List<Match> matches = matchTree.computeIfAbsent(lowestKey, ArrayList::new);
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
        tokenMatchSets.computeIfAbsent(mark1.getIndex(), HashSet::new).add(mark2.getIndex());
        tokenMatchSets.computeIfAbsent(mark2.getIndex(), HashSet::new).add(mark1.getIndex());
    }

    List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        matchTree.values().forEach(matches::addAll);
        return matches;
    }

    private boolean hasPreviousDupe(SmallTokenEntry mark1, SmallTokenEntry mark2) {
        return ma.tokensMatch(mark1, mark2, -1);
    }

}
