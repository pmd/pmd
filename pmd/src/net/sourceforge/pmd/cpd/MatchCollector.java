package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchCollector {

    private List marks;
    private MarkComparator markComparator;

    public MatchCollector(List marks, MarkComparator mc) {
        this.marks = marks;
        this.markComparator = mc;
    }

    public List collect(int minimumLength) {
        List matches = new ArrayList();
        Set filesUsedSoFar = new HashSet();
        for (int i = 1; i < marks.size();  i++) {
            Mark mark1 = (Mark)marks.get(i);
            Mark mark2 = (Mark)marks.get(i - 1);
            if (!filesUsedSoFar.contains(mark1.getTokenSrcID()) && !filesUsedSoFar.contains(mark2.getTokenSrcID())) {
                int dupes = countDuplicateTokens(mark1, mark2);
                if (dupes >= minimumLength) {
                    filesUsedSoFar.add(mark1.getTokenSrcID());
                    filesUsedSoFar.add(mark2.getTokenSrcID());
                    Match match = new Match(dupes, mark1, mark2);
                    matches.add(match);
                }
            }
        }
        return matches;
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
