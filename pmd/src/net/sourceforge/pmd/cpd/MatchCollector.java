package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchCollector {

    private List marks;
    private int codeSize;
    private MarkComparator markComparator;

    public MatchCollector(List marks, int codeSize, MarkComparator mc) {
        this.marks = marks;
        this.codeSize = codeSize;
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
        int numberOfDuplicateTokens = 0;
        for (int i = 0; i < codeSize; i++) {
            if (matchEnded(markComparator.tokenAt(i, mark1), markComparator.tokenAt(i, mark2))) {
                return numberOfDuplicateTokens;
            }
            numberOfDuplicateTokens++;
        }
        return numberOfDuplicateTokens;
    }

    private boolean matchEnded(TokenEntry token1, TokenEntry token2) {
        return !token1.equals(token2) || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF;
    }
}
