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

    public TokenSets(TokenSet ts) {
        tokenSets.add(ts);
    }

    public void add(TokenSet ts) {
        tokenSets.add(ts);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokenSets.iterator(); i.hasNext();) {
            TokenSet ts = (TokenSet)i.next();
            sb.append(ts.toString());
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public Iterator iterator() {
        return tokenSets.iterator();
    }

    public TokenSet getTokenSet(Occurrence occ) {
        for (Iterator i = iterator(); i.hasNext();) {
            TokenSet ts = (TokenSet)i.next();
            if (ts.getID().equals(occ.getTokenSetID())) {
                return ts;
            }
        }
        throw new RuntimeException("Couldn't find token set " + occ.getTokenSetID());
    }

}
