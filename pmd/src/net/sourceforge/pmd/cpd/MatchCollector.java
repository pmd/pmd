package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchCollector {

    private List marks;
    private List code;
    private MarkComparator mc;

    public MatchCollector(List marks, List code, MarkComparator mc) {
        this.marks = marks;
        this.code = code;
        this.mc = mc;
    }

    public List collect(int min) {
        List matches = new ArrayList();
        Set soFar = new HashSet();
        for (int i = 1; i < marks.size();  i++) {
            int matchedTokens = 0;
            Mark mark1 = (Mark)marks.get(i);
            Mark mark2 = (Mark)marks.get(i - 1);
            for (int j = 0; j < code.size(); j++) {
                TokenEntry token1 = mc.tokenAt(j, mark1);
                TokenEntry token2 = mc.tokenAt(j, mark2);
                if (!token1.equals(token2) || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF) {
                    break;
                }
                matchedTokens++;
            }
            if (matchedTokens > min && !soFar.contains(mark1.getTokenSrcID()) && !soFar.contains(mark2.getTokenSrcID())) {
                soFar.add(mark1.getTokenSrcID());
                soFar.add(mark2.getTokenSrcID());
                matches.add(new Match(matchedTokens, mark1, mark2));
            }
        }
        return matches;
    }
}
