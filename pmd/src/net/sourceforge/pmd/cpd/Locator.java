package net.sourceforge.pmd.cpd;

public class Locator {

    private String file;
    private int line;
    private int loc;

    public Locator(String file, int line, int originalTokenLocation) {
        this.file = file;
        this.line = line;
        this.loc = originalTokenLocation;
    }

    public int getLine() {
        return this.line;
    }
    public String getFile() {
        return file;
    }

    public int getLoc() {
        return this.loc;
    }

    public String toString() {
        return file + ":" + line;
    }
}
