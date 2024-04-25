/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;
import net.sourceforge.pmd.lang.document.FileId;

import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntSet;
import com.carrotsearch.hppc.cursors.IntCursor;

class MatchCollector {

    private final Map<Integer, List<Match>> matchTree = new ConcurrentHashMap<>();
    /**
     * Determine the matches in the list of token entries, given that all these token entries have the same hash.
     *
     * @param marks       List of tokens
     * @param tokens      Token store
     * @param minTileSize Tile size used for hashing
     */
    void collect(List<SmallTokenEntry> marks, TokenFileSet tokens, int minTileSize) {
        final IntObjectMap<IntSet> tokenMatchSets = new IntObjectHashMap<>();
        // first get a pairwise collection of all maximal matches
        long start = System.currentTimeMillis();
        int skipped;
        for (int i = 0; i < marks.size() - 1; i += skipped + 1) {
            skipped = 0;
            SmallTokenEntry mark1 = marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                SmallTokenEntry mark2 = marks.get(j);
                boolean sameFile = mark1.fileId == mark2.fileId;
                int diff = mark1.indexInFile - mark2.indexInFile;
                if (sameFile && -diff < minTileSize) {
                    // self-repeating sequence such as ABBABBABB with min 6,
                    // will match 2 against any other occurrence of ABBABB
                    // avoid duplicate overlapping reports by skipping it on the next outer loop
                    skipped++;
                    continue;
                }
                if (tokens.isPreviousTokenEqual(mark1, mark2)) {
                    continue;
                }

                // "match too small" check
                int dupes = tokens.countDupTokens(mark1, mark2);
                if (dupes < minTileSize) {
                    continue;
                }
                // both blocks overlap
                if (sameFile && diff + dupes >= 1) {
                    continue;
                }

                reportMatch(tokens.toTokenEntry(mark1), tokens.toTokenEntry(mark2), dupes, tokenMatchSets, tokens);
            }
        }
        long totalTimeMilli = System.currentTimeMillis() - start;
        if (totalTimeMilli > 1000) {
            String files = marks.stream().mapToInt(it -> it.fileId).distinct().mapToObj(tokens::getFileId).map(FileId::getAbsolutePath).collect(Collectors.joining("\n"));
            System.out.println("Time: " + totalTimeMilli + " marks " + marks.size() + " tfset:\n" + files);
        }
    }

    private void reportMatch(TokenEntry mark1, TokenEntry mark2, int dupes, @Nullable IntObjectMap<IntSet> tokenMatchSets, TokenFileSet tokens) {
        /*
         * Check if the match is previously know. This can happen when a snippet is duplicated more than once.
         * If A, B and C are identical snippets, MatchAlgorithm will find the matching pairs:
         *  - AB
         *  - AC
         *  - BC
         * It should be reduced to a single match with 3 marks
         */
        IntSet curMatchSet = computeIfAbsent(mark1.getIndex(), tokenMatchSets);
        if (curMatchSet.contains(mark2.getIndex())) {
            return;
        }

        // This may not be a "new match", but actually a sub-match of a larger one.
        // always rely on the lowest mark index, as that's the order in which process them
        final int lowestKey = min(curMatchSet, mark1.getIndex());

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

    private Mark makeMark(TokenEntry token, int matchLen, TokenFileSet tokens) {
        Mark mark = new Mark(token);
        TokenEntry endToken = tokens.getEndToken(token, matchLen);
        mark.setEndToken(endToken);
        return mark;
    }


    private int min(IntSet set, int start) {
        int min = start;
        for (IntCursor intCursor : set) {
            if (intCursor.value < min)
                min = intCursor.value;
        }
        return min;
    }

    static final IntSet EMPTY = new IntHashSet();

    private IntSet computeIfAbsent(int key, @Nullable IntObjectMap<IntSet> tokenMatchSets) {
        if (tokenMatchSets == null) {
            return EMPTY;
        }
        int i = tokenMatchSets.indexOf(key);
        if (tokenMatchSets.indexExists(i))
            return tokenMatchSets.indexGet(i);
        IntHashSet value = new IntHashSet();
        tokenMatchSets.indexInsert(i, key, value);
        return value;
    }

    private void registerTokenMatch(TokenEntry mark1, TokenEntry mark2, IntObjectMap<IntSet> tokenMatchSets) {
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
