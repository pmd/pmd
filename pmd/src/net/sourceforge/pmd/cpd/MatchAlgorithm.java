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
    private List matchesList = new ArrayList();

    public void add(TokenEntry token, Locator locator, CPDListener cpdListener) {
        if (!pool.containsKey(token)) {
            pool.put(token, token);
        }
        code.add(token);
        marks.add(new Mark(code, code.size(), locator, cpdListener));
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

       Collections.sort(marks);

       Set soFar = new HashSet();
       for (int i = 1; i < marks.size();  i++) {
           int matches = 0;
           Mark mark1 = (Mark)marks.get(i);
           Mark mark2 = (Mark)marks.get(i - 1);

           // search backwards. If we find a delimiter before this one, skip
           //this match - there's a longer match.
           /*
           boolean betterAvailable = false;
           for (int j = 1; j < code.size(); j++) {
               MyToken token1 = mark1.tokenAt(j);
               MyToken token2 = mark2.tokenAt(j);
               // don't continue matches beyond EOFs.
               if (!token1.equals(token2)
                   || token1 == MyToken.EOF
                   || token2 == MyToken.EOF) {
                   break;
               }

               if (token1.isMarkToken()) {
                   betterAvailable = true;
                   break;
               }
               matches++;
           }
           if (betterAvailable) {
               continue;
           }
*/
           for (int j = 0; j < code.size(); j++) {
               TokenEntry token1 = mark1.tokenAt(j);
               TokenEntry token2 = mark2.tokenAt(j);
               if (!token1.equals(token2) || token1 == TokenEntry.EOF || token2 == TokenEntry.EOF) {
                   break;
               }
               matches++;
           }
           if (matches > min && !soFar.contains(mark1.getLocator().getFile()) && !soFar.contains(mark2.getLocator().getFile())) {
               soFar.add(mark1.getLocator().getFile());
               soFar.add(mark2.getLocator().getFile());
               matchesList.add(new Match(matches, mark1.getLocator(), mark2.getLocator()));
           }
       }
    }

    public Iterator matches() {
        return matchesList.iterator();
    }
}

