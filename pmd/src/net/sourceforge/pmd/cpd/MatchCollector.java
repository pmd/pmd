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

    private List marks;
    private MarkComparator markComparator;
    private Map startMap = new HashMap(101);
    private Map fileMap = new HashMap(101);

    public MatchCollector(List marks, MarkComparator mc) {
        this.marks = marks;
        this.markComparator = mc;
    }

    public List collect(int minimumLength) {
        //first get a pairwise collection of all maximal matches
        Match.MatchCode matchCode = new Match.MatchCode();
        for (int i = 0; i < marks.size() - 1; i++) {
            Mark mark1 = (Mark) marks.get(i);
            for (int j = i + 1; j < marks.size(); j++) {
                Mark mark2 = (Mark) marks.get(j);
                int diff = mark1.getIndexIntoTokenArray() - mark2.getIndexIntoTokenArray();
                if (diff > 0) {
                    break;
                }
                if (-diff < minimumLength) {
                    continue;
                }
                if (hasPreviousDupe(mark1, mark2)) {
                    continue;
                }
                matchCode.setFirst(mark1.getIndexIntoTokenArray());
                matchCode.setSecond(mark2.getIndexIntoTokenArray());
                if (startMap.get(matchCode) != null) {
                    continue;
                }
                int dupes = countDuplicateTokens(mark1, mark2);
                if (dupes < minimumLength) {
                    break;
                }
                if (diff + dupes >= 1) {
                    continue;
                }
                determineMatch(mark1, mark2, dupes);
            }
        }

        //then collect the same matches together
        ArrayList matchList = new ArrayList(startMap.values());
        startMap.clear();
        fileMap.clear();
        groupMatches(matchList);
        return matchList;
    }

    //a match is not valid if it overlaps with a previous match in the same regions
    private void determineMatch(Mark mark1, Mark mark2, int dupes) {
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
            if (other.getFirstMark().getIndexIntoTokenArray() + other.getTokenCount() - mark1.getIndexIntoTokenArray()
                > 0) {
                boolean ordered = other.getSecondMark().getIndexIntoTokenArray() - mark2.getIndexIntoTokenArray() < 0;
                if ((ordered && (other.getEndIndex() - mark2.getIndexIntoTokenArray() > 0))
                    || (!ordered && (match.getEndIndex() - other.getSecondMark().getIndexIntoTokenArray()) > 0)) {
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
        for (int i = matchList.size(); i > 1; i--) {
            Match match1 = (Match) matchList.get(i - 1);
            Mark mark1 = (Mark) match1.getMarkSet().iterator().next();
            matchSet.clear();
            matchSet.add(match1.getMatchCode());
            for (int j = i - 1; j > 0; j--) {
                Match match2 = (Match) matchList.get(j - 1);
                if (match1.getTokenCount() != match2.getTokenCount()) {
                    break;
                }
                Mark mark2 = null;
                for (Iterator iter = match2.getMarkSet().iterator(); iter.hasNext();) {
                    mark2 = (Mark) iter.next();
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
                Mark cmark1 = (Mark) a1.get(outer);
                for (int inner = outer + 1; inner < a1.size() && !done; inner++) {
                    Mark cmark2 = (Mark) a1.get(inner);
                    if (!matchSet.contains(new Match.MatchCode(cmark1, cmark2))) {
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

    private boolean hasPreviousDupe(Mark mark1, Mark mark2) {
        if (mark1.getIndexIntoTokenArray() == 0) {
            return false;
        }
        return matchEnded(markComparator.tokenAt(-1, mark1), markComparator.tokenAt(-1, mark2));
    }

    private int countDuplicateTokens(Mark mark1, Mark mark2) {
        int index = 0;
        while (!matchEnded(markComparator.tokenAt(index, mark1), markComparator.tokenAt(index, mark2))) {
            index++;
        } 
        return index;
    }

    private boolean matchEnded(TokenEntry token1, TokenEntry token2) {
        return !token1.equals(token2) || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF;
    }
}