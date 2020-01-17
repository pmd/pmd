/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tokens {

    private List<TokenEntry> tokens = new ArrayList<>();

    public void add(TokenEntry tokenEntry) {
        this.tokens.add(tokenEntry);
    }

    public Iterator<TokenEntry> iterator() {
        return tokens.iterator();
    }

    private TokenEntry get(int index) {
        return tokens.get(index);
    }

    public int size() {
        return tokens.size();
    }

    public TokenEntry getEndToken(TokenEntry mark, Match match) {
        return get(mark.getIndex() + match.getTokenCount() - 1);
    }

    public int getLineCount(TokenEntry mark, Match match) {
        TokenEntry endTok = getEndToken(mark, match);
        if (endTok == TokenEntry.EOF) {
            endTok = get(mark.getIndex() + match.getTokenCount() - 2);
        }
        return endTok.getBeginLine() - mark.getBeginLine() + 1;
    }

    public List<TokenEntry> getTokens() {
        return tokens;
    }

}
