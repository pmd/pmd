package net.sourceforge.pmd.cpd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TokenSets {

    private Map tokenLists = new HashMap();

    public void add(TokenList tokenList) {
        tokenLists.put(tokenList.getID(), tokenList);
    }

    public int tokenCount() {
        int total = 0;
        for (Iterator i = tokenLists.values().iterator(); i.hasNext();) {
            total += ((TokenList) i.next()).size();
        }
        return total;
    }

    public Iterator iterator() {
        return tokenLists.values().iterator();
    }

    public TokenList getTokenList(String file) {
        return (TokenList) tokenLists.get(file);
    }
}
