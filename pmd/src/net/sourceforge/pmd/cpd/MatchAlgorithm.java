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

    public void add(TokenEntry token, Locator locator, CPDListener cpdListener) {
        if (!pool.containsKey(token)) {
            pool.put(token, token);
        }
        code.add(pool.get(token));
        marks.add(new Mark(code, code.size(), locator));
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

       Collections.sort(marks, new MarkComparator(cpdListener, code));

       Set soFar = new HashSet();
       for (int i = 1; i < marks.size();  i++) {
           int matchedTokens = 0;
           Mark mark1 = (Mark)marks.get(i);
           Mark mark2 = (Mark)marks.get(i - 1);
           for (int j = 0; j < code.size(); j++) {
               TokenEntry token1 = mark1.tokenAt(j);
               TokenEntry token2 = mark2.tokenAt(j);
               if (!token1.equals(token2) || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF) {
                   break;
               }
               matchedTokens++;
           }
           if (matchedTokens > min && !soFar.contains(mark1.getLocator().getFile()) && !soFar.contains(mark2.getLocator().getFile())) {
               soFar.add(mark1.getLocator().getFile());
               soFar.add(mark2.getLocator().getFile());
               matches.add(new Match(matchedTokens, mark1.getLocator(), mark2.getLocator()));
           }
       }
    }

    public Iterator matches() {
        return matches.iterator();
    }
}

