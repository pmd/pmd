/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 10:28:41 AM
 */
package net.sourceforge.pmd.cpd;

public class Occurrence {

    private int index;
    private String tokenSetID;

    public Occurrence(String id, Token tok) {
        this.tokenSetID = id;
        this.index = tok.getIndex();
    }

    public int getIndex() {
        return index;
    }

    public String getTokenSetID() {
        return tokenSetID;
    }

    public String toString() {
        return "[" + tokenSetID + "," + index + "]";
    }

    public boolean equals(Object other) {
        Occurrence o1 = (Occurrence)other;
        return o1.toString().equals(this.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }
}
