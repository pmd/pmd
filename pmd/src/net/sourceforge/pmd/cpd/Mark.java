package net.sourceforge.pmd.cpd;

import java.util.List;

public class Mark {

    private int indexIntoFile;
    private int indexIntoTokenArray;
    private List code;
    private String file;

    public Mark(List code, int offset, String file, int index) {
        this.code = code;
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

    public TokenEntry tokenAt(int index) {
        return (TokenEntry)code.get((index + indexIntoTokenArray) % code.size());
    }

}
