package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tokens {

    private List tokens = new ArrayList();

    public void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    public Iterator iterator() {
        return tokens.iterator();
    }

    private TokenEntry get(int index) {
        return (TokenEntry)tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public int getLineCount(Mark mark, Match match) {
        TokenEntry endTok = get(mark.getIndexIntoTokenArray() + match.getTokenCount());
        if (endTok.equals(TokenEntry.EOF)) {
            endTok = get(mark.getIndexIntoTokenArray() + match.getTokenCount() - 1);
        }
        if (!endTok.getTokenSrcID().equals(mark.getFile())) {
            throw new RuntimeException("Something went wrong; CPD thinks that a match extends across two files: " + mark.getFile() + " and " + endTok.getTokenSrcID());
        }
        return endTok.getBeginLine() - mark.getBeginLine();
    }


}
