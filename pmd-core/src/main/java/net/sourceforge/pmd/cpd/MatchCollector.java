/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MatchCollector {
    private List<Match> matchList = new ArrayList<>();
    private Map<Integer, Map<Integer, Match>> matchTree = new TreeMap<>();
    private MatchAlgorithm ma;

    public MatchCollector(MatchAlgorithm ma) {
        this.ma = ma;
    }

    public void collect(List<TokenEntry> marks) {
        //first get a pairwise collection of all maximal matches
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
                // is it still too close together
                if (diff + dupes >= 1) {
                    continue;
                }
                reportMatch(mark1, mark2, dupes);
            }
        }
    }

    private void reportMatch(TokenEntry mark1, TokenEntry mark2, int dupes) {
        Map<Integer, Match> matches = matchTree.get(dupes);
        if (matches == null) {            
            matches = new TreeMap<>();
            matchTree.put(dupes, matches);
            addNewMatch(mark1, mark2, dupes, matches);
        } else {
            Match matchA = matchTree.get(dupes).get(mark1.getIndex());
            Match matchB = matchTree.get(dupes).get(mark2.getIndex());

            if (matchA == null && matchB == null) {
                addNewMatch(mark1, mark2, dupes, matches);
            } else if(matchA == null) {
                matchB.addTokenEntry(mark1);
                matches.put(mark1.getIndex(), matchB);
            } else if(matchB == null) {
                matchA.addTokenEntry(mark2);
                matches.put(mark2.getIndex(), matchA);
            }
        }
    }
    
    private void addNewMatch(TokenEntry mark1, TokenEntry mark2, int dupes, Map<Integer, Match> matches){
        Match match = new Match(dupes, mark1, mark2);
        matches.put(mark1.getIndex(), match);
        matches.put(mark2.getIndex(), match);
        matchList.add(match);        
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public List<Match> getMatches() {
        Collections.sort(matchList);
        return matchList;
    }    

    private boolean hasPreviousDupe(TokenEntry mark1, TokenEntry mark2) {
        if (mark1.getIndex() == 0) {
            return false;
        }
        return !matchEnded(ma.tokenAt(-1, mark1), ma.tokenAt(-1, mark2));
    }

    private int countDuplicateTokens(TokenEntry mark1, TokenEntry mark2) {
        int index = 0;
        while (!matchEnded(ma.tokenAt(index, mark1), ma.tokenAt(index, mark2))) {
            index++;
        }
        return index;
    }

    private boolean matchEnded(TokenEntry token1, TokenEntry token2) {
        return token1.getIdentifier() != token2.getIdentifier() || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF;
    }
}