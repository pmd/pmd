/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 9:59:31 AM
 */
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class TokenSet {

    private String id;
    private List tokens = new ArrayList();

    public TokenSet(String id) {
        this.id = id;
    }

    public void add(Token tok) {
        tokens.add(tok);
    }

    public String getID() {
        return id;
    }

    public String toString() {
        return id + ":" + tokens.size();
    }

    public boolean equals(Object other) {
        TokenSet o = (TokenSet)other;
        return o.getID().equals(id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public Iterator iterator() {
        return tokens.iterator();
    }

    public boolean hasTokenAfter(Tile tile, Occurrence occ) {
        int nextTokenIndex = occ.getIndex() + tile.getTokenCount();
        return nextTokenIndex < tokens.size();
    }

    public Token get(int index) {
        return (Token)tokens.get(index);
    }




}
