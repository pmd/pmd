/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;

import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongObjectMap;

class MatchCollector {

    private final Map<Long, List<Match>> matchTree = new ConcurrentHashMap<>();

    /**
     * Determine the matches in the list of token entries, given that all these token entries have the same hash.
     *
     * @param marks       List of tokens
     * @param tokens      Token store
     * @param minTileSize Tile size used for hashing
     */
    void collect(List<SmallTokenEntry> marks, TokenFileSet tokens, int minTileSize) {
        assert marks.stream().mapToInt(it -> it.prevToken).distinct().count() == marks.size()
            : "By construction the items of a mark list should have distinct previous tokens";
        marks.sort(Comparator.naturalOrder());

        StringBuilder sb = new StringBuilder();
        final LongObjectMap<SortedSet<Long>> tokenMatchSets = new LongObjectHashMap<>();
        // first get a pairwise collection of all maximal matches
        int skipped;
        for (int i = 0; i < marks.size() - 1; i += skipped + 1) {
            skipped = 0;
            for (int j = 0; j <= i; j++) {
                sb.append("0,");
            }
            SmallTokenEntry mark1 = marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                SmallTokenEntry mark2 = marks.get(j);
                // todo move that down
                int dupes = tokens.countDupTokens(mark1, mark2);
                boolean sameFile = mark1.fileId == mark2.fileId;
                int diff = mark1.indexInFile - mark2.indexInFile;
                sb.append(dupes).append(',');
                if (sameFile && -diff < minTileSize) {
                    // self-repeating sequence such as ABBABBABB with min 6,
                    // will match 2 against any other occurrence of ABBABB
                    // avoid duplicate overlapping reports by skipping it on the next outer loop
                    //skipped++;
                    continue;
                }

                // "match too small" check
                //int dupes = tokens.countDupTokens(mark1, mark2);
                if (dupes < minTileSize) {
                    continue;
                }
                // both blocks overlap
                if (sameFile && diff + dupes >= 1) {
                    continue;
                }

                reportMatch(tokens.toTokenEntry(mark1), tokens.toTokenEntry(mark2), dupes, tokenMatchSets, tokens);
            }
            sb.append('\n');
        }
        if (marks.size() > 2) {
            System.out.print(sb);
            System.out.println("-----");
        }
    }

    private void reportMatch(TokenEntry mark1, TokenEntry mark2, int dupes, LongObjectMap<SortedSet<Long>> tokenMatchSets, TokenFileSet tokens) {
        /*
         * Check if the match is previously know. This can happen when a snippet is duplicated more than once.
         * If A, B and C are identical snippets, MatchAlgorithm will find the matching pairs:
         *  - AB
         *  - AC
         *  - BC
         * It should be reduced to a single match with 3 marks
         */
        SortedSet<Long> curMatchSet = computeIfAbsent(mark1.getIndex(), tokenMatchSets);
        if (curMatchSet.contains(mark2.getIndex())) {
            return;
        }

        // This may not be a "new match", but actually a sub-match of a larger one.
        // always rely on the lowest mark index, as that's the order in which process them
        final long lowestKey = curMatchSet.isEmpty() ? mark1.getIndex() : curMatchSet.first();

        List<Match> matches = matchTree.computeIfAbsent(lowestKey, k -> new ArrayList<>(1));
        synchronized (matches) {

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
                    if (matchContains(otherEnd, mark2, m.getTokenCount(), dupes)) {
                        // this match is embedded in the previous one… ignore it.
                        return;
                    } else if (matchContains(mark2, otherEnd, dupes, m.getTokenCount())) {
                        // the new match is longer and overlaps with the old one - replace it
                        matchIterator.remove();
                        break;
                    } else if (dupes == m.getTokenCount()) {
                        // we found yet another exact match of the same snippet. Roll it together

                        // Add this adjacency to all combinations
                        m.iterator().forEachRemaining(other -> registerTokenMatch(other.getToken(), mark2, tokenMatchSets));

                        m.addMark(makeMark(mark2, dupes, tokens));
                        return;
                    }
                }
            }
            // this is a new match, add it
            Match match = new Match(dupes, makeMark(mark1, dupes, tokens), makeMark(mark2, dupes, tokens));
            matches.add(match);
        }


        // add matches in both directions
        registerTokenMatch(mark1, mark2, tokenMatchSets);
    }
    private boolean matchContains(TokenEntry mark1, TokenEntry mark2, int tokenCount1, int tokenCount2) {
        return mark1.getFileId() == mark2.getFileId()
            && mark1.getLocalIndex() < mark2.getLocalIndex()
            && mark1.getLocalIndex() + tokenCount1 >= mark2.getLocalIndex() + tokenCount2;
    }

    private Mark makeMark(TokenEntry token, int matchLen, TokenFileSet tokens) {
        Mark mark = new Mark(token);
        TokenEntry endToken = tokens.getEndToken(token, matchLen);
        mark.setEndToken(endToken);
        return mark;
    }


    private SortedSet<Long> computeIfAbsent(long key, LongObjectMap<SortedSet<Long>> tokenMatchSets) {
        int i = tokenMatchSets.indexOf(key);
        if (tokenMatchSets.indexExists(i)) {
            return tokenMatchSets.indexGet(i);
        }
        SortedSet<Long> value = new TreeSet<>(Comparator.naturalOrder());
        tokenMatchSets.indexInsert(i, key, value);
        return value;
    }

    private void registerTokenMatch(TokenEntry mark1, TokenEntry mark2, LongObjectMap<SortedSet<Long>> tokenMatchSets) {
        computeIfAbsent(mark1.getIndex(), tokenMatchSets).add(mark2.getIndex());
        computeIfAbsent(mark2.getIndex(), tokenMatchSets).add(mark1.getIndex());
    }

    List<Match> getMatches() {
        List<Match> matches = new ArrayList<>();
        matchTree.values().forEach(matches::addAll);
        matches.sort(Comparator.naturalOrder());
        return matches;
    }

}
