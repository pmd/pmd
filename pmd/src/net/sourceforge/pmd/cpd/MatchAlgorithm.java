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
    // using a treemap means I don't actually sort my flyweight tokens.
    private Map pool = new TreeMap();
    private List code = new ArrayList();
    private List marks = new ArrayList();
    // separate what the token is from where it is. Locator is only used if we need to see the code at that location.

    private MatchListener l;
    private CPDListener cpdListener;

    public MatchAlgorithm(MatchListener l, CPDListener cpdListener) {
        this.l = l;
        this.cpdListener = cpdListener;
    }

    public void add(MyToken token, Locator locator) {
        pool.put(token, token);
        code.add(token);
        if (token.isMarkToken()) {
            marks.add(new Mark(code, code.size(), locator, cpdListener));
        }
/*
        MyToken flyweight = (MyToken)pool.get(token);
        if (flyweight == null) {
            pool.put(token, token);
            flyweight = token;
        }
        code.add(flyweight);
        if (flyweight.isMarkToken()) {
            marks.add(new Mark(code, code.size(), locator));
        }
*/
    }

    /**
       Should return something, or notify someone with locators - that
       kind of thing ;)
    */
    public void findMatches(int min) {
       /*
         Assign sort codes to all the pooled code. This should speed
         up sorting them.
       */
        int count = 1;
       for (Iterator iter = pool.keySet().iterator(); iter.hasNext();) {
           MyToken token = (MyToken)iter.next();
           token.setSortCode(count++);
       }
       // use quicksort on the marks, same as the perl version
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
               MyToken token1 = mark1.tokenAt(j);
               MyToken token2 = mark2.tokenAt(j);
               if (!token1.equals(token2) || token1 == MyToken.EOF || token2 == MyToken.EOF) {
                   break;
               }
               matches++;
           }
           if (matches > min && !soFar.contains(mark1.getLocator().getFile()) && !soFar.contains(mark2.getLocator().getFile())) {
               soFar.add(mark1.getLocator().getFile());
               soFar.add(mark2.getLocator().getFile());
               l.matchFound(new Match(matches, mark1.getLocator(), mark2.getLocator()));
           }
       }
    }
}

