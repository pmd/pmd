/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:08:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class TokenSets extends AbstractSet {

    private Map tokenMap = new HashMap();

    public TokenSets() {}

    public TokenSets(TokenList tokens) {
        tokenMap.put(tokens.getID(), tokens);
    }

    public void add(TokenList tokens) {
        tokenMap.put(tokens.getID(), tokens);
    }

    public int size() {
        return tokenMap.size();
    }

    public Iterator iterator() {
        return tokenMap.values().iterator();
    }

    public TokenList getTokenList(Token tok) {
        return (TokenList)tokenMap.get(tok.getTokenSrcID());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokenMap.values().iterator(); i.hasNext();) {
            TokenList ts = (TokenList)i.next();
            sb.append(ts.toString());
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
