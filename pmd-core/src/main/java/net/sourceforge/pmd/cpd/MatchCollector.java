/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;

/**
 * Collect matches by computing the similarity between different tokens that had the same hash.
 */
class MatchCollector {

    private final List<Match> matchList = new ArrayList<>();
    private final SourceManager sourceManager;
    private final TokenFileSet tokens;
    private final int minTileSize;

    MatchCollector(SourceManager sourceManager, TokenFileSet tokens, int minTileSize) {
        this.sourceManager = sourceManager;
        this.tokens = tokens;
        this.minTileSize = minTileSize;
    }

    String debugMark(SmallTokenEntry mark) {
        return sourceManager.getSlice(new Mark(tokens.toTokenEntry(mark))).toString();
    }

    /**
     * Determine the matches in the list of token entries, given that all these token entries have the same hash.
     *
     * @param marks List of tokens
     */
    void collect(List<SmallTokenEntry> marks) {

        if (marks.size() < 2) {
            return;
        }

        if (marks.size() == 2) {
            // common happy path
            SmallTokenEntry fst = marks.get(0);
            SmallTokenEntry snd = marks.get(1);
            int dupes = tokens.countDupTokens(fst, snd, 0);
            if (isDuplicate(dupes)) {
                recordMatch(Match.of(makeMark(fst, dupes), makeMark(snd, dupes)));
            }
            return;
        }

        marks.sort(Comparator.naturalOrder());

        /*
            Compute the common prefix length of each token (=mark) with all other tokens in the list.
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

            The diagonal does not need to be computed, and neither does the lower half because of symmetry.
            Now since we proceed row major (for cache reuse) we will first compute row A, that is preflen(A, x) for x in {B, C, D}.
            Given the second fact above, we know that preflen(B,C) >= min(preflen(A,B), preflen(B,C)) = min(2, 3) = 2.
            This is stored in the table off as a lower bound on preflen(B,C).
            When computing the second row, for preflen(B,C), we can avoid checking the first two tokens of B and C,
            as we know they have them in common with A, so they must have them in common together as well.
            At each row i, we use the first pair (i, i+1) as a pivot to compute the lower bounds for row i+1.

            In practice this optimization is very useful.
            If we enter this MatchCollector function, then the marks in the list have the same hash. Because the hash
            computation takes tile size into account, it is very likely that tokens having the same hash have a preflen
            greater than the tile size. Since the tile size is routinely hundreds of tokens, we are saving at least that
            many unnecessary comparisons _per cell_ (and number of cell is quadratic). In particular for perfect matches
            only one comparison is necessary.
         */

        int[] off = new int[marks.size()];

        /*
            Duplicates create a Match in this array. The same Match object is put in the cell of both indices that matched,
            but the rightmost one is marked as aliased in the bit set. This is here to merge transitive matches. Eg if you
            match AB and then BC, then you end up with a match ABC that is in the cells of A, B and C. But only A is set
            to "not aliased", so only it will be reported in the end, because it is the same object as the others.
         */
        @Nullable Match.MatchBuilder[] matches = new Match.MatchBuilder[marks.size()];
        BitSet isAliased = new BitSet(marks.size());

        for (int i = 0; i < marks.size() - 1; i++) {
            int j = i + 1;
            SmallTokenEntry mark1 = marks.get(i);
            SmallTokenEntry mark2 = marks.get(j);

            int firstDups = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);

            if (isDuplicate(firstDups)) {
                handleDuplicate(i, j, mark1, mark2, firstDups, matches);
                isAliased.set(j);
            }

            for (j = i + 2; j < marks.size(); j++) {
                mark2 = marks.get(j);

                int dupes = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);
                off[j] = Math.min(firstDups, dupes);

                if (isDuplicate(dupes)) {
                    handleDuplicate(i, j, mark1, mark2, dupes, matches);
                    isAliased.set(j);
                }
            }
        }

        // report all non-aliased matches
        for (int i = isAliased.nextClearBit(0); i < matches.length; i = isAliased.nextClearBit(i + 1)) {
            Match.MatchBuilder builder = matches[i];
            if (builder == null) {
                continue;
            }
            Match match = builder.build();
            if (match == null) {
                continue;
            }
            recordMatch(match);
        }
    }

    private void handleDuplicate(int i, int j, SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes, @Nullable Match.MatchBuilder[] matches) {
        Match.MatchBuilder builder = matches[i];
        if (builder == null) {
            builder = new Match.MatchBuilder();
            matches[i] = builder;
        }
        matches[j] = builder;

        builder.addMark(makeMark(mark1, dupes));
        builder.addMark(makeMark(mark2, dupes));
    }

    private Mark makeMark(SmallTokenEntry smallTok, int matchLen) {
        TokenEntry token = tokens.toTokenEntry(smallTok);
        TokenEntry endToken = this.tokens.getEndToken(token, matchLen);
        return new Mark(token, endToken);
    }

    private boolean isDuplicate(int dupes) {
        // "match too small" check
        return dupes >= minTileSize;
    }

    private void recordMatch(Match match) {
        synchronized (matchList) {
            matchList.add(match);
        }
    }

    List<Match> getMatches() {
        matchList.sort(Comparator.naturalOrder());
        return matchList;
    }

}
