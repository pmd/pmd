/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

public class Mark implements Comparable {

    private int indexIntoTokenArray;
    private String tokenSrcID;
    private int beginLine;
    private int hashCode;

    public Mark(int offset, String tokenSrcID, int beginLine) {
        this.indexIntoTokenArray = offset;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public String getTokenSrcID() {
        return this.tokenSrcID;
    }

    public int getIndexIntoTokenArray() {
        return indexIntoTokenArray;
    }

    public String toString() {
        return "Mark:\r\nindexIntoTokenArray = " + indexIntoTokenArray + "\r\nbeginLine = " + beginLine;
    }

    public int compareTo(Object o) {
        Mark other = (Mark) o;
        return getIndexIntoTokenArray() - other.getIndexIntoTokenArray();
    }

    public int hashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
    
    public boolean equals(Object o) {
        Mark other = (Mark)o;
        return other.hashCode == hashCode;
    }
}
