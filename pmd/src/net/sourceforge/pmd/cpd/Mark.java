package net.sourceforge.pmd.cpd;

public class Mark {

    private int indexIntoFile;
    private int indexIntoTokenArray;
    private String tokenSrcID;
    private int  beginLine;

    public Mark(int offset, String tokenSrcID, int index, int beginLine) {
        this.indexIntoTokenArray = offset;
        this.indexIntoFile = index;
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public String getTokenSrcID() {
        return this.tokenSrcID;
    }

    public int getIndexIntoFile() {
        return this.indexIntoFile;
    }

    public int getIndexIntoTokenArray() {
        return indexIntoTokenArray;
    }

    public String toString() {
        return "Mark:\r\nindexIntoFile = " + indexIntoFile + "\r\nindexIntoTokenArray = " + indexIntoTokenArray + "\r\nbeginLine = " + beginLine;
    }
}
