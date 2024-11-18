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

        marks.sort(Comparator.naturalOrder());

        if (marks.size() == 2) {
            // common happy path
            SmallTokenEntry fst = marks.get(0);
            SmallTokenEntry snd = marks.get(1);
            int dupes = tokens.countDupTokens(fst, snd, 0);
            SmallTokenEntry bestMark = bestDuplicate(fst, snd, dupes);
            if (bestMark != null) {
                dupes = bestMark.prevToken;
                recordMatch(Match.of(makeMark(fst, dupes), makeMark(bestMark, dupes)));
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

            if (isDuplicate(mark1, mark2, firstDups)) {
                handleDuplicate(i, j, mark1, mark2, firstDups, matches, isAliased);
            }

            for (j = i + 2; j < marks.size(); j++) {
                mark2 = marks.get(j);

                int dupes = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);
                off[j] = Math.min(firstDups, dupes);

                if (isDuplicate(mark1, mark2, dupes)) {
                    handleDuplicate(i, j, mark1, mark2, dupes, matches, isAliased);
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

    private void handleDuplicate(int i, int j, SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes, @Nullable Match.MatchBuilder[] matches, BitSet isAliased) {
        Match.MatchBuilder builder = matches[i];
        if (builder == null) {
            builder = matches[j];
        }
        if (builder == null) {
            builder = new Match.MatchBuilder();
        }
        matches[i] = builder;
        matches[j] = builder;

        builder.addMark(makeMark(mark1, dupes));
        builder.addMark(makeMark(mark2, dupes));

        if (isAliased.get(j)) {
            isAliased.set(i);
        } else {
            isAliased.set(j);
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
   match. However, the final, leftmost tokens that have the same sequence may overlap. For instance
   in
       A B C;   // 1
       A B C;   // 2
       A B C;   // 3
       A B C;   // 4
       A B C;   // 5
       A B C;   // 6
    with a tile size of 5. The hashes of A1, A2, A3, A4 will be equal, but we will only
    have A1 and A2 in the mark set. That is because we first add A4, then A3, notice they
    both are preceded by ;, so we remove them. Then we add A2 and A1, which are kept.
    A1 and A2 have 4x4 tokens in common (4 lines of A B C;), but 3 of those lines are shared.

    Ideally in this situation we want to report A1-A2 and A3-A4 as non-overlapping
    maximal marks in one match.

    We start by checking that A1 and A2 are in the same file and follow each other.
    Then we compute their prefix length, which is 16. This is greater than the distance
    between A1 and A2 (=4). That means that their common token sequence overlaps.
    In this case we try to look 4 tokens away from A2 (=A3) and again compare with A1.
    We find they have 12 tokens in common, and still overlap. But the non-overlapping
    match part is 12-4=8 which is greater than our previous candidate, and greater than
    the tile size (=5). (A1,A3) is therefore a better candidate than (A1,A2).
    Next we check 4 tokens away from A3, ie we check (A1,A4). This still has 8
    non-overlapping tokens so is not better. We stop and report (A1,A3).



    */
    private SmallTokenEntry bestDuplicate(final SmallTokenEntry mark1, final SmallTokenEntry mark2, final int dupes) {
        if (dupes < minTileSize) {
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
            if (distance < dupes && dupes % distance == 0) {
                // Then probably cyclic repetition of length "distance"
                // To report a non-overlapping match we take half of the
                // total duplicated range.
                int probableBest = (distance + dupes) / 2;
                SmallTokenEntry best = mark1.getNext(probableBest);
                int bestDups = tokens.countDupTokens(mark1, best, 0);
                if (bestDups == probableBest) {
                    best.prevToken = bestDups;
                    return best;
                }
            }
        }
        mark2.prevToken = dupes;
        return mark2;
    }


    private boolean isDuplicate(SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes) { // NOPMD
        // "match too small" check
        if (dupes < minTileSize) { // NOPMD
            return false;
        }
        return true;

        //        boolean sameFile = mark1.fileId == mark2.fileId;
        //        if (!sameFile) {
        //            return true;
        //        }
        //
        ////
        //        int distance = Math.abs(mark2.indexInFile - mark1.indexInFile);
        //        // check that they do not overlap
        //        return distance >= minTileSize
        //            && dupes < distance + 1;
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
