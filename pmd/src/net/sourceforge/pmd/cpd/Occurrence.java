/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:28:41 AM
 */
package net.sourceforge.pmd.cpd;

public class Occurrence {

    private Token token;

    public Occurrence(Token tok) {
        this.token = tok;
    }

    public int getIndex() {
        return token.getIndex();
    }

    public String getTokenSetID() {
        return token.getTokenSrcID();
    }

    public String toString() {
        return "[" + token.getTokenSrcID() + "," + token.getIndex() + "]";
    }

    public boolean equals(Object other) {
        Occurrence o1 = (Occurrence)other;
        return o1.toString().equals(this.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }
}
