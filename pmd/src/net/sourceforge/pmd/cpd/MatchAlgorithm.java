package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MatchAlgorithm {

    private Map pool = new TreeMap();
    private List code = new ArrayList();
    private List marks = new ArrayList();
    private List matches = new ArrayList();
    private CPDListener cpdListener;

    public void add(TokenEntry token, CPDListener cpdListener) {
        if (!pool.containsKey(token)) {
            pool.put(token, token);
        }
        code.add(pool.get(token));
        marks.add(new Mark(code.size(), token.getTokenSrcID(), token.getIndex()));
        this.cpdListener = cpdListener;
    }

    public void findMatches(int min) {
       /*
         Assign sort codes to all the pooled code. This should speed
         up sorting them.
       */
       int count = 1;
       for (Iterator iter = pool.keySet().iterator(); iter.hasNext();) {
           TokenEntry token = (TokenEntry)iter.next();
           token.setSortCode(count++);
       }

       MarkComparator mc = new MarkComparator(cpdListener, code);
       Collections.sort(marks, mc);

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
           if (matchedTokens > min && !soFar.contains(mark1.getFile()) && !soFar.contains(mark2.getFile())) {
               soFar.add(mark1.getFile());
               soFar.add(mark2.getFile());
               matches.add(new Match(matchedTokens, mark1, mark2));
           }
       }
    }

    public Iterator matches() {
        return matches.iterator();
    }
}

