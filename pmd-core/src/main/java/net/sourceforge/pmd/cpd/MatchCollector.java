/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class MatchCollector {

    private final List<Match> matchList = new ArrayList<>();
    private final Map<Integer, List<Match>> matchTree = new HashMap<>();
    private final MatchAlgorithm ma;

    MatchCollector(MatchAlgorithm ma) {
        this.ma = ma;
    }

    public void collect(List<TokenEntry> marks) {
        // first get a pairwise collection of all maximal matches
        for (int i = 0; i < marks.size() - 1; i++) {
            TokenEntry mark1 = marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                TokenEntry mark2 = marks.get(j);
                int diff = mark1.getIndex() - mark2.getIndex();
                if (-diff < ma.getMinimumTileSize()) {
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
        matchTree.compute(mark1.getIndex(), (m1Index, matches) -> {
            if (matches == null) {
                matches = new ArrayList<>();
                addNewMatch(mark1, mark2, dupes, matches);
            } else {
                Iterator<Match> matchIterator = matches.iterator();
                while (matchIterator.hasNext()) {
                    Match m = matchIterator.next();
                    TokenEntry otherEnd = m.getSecondMark().getToken();

                    // does the new match supersedes this one?
                    if (otherEnd.getIndex() < mark2.getIndex() && otherEnd.getIndex() + m.getTokenCount() >= mark2.getIndex() + dupes) {
                        // this match is embedded in the previous one… ignore it.
                        return matches;
                    } else if (mark2.getIndex() < otherEnd.getIndex() && mark2.getIndex() + dupes >= otherEnd.getIndex() + m.getTokenCount()) {
                        // the new match is longer and overlaps with the old one - replace it
                        matchIterator.remove();
                        matchList.remove(m);
                        break;
                    }
                }

                addNewMatch(mark1, mark2, dupes, matches);
            }
            return matches;
        });
    }

    private void addNewMatch(TokenEntry mark1, TokenEntry mark2, int dupes, List<Match> matches) {
        Match match = new Match(dupes, mark1, mark2);
        matches.add(match);
        matchList.add(match);
    }

    List<Match> getMatches() {
        return matchList;
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
