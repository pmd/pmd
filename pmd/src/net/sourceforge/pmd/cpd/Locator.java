package net.sourceforge.pmd.cpd;

public class Locator {

    private String file;
    private int line;
    private int indexIntoFile;

    public Locator(String file, int line, int indexIntoFile) {
        this.file = file;
        this.line = line;
        this.indexIntoFile = indexIntoFile;
    }

    public int getLine() {
        return this.line;
    }
    public String getFile() {
        return file;
    }

    public int getIndexIntoFile() {
        return this.indexIntoFile;
    }

    public String toString() {
        return file + ":" + line;
    }
}
