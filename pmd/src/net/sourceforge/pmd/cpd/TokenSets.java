/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:08:08 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TokenSets extends AbstractSet implements Serializable {

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    private Map tokenMap = new HashMap();

    public TokenSets() {
    }

    public TokenSets(TokenList tokens) {
        tokenMap.put(tokens.getID(), tokens);
    }

    public void add(TokenList tokens) {
        tokenMap.put(tokens.getID(), tokens);
    }

    public int size() {
        return tokenMap.size();
    }

    public int tokenCount() {
        int total = 0;
        for (Iterator i = tokenMap.values().iterator(); i.hasNext();) {
            TokenList tl = (TokenList) i.next();
            total += tl.size();
        }
        return total;
    }

    public Iterator iterator() {
        return tokenMap.values().iterator();
    }

    public TokenList getTokenList(TokenEntry tok) {
        return (TokenList) tokenMap.get(tok.getTokenSrcID());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokenMap.values().iterator(); i.hasNext();) {
            TokenList ts = (TokenList) i.next();
            sb.append(ts.toString());
            sb.append(EOL);
        }
        return sb.toString();
    }

}
