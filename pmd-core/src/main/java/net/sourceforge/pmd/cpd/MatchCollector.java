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
    private final SourceManager manager;
    private final TokenFileSet tokens;
    private final int minTileSize;

    MatchCollector(SourceManager sourceManager, TokenFileSet tokens, int minTileSize) {
        this.manager = sourceManager;
        this.tokens = tokens;
        this.minTileSize = minTileSize;
    }

    /**
     * Determine the matches in the list of token entries, given that all these token entries have the same hash.
     *
     * @param marks       List of tokens
     */
    void collect(List<SmallTokenEntry> marks) {
        assert marks.stream().mapToInt(it -> it.prevToken).distinct().count() == marks.size()
            : "By construction the items of a mark list should have distinct previous tokens";

        if (marks.size() < 2) {
            return;
        }
        final LongObjectMap<SortedSet<Long>> tokenMatchSets = new LongObjectHashMap<>();

        if (marks.size() == 2) {
            int dupes = tokens.countDupTokens(marks.get(0), marks.get(1), 0);
            handleDupCount(marks.get(0), marks.get(1), dupes, tokenMatchSets);
        }

        marks.sort(Comparator.naturalOrder());

        /*
            Compute the common prefix length of each token (mark) with all other tokens in the list.
            We have
                preflen(A, B) = preflen(B, A)
            and
                preflen(A,C) >= min(preflen(A,B), preflen(A,C))

            We can use these facts to avoid unnecessary work.

            Eg if A = "001", B = "000", C = "001", D = "010"
            Then we have the following table for preflen:

                 A  B  C  D
            A    _  2  3  1
            B    _  _  2  1
            C    _  _  _  1
            D    _  _  _  _

            This is what we want to compute. How would you do it?

            The diagonal does not need to be computed, and the lower half neither because of symmetry.
            Now since we proceed row major we will first compute row A, that is preflen(A, x) for x in {B, C, D}.
            Given the second fact above, we know that preflen(B,C) >= min(preflen(A,B), preflen(B,C)) = min(2, 3) = 2.
            This is stored in the table off as a lower bound on preflen(B,C).
            When computing the second row, which is preflen(B,C), we can avoid checking the
            first two tokens of B and C as we know they have them in common with A, so they must have
            them in common together as well.
            At each row i, we use the first pair (i, i+1) as a pivot to compute the lower bounds for row i+1.

            In practice this optimization is very advantageous.
            If we enter this MatchCollector function, then the marks in the list have the same hash. Because of the
            way hashes are computed (taking tile size into account), it is very likely that tokens having
            the same hash have a preflen greater than the tile size. Since the tile size is routinely hundreds of tokens,
            we are saving that many unnecessary comparisons. Eg for perfect matches only one comparison is necessary.
            Also remember that this algorithm is still quadratic in the number of marks in the list, so speeding up
            each cell provides a very significant runtime improvement.

         */

        int[] off = new int[marks.size()];

        for (int i = 0; i < marks.size() - 1; i++) {
            SmallTokenEntry mark1 = marks.get(i);

            int firstDups = tokens.countDupTokens(marks.get(i), marks.get(i + 1), off[i + 1]);

            for (int j = i + 2; j < marks.size(); j++) {
                SmallTokenEntry mark2 = marks.get(j);

                int dupes = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);
                off[j] = Math.min(firstDups, dupes);

                handleDupCount(mark1, mark2, dupes, tokenMatchSets);
            }
        }
    }

    private void handleDupCount(SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes, LongObjectMap<SortedSet<Long>> tokenMatchSets) {

        boolean sameFile = mark1.fileId == mark2.fileId;
        int diff = mark1.indexInFile - mark2.indexInFile;
        if (sameFile && -diff < minTileSize) {
            // self-repeating sequence such as ABBABBABB with min 6,
            // will match 2 against any other occurrence of ABBABB
            return;
        }

        // "match too small" check
        //int dupes = tokens.countDupTokens(mark1, mark2);
        if (dupes < minTileSize) {
            return;
        }
        // both blocks overlap
        if (sameFile && diff + dupes >= 1) {
            return;
        }

        reportMatch(tokens.toTokenEntry(mark1), tokens.toTokenEntry(mark2), dupes, tokenMatchSets);
    }

    private void reportMatch(TokenEntry mark1, TokenEntry mark2, int dupes, LongObjectMap<SortedSet<Long>> tokenMatchSets) {
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

                        m.addMark(makeMark(mark2, dupes));
                        return;
                    }
                }
            }
            // this is a new match, add it
            Match match = new Match(dupes, makeMark(mark1, dupes), makeMark(mark2, dupes));
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

    private Mark makeMark(TokenEntry token, int matchLen) {
        Mark mark = new Mark(token);
        TokenEntry endToken = this.tokens.getEndToken(token, matchLen);
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
