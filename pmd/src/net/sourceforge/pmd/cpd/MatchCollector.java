/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchCollector {

    private MatchAlgorithm ma;
    private Map startMap = new HashMap();
    private Map fileMap = new HashMap();

    public MatchCollector(MatchAlgorithm ma) {
        this.ma = ma;
    }

    public void collect(int minimumLength, List marks) {
        //first get a pairwise collection of all maximal matches
        for (int i = 0; i < marks.size() - 1; i++) {
            TokenEntry mark1 = (TokenEntry) marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
				TokenEntry mark2 = (TokenEntry) marks.get(j);
				int diff = mark1.getIndex() - mark2.getIndex();
                if (-diff < minimumLength) {
                    continue;
                }
                if (hasPreviousDupe(mark1, mark2)) {
                    continue;
                }
                int dupes = countDuplicateTokens(mark1, mark2);
                //false positive check
                if (dupes < minimumLength) {
                    continue;
                }
                //is it still too close together
                if (diff + dupes >= 1) {
                    continue;
                }
                determineMatch(mark1, mark2, dupes);
            }
        }
    }
    
    public List getMatches() {
		ArrayList matchList = new ArrayList(startMap.values());
		groupMatches(matchList);
		return matchList;
    }

    /**
     * A greedy algorithm for determining non-overlapping matches
     */
    private void determineMatch(TokenEntry mark1, TokenEntry mark2, int dupes) {
        Match match = new Match(dupes, mark1, mark2);
        String fileKey = mark1.getTokenSrcID() + mark2.getTokenSrcID();
        ArrayList pairMatches = (ArrayList) fileMap.get(fileKey);
        if (pairMatches == null) {
            pairMatches = new ArrayList();
            fileMap.put(fileKey, pairMatches);
        }
        boolean add = true;
        for (int k = 0; k < pairMatches.size(); k++) {
            Match other = (Match) pairMatches.get(k);
            if (other.getFirstMark().getIndex() + other.getTokenCount() - mark1.getIndex()
                > 0) {
                boolean ordered = other.getSecondMark().getIndex() - mark2.getIndex() < 0;
                if ((ordered && (other.getEndIndex() - mark2.getIndex() > 0))
                    || (!ordered && (match.getEndIndex() - other.getSecondMark().getIndex()) > 0)) {
                    if (other.getTokenCount() >= match.getTokenCount()) {
                        add = false;
                        break;
                    } else {
                        pairMatches.remove(k);
                        startMap.remove(other.getMatchCode());
                    }
                }
            }
        }
        if (add) {
            pairMatches.add(match);
            startMap.put(match.getMatchCode(), match);
        }
    }

    private void groupMatches(ArrayList matchList) {
        Collections.sort(matchList);
        HashSet matchSet = new HashSet();
        Match.MatchCode matchCode = new Match.MatchCode();
        for (int i = matchList.size(); i > 1; i--) {
            Match match1 = (Match) matchList.get(i - 1);
            TokenEntry mark1 = (TokenEntry) match1.getMarkSet().iterator().next();
            matchSet.clear();
            matchSet.add(match1.getMatchCode());
            for (int j = i - 1; j > 0; j--) {
                Match match2 = (Match) matchList.get(j - 1);
                if (match1.getTokenCount() != match2.getTokenCount()) {
                    break;
                }
                TokenEntry mark2 = null;
                for (Iterator iter = match2.getMarkSet().iterator(); iter.hasNext();) {
                    mark2 = (TokenEntry) iter.next();
                    if (mark2 != mark1) {
                        break;
                    }
                }
                int dupes = countDuplicateTokens(mark1, mark2);
                if (dupes < match1.getTokenCount()) {
                    break;
                }
                matchSet.add(match2.getMatchCode());
                match1.getMarkSet().addAll(match2.getMarkSet());
                matchList.remove(i - 2);
                i--;
            }
            if (matchSet.size() == 1) {
                continue;
            }
            //prune the mark set
            Set pruned = match1.getMarkSet();
            boolean done = false;
            ArrayList a1 = new ArrayList(match1.getMarkSet());
            Collections.sort(a1);
            for (int outer = 0; outer < a1.size() - 1 && !done; outer++) {
                TokenEntry cmark1 = (TokenEntry) a1.get(outer);
                for (int inner = outer + 1; inner < a1.size() && !done; inner++) {
                    TokenEntry cmark2 = (TokenEntry) a1.get(inner);
                    matchCode.setFirst(cmark1.getIndex());
                    matchCode.setSecond(cmark2.getIndex());
                    if (!matchSet.contains(matchCode)) {
                        if (pruned.size() > 2) {
                            pruned.remove(cmark2);
                        }
                        if (pruned.size() == 2) {
                            done = true;
                        }
                    }
                }
            }
        }
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