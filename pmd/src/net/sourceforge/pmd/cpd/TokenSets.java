package net.sourceforge.pmd.cpd;

import java.util.HashMap;
import java.util.Iterator;

public class TokenSets extends HashMap {

    public TokenSets() {
        super();
    }

    public void add(TokenList tokenList) {
        put(tokenList.getFileName(), tokenList);
    }

    public int tokenCount() {
        int total = 0;
        for (Iterator i = values().iterator(); i.hasNext();) {
            total += ((TokenList) i.next()).size();
        }
        return total;
    }

    public TokenList getTokenList(String file) {
        return (TokenList)get(file);
    }
}
