package net.sourceforge.pmd.cpd;

public class Locator {

    private String file;
    private int tokenIndex;

    public Locator(String file, int tokenIndex) {
        this.file = file;
        this.tokenIndex = tokenIndex;
    }

    public String getFile() {
        return file;
    }

    public int getTokenIndex() {
        return this.tokenIndex;
    }
}
