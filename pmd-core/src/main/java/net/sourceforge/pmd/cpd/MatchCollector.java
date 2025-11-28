/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;
import net.sourceforge.pmd.util.AssertionUtil;

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

        marks.sort(Comparator.naturalOrder());

        if (marks.size() == 2) {
            // common happy path
            SmallTokenEntry fst = marks.get(0);
            SmallTokenEntry snd = marks.get(1);
            int dupes = tokens.countDupTokens(fst, snd, 0);
            MatchRefinement refinement = bestDuplicate(fst, snd, dupes);
            if (refinement != null) {
                dupes = refinement.dupes;
                recordMatch(Match.of(makeMark(fst, dupes), makeMark(refinement.mark2, dupes)));
            }
            return;
        }

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
            This is stored in the table lb as a lower bound on preflen(B,C).
            When computing the second row, for preflen(B,C), we can avoid checking the first two tokens of B and C,
            as we know they have them in common with A, so they must have them in common together as well.
            At each row i, we use the first pair (i, i+1) as a pivot to compute the lower bounds for row i+1.

            In practice this optimization is very useful.
            If we enter this MatchCollector function, then the marks in the list have the same hash. Because the hash
            computation takes tile size into account, it is very likely that tokens having the same hash have a preflen
            greater than the tile size. Since the tile size is routinely hundreds of tokens, we are saving at least that
            many unnecessary comparisons _per cell_ (and number of cell is quadratic). In particular for perfect matches
            only one comparison is necessary. I measured that depending on the analysed sources, anywhere from 35 to 65%
            of comparisons can be eliminated with this trick (but keep in mind, runtime is dominated by lexing anyway).
         */

        int[] lb = new int[marks.size()];

        /*
            Duplicates create a Match in this array. The same Match object is put in the cell of both indices that matched,
            but the rightmost one is marked as aliased in the bit set. This is here to merge transitive matches. Eg if you
            match AB and then BC, then you end up with a match ABC that is in the cells of A, B and C. But only A is set
            to "not aliased", so only it will be reported in the end, because it is the same object as the others.
         */
        Match.@Nullable MatchBuilder[] matches = new Match.MatchBuilder[marks.size()];
        BitSet isAliased = new BitSet(marks.size());

        for (int i = 0; i < marks.size() - 1; i++) {
            int j = i + 1;
            SmallTokenEntry mark1 = marks.get(i);
            SmallTokenEntry mark2 = marks.get(j);


            int dupesIIp1 = lb[j] + tokens.countDupTokens(mark1, mark2, lb[j]);

            if (isDuplicate(dupesIIp1)) {
                handleDuplicate(i, j, mark1, mark2, dupesIIp1, matches, isAliased);
            }

            for (j = i + 2; j < marks.size(); j++) {
                mark2 = marks.get(j);

                int dupesIJ = lb[j] + tokens.countDupTokens(mark1, mark2, lb[j]);
                lb[j] = Math.min(dupesIIp1, dupesIJ);

                if (isDuplicate(dupesIJ)) {
                    handleDuplicate(i, j, mark1, mark2, dupesIJ, matches, isAliased);
                }
            }
            // for (int k = 0; k < marks.size(); k++) {
            //     if (k <= i) {
            //         System.out.printf("%4d", 0);
            //     } else {
            //         System.out.printf("%4d", lb[k]);
            //     }
            // }
            // System.out.println();
        }

        verifyAliasBits(matches, isAliased);

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


    private void handleDuplicate(int i, int j, SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes, Match.@Nullable MatchBuilder[] matches, BitSet isAliased) {
        assert i < j;
        // We want always the leftmost match to be the nonaliased one.
        Match.MatchBuilder match = matches[i];
        if (match == null) {
            match = matches[j];
        }
        if (match == null) {
            match = new Match.MatchBuilder();
        }
        matches[i] = match;
        matches[j] = match;

        match.addMark(makeMark(mark1, dupes));
        match.addMark(makeMark(mark2, dupes));

        if (isAliased.get(j)) {
            isAliased.set(i);
        } else {
            isAliased.set(j);
        }
    }

    /**
     * Verification loop to test whether the alias bit setting works.
     * If the same builder is recorded at multiple indices, we want the
     * leftmost index to be marked as non-aliased and all the others to
     * be marked as aliased.
     */
    private static void verifyAliasBits(Match.@Nullable MatchBuilder[] matches, BitSet isAliased) {
        if (!AssertionUtil.isAssertEnabled()) {
            return;
        }

        Map<Match.MatchBuilder, Void> identitySet = new IdentityHashMap<>();
        for (int i = 0; i < matches.length; i++) {
            Match.MatchBuilder builder = matches[i];
            if (builder == null) {
                continue;
            }

            if (identitySet.containsKey(builder)) {
                assert isAliased.get(i) : "Aliased match has no aliased bit";
            } else {
                assert !isAliased.get(i) : "Non aliased match has aliased bit";
            }

            identitySet.put(builder, null);
        }
    }

    private Mark makeMark(SmallTokenEntry smallTok, int matchLen) {
        TokenEntry token = tokens.toTokenEntry(smallTok);
        TokenEntry endToken = this.tokens.getEndToken(token, matchLen);
        return new Mark(token, endToken);
    }


    /*
   One optimization we do is to prune marks that have same hash, but have the same preceding token.
   This is because the preceding token necessarily also hos the same hash, and will yield a longer
   match. However, the final, leftmost tokens that have the same prefix sequence may overlap. For
   instance in
       A B C D   // 1
       A B C D   // 2
       A B C D   // 3
       A B C D   // 4
       A B C D   // 5
    with a tile size of 5. The hashes of A1, A2, A3, A4 will be equal, but we will only
    have A1 and A2 in the mark set. That is because we first add A5, then A4, notice they
    both are preceded by D, so we remove them. Then we add A3 and A2 and prune them. Finally,
    we add A1, which is kept, but is alone so won't be processed by this algorithm. However,
    D1 and D2 will.
    D1 and D2 have 13 tokens in common (3 lines of `D A B C` plus D5), but 2 of those
    lines are shared (D2-D4).

    Ideally in this situation we want to report D1-D2 and D3-D4 as non-overlapping
    maximal marks in one match. (We could also opt to report D1-D3 and D2-D5...
    but currently we don't as they overlap, even if they are larger matches.)

    We start by checking that D1 and D2 are in the same file and follow each other.
    Then we compute their prefix length, which is 13. This is greater than the distance
    between D1 and D2 (=4). That means that their common token sequence overlaps on 9 tokens.
    In this case we try to look 4 tokens away from D2 (=D3) and again compare with D1.
    We find they have 9 tokens in common, and do not overlap. (D1,D3) is therefore a
    better candidate than (D1,D2). There is no need to check further as we could only
    get a smaller prefix length. The match is maximal (D1,D3).
    */
    private MatchRefinement bestDuplicate(final SmallTokenEntry mark1, final SmallTokenEntry mark2, final int dupes) {
        if (!isDuplicate(dupes)) {
            // not a good enough duplicate
            return null;
        }
        if (mark1.fileId == mark2.fileId) {
            assert mark1.indexInFile < mark2.indexInFile : mark1 + " should come before " + mark2;
            final int distance = mark2.indexInFile - mark1.indexInFile;
            if (distance + dupes < minTileSize * 2) {
                // There are no non-overlapping marks that
                // are bigger than the tile size.
                return null;
            }

            if (distance < dupes) {
                // Then probably cyclic repetition of length "distance".
                SmallTokenEntry bestMark = mark2;
                int bestDupes = dupes;
                for (int i = 1; ; i++) {
                    SmallTokenEntry maybeBetter = mark2.getNext(i * distance);
                    int newDupes = tokens.countDupTokens(mark1, maybeBetter, 0);
                    if (newDupes >= minTileSize) {
                        bestMark = maybeBetter;
                        bestDupes = newDupes;
                    } else {
                        // We have the "maximal" mark, but both still overlap
                        break;
                    }
                    if (mark1.indexInFile + bestDupes <= bestMark.indexInFile) {
                        // they don't overlap anymore
                        break;
                    }
                }
                return new MatchRefinement(bestMark, bestDupes);
            }
        }
        return new MatchRefinement(mark2, dupes);
    }

    private static final class MatchRefinement {
        final SmallTokenEntry mark2;
        final int dupes;

        private MatchRefinement(SmallTokenEntry mark2, int dupes) {
            this.mark2 = mark2;
            this.dupes = dupes;
        }
    }


    private boolean isDuplicate(int dupes) {
        // "match too small" check. We don't check for overlap anymore as
        // the match builder deals with overlapping marks now.
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
