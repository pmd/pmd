package net.sourceforge.pmd.cpd;



public class Mark {

    private int indexIntoFile;
    private int indexIntoTokenArray;
    private String file;

    public Mark(int offset, String file, int index) {
        this.indexIntoTokenArray = offset;
        this.indexIntoFile = index;
        this.file = file;
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
