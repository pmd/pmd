/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:31:44 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class Tile {

    private List tokens = new ArrayList();

    public Tile(Token tok) {
        tokens.add(tok);
    }

    public Tile(List newTokens) {
        tokens.addAll(newTokens);
    }

    public List getTokens() {
        return tokens;
    }

    public void add(Token tok) {
        tokens.add(tok);
    }

    public Tile copy() {
        List list = new ArrayList();
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            list.add(i.next());
        }
        Tile tile = new Tile(list);
        return tile;
    }

    public String getImage() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            Token tok = (Token)i.next();
            sb.append(tok.getImage());
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Tile[");
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            if (sb.length() > 6) {
                sb.append(",");
            }
            Token tok = (Token)i.next();
            sb.append(tok);
        }
        return sb.toString()+ "]";
    }

    public boolean equals(Object other) {
        Tile tile = (Tile)other;
        return tile.getImage().equals(getImage());
    }

    public int hashCode() {
        return getImage().hashCode();
    }

    public int getTokenCount() {
        return tokens.size();
    }

    public boolean contains(Token candidate) {
        for (Iterator i = tokens.iterator(); i.hasNext();) {
            Token token = (Token)i.next();
            if (candidate.getImage().equals(token.getImage()) && candidate.getIndex() == token.getIndex() && candidate.getTokenSrcID() == token.getTokenSrcID()) {
                return true;
            }
        }
        return false;
    }

}
