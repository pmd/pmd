package net.sourceforge.pmd.cpd;

public class Mark {

    private int indexIntoFile;
    private int indexIntoTokenArray;
    private String file;
    private int  beginLine;

    public Mark(int offset, String file, int index, int beginLine) {
        this.indexIntoTokenArray = offset;
        this.indexIntoFile = index;
        this.file = file;
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public String getFile() {
        return this.file;
    }

    public int getIndexIntoFile() {
        return this.indexIntoFile;
    }

    public int getIndexIntoTokenArray() {
        return indexIntoTokenArray;
    }
}
