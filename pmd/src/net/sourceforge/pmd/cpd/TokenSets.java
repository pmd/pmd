/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:08:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class TokenSets extends AbstractSet {

    private Set tokenSets = new HashSet();

    public TokenSets() {}

    public TokenSets(TokenList ts) {
        tokenSets.add(ts);
    }

    public void add(TokenList ts) {
        tokenSets.add(ts);
    }

    public int size() {
        return tokenSets.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokenSets.iterator(); i.hasNext();) {
            TokenList ts = (TokenList)i.next();
            sb.append(ts.toString());
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public Iterator iterator() {
        return tokenSets.iterator();
    }

    public TokenList getTokenSet(Token tok) {
        for (Iterator i = iterator(); i.hasNext();) {
            TokenList ts = (TokenList)i.next();
            if (ts.getID().equals(tok.getTokenSrcID())) {
                return ts;
            }
        }
        throw new RuntimeException("Couldn't find token set " + tok.getTokenSrcID());
    }

}
