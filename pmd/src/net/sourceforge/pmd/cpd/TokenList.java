package net.sourceforge.pmd.cpd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TokenList implements Serializable {

    private String fileName;
    private List tokens = new ArrayList();
    private List code;

    public TokenList(String fileName) {
        this.fileName = fileName;
    }

    public void setCode(List newCode) {
        code = newCode;
    }

    public int getLineNumber(int tokenIndex) {
        return ((TokenEntry)tokens.get(tokenIndex)).getBeginLine();
    }

    public int getLineCount(int startTokenIndex, int tokenCount) {
        TokenEntry t = (TokenEntry)tokens.get(startTokenIndex);
        TokenEntry t2 = (TokenEntry)tokens.get(Math.min(startTokenIndex + tokenCount, tokens.size()-1));
        return t2.getBeginLine() - t.getBeginLine();
    }

    public String getLineSlice(int startTokenIndex, int tokenCount) {
        TokenEntry t = (TokenEntry)tokens.get(startTokenIndex);
        TokenEntry t2 = (TokenEntry)tokens.get(Math.min(startTokenIndex + tokenCount, tokens.size()-1));
        return getSlice(t.getBeginLine()-1, t2.getBeginLine());
    }

    public String getSlice(int startLine, int endLine) {
        StringBuffer sb = new StringBuffer();
        // TODO this check for i<code.size() should not be necessary
        for (int i = startLine; i <= endLine && i < code.size(); i++) {
            if (sb.length() != 0) {
                sb.append(CPD.EOL);
            }
            sb.append((String) code.get(i));
        }
        return sb.toString();
    }

    public void add(TokenEntry tok) {
        tokens.add(tok);
    }

    public String getFileName() {
        return fileName;
    }

    public int size() {
        return tokens.size();
    }

    public boolean equals(Object other) {
        TokenList o = (TokenList) other;
        return o.getFileName().equals(fileName);
    }

    public int hashCode() {
        return fileName.hashCode();
    }

    public Iterator iterator() {
        return tokens.iterator();
    }
}
