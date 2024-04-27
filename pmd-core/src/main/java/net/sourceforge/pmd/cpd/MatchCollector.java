/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pmd.cpd.TokenFileSet.SmallTokenEntry;

class MatchCollector {

    private final List<Match> matchList = new ArrayList<>();
    private final TokenFileSet tokens;
    private final int minTileSize;

    MatchCollector(TokenFileSet tokens, int minTileSize) {
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

        if (marks.size() == 2) {
            SmallTokenEntry fst = marks.get(0);
            SmallTokenEntry snd = marks.get(1);
            int dupes = tokens.countDupTokens(fst, snd, 0);
            if (isDuplicate(fst, snd, dupes)) {
                recordMatch(new Match(dupes, makeMark(fst, dupes), makeMark(snd, dupes)));
            }
            return;
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

        Match[] matches = new Match[marks.size()];
        BitSet isAliased = new BitSet(marks.size());

        for (int i = 0; i < marks.size() - 1; i++) {
            int j = i + 1;
            SmallTokenEntry mark1 = marks.get(i);
            SmallTokenEntry mark2 = marks.get(j);

            int firstDups = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);

            if (isDuplicate(mark1, mark2, firstDups)) {
                handleDuplicate(i, j, mark1, mark2, firstDups, matches);
                isAliased.set(j);
            }

            for (j = i + 2; j < marks.size(); j++) {
                mark2 = marks.get(j);

                int dupes = off[j] + tokens.countDupTokens(mark1, mark2, off[j]);
                off[j] = Math.min(firstDups, dupes);

                if (isDuplicate(mark1, mark2, dupes)) {
                    handleDuplicate(i, j, mark1, mark2, dupes, matches);
                    isAliased.set(j);
                }
            }
        }

        for (int i = isAliased.nextClearBit(0); i < matches.length; i = isAliased.nextClearBit(i + 1)) {
            Match match = matches[i];
            if (match != null) {
                recordMatch(match);
            }
        }
    }

    private void handleDuplicate(int i, int j, SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes, Match[] matches) {
        Match match = matches[i];
        if (match == null) {
            match = new Match(dupes);
            matches[i] = match;
        }
        matches[j] = match;

        match.addMark(makeMark(mark1, dupes));
        match.addMark(makeMark(mark2, dupes));
    }

    private Mark makeMark(SmallTokenEntry smallTok, int matchLen) {
        TokenEntry token = tokens.toTokenEntry(smallTok);
        TokenEntry endToken = this.tokens.getEndToken(token, matchLen);
        return new Mark(token, endToken);
    }

    private boolean isDuplicate(SmallTokenEntry mark1, SmallTokenEntry mark2, int dupes) {
        boolean sameFile = mark1.fileId == mark2.fileId;
        int diff = mark1.indexInFile - mark2.indexInFile;
        if (sameFile && -diff < minTileSize) {
            // self-repeating sequence such as ABBABBABB with min 6,
            // will match 2 against any other occurrence of ABBABB
            return false;
        }

        // "match too small" check
        //int dupes = tokens.countDupTokens(mark1, mark2);
        if (dupes < minTileSize) {
            return false;
        }
        // blocks do not overlap
        return !sameFile || diff + dupes < 1;
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
