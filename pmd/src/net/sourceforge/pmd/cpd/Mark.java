package net.sourceforge.pmd.cpd;

import java.util.List;

public class Mark {

    private int index;
    private int offset;
    private List code;
    private String file;

    public Mark(List code, int offset, String file, int index) {
        this.code = code;
        this.offset = offset;
        this.index = index;
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }

    public int getIndexIntoFile() {
        return this.index;
    }

    public int getIndexIntoTokenArray() {
        return offset;
    }

    public TokenEntry tokenAt(int index) {
        if (index < 0) {
            return (TokenEntry)code.get((code.size() + index + offset) % code.size());
        } else {
            return (TokenEntry)code.get((index + offset) % code.size());
        }
    }

}
