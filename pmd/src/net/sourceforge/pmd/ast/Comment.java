package net.sourceforge.pmd.ast;

public abstract class Comment {

    private String image;

    private int beginLine = -1;

    private int endLine;

    private int beginColumn = -1;

    private int endColumn;

    protected Comment(Token t) {
        beginLine = t.beginLine;
        endLine = t.endLine;
        beginColumn = t.beginColumn;
        endColumn = t.endColumn;
        image = t.image;
    }

    public String getImage() {
        return image;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getBeginColumn() {
        return beginColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

}
