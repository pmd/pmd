/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:08:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.*;

public class TokenSets {

    Set tokenSets = new HashSet();

    public TokenSets() {}

    public TokenSets(TokenList ts) {
        tokenSets.add(ts);
    }

    public void add(TokenList ts) {
        tokenSets.add(ts);
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

    public TokenList getTokenSet(Occurrence occ) {
        for (Iterator i = iterator(); i.hasNext();) {
            TokenList ts = (TokenList)i.next();
            if (ts.getID().equals(occ.getTokenSetID())) {
                return ts;
            }
        }
        throw new RuntimeException("Couldn't find token set " + occ.getTokenSetID());
    }

}
