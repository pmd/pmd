/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 9:59:31 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TokenList implements Serializable {

    private String id;
    private List tokens = new ArrayList();
    private List code;

    /**
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

    // don't use this, just for Serialization
    protected TokenList() {}

    public TokenList(String id) {
        this.id = id;
    }

    public void setCode(List newCode) {
        code = newCode;
    }

    public String getSlice(int startLine, int endLine) {
        StringBuffer sb = new StringBuffer();
        // TODO this check for i<code.size() should not be necessary
        for (int i=startLine; (i<=endLine) && (i < code.size()) ; i++) {
            if (sb.length() !=0) {
                sb.append(EOL);
            }
            sb.append((String)code.get(i));
        }
        return sb.toString();
    }

    public void add(TokenEntry tok) {
        tokens.add(tok);
    }

    public String getID() {
        return id;
    }

    public int size() {
        return tokens.size();
    }

    public String toString() {
        return id + ":" + tokens.size();
    }

    public boolean equals(Object other) {
        TokenList o = (TokenList)other;
        return o.getID().equals(id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public Iterator iterator() {
        return tokens.iterator();
    }

    public boolean hasTokenAfter(Tile tile, TokenEntry tok) {
        int nextTokenIndex = tok.getIndex() + tile.getTokenCount();
        return nextTokenIndex < tokens.size();
    }

    public TokenEntry get(int index) {
        return (TokenEntry)tokens.get(index);
    }
}
