/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenEntry implements Comparable<TokenEntry> {

    public static final TokenEntry EOF = new TokenEntry();

    private String tokenSrcID;
    private final int beginLine;
    private final int beginColumn;
    private final int endColumn;
    private int index;
    private int identifier;
    private int hashCode;

    private static final ThreadLocal<Map<String, Integer>> TOKENS = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };
    private static final ThreadLocal<AtomicInteger> TOKEN_COUNT = new ThreadLocal<AtomicInteger>() {
        @Override
        protected AtomicInteger initialValue() {
            return new AtomicInteger(0);
        }
    };

    private TokenEntry() {
        this.identifier = 0;
        this.tokenSrcID = "EOFMarker";
        this.beginLine = -1;
        this.beginColumn = -1;
        this.endColumn = -1;
    }

    /**
     * Creates a new token entry with the given informations.
     * @param image
     * @param tokenSrcID
     * @param beginLine the linenumber, 1-based.
     */
    public TokenEntry(String image, String tokenSrcID, int beginLine) {
        this(image, tokenSrcID, beginLine, -1, -1);
    }

    /**
     * Creates a new token entry with the given informations.
     * @param image
     * @param tokenSrcID
     * @param beginLine the linenumber, 1-based.
     * @param beginColumn the column number, 1-based
     * @param endColumn the column number, 1-based
     */
    public TokenEntry(String image, String tokenSrcID, int beginLine, int beginColumn, int endColumn) {
        setImage(image);
        this.tokenSrcID = tokenSrcID;
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
        this.index = TOKEN_COUNT.get().getAndIncrement();
    }

    public static TokenEntry getEOF() {
        TOKEN_COUNT.get().getAndIncrement();
        return EOF;
    }

    public static void clearImages() {
        TOKENS.get().clear();
        TOKENS.remove();
        TOKEN_COUNT.remove();
    }

    /**
     * Helper class to preserve and restore the current state of the token
     * entries.
     */
    public static class State {
        private int tokenCount;
        private Map<String, Integer> tokens;
        private List<TokenEntry> entries;

        public State(List<TokenEntry> entries) {
            this.tokenCount = TokenEntry.TOKEN_COUNT.get().intValue();
            this.tokens = new HashMap<>(TokenEntry.TOKENS.get());
            this.entries = new ArrayList<>(entries);
        }

        public List<TokenEntry> restore() {
            TokenEntry.TOKEN_COUNT.get().set(tokenCount);
            TOKENS.get().clear();
            TOKENS.get().putAll(tokens);
            return entries;
        }
    }

    public String getTokenSrcID() {
        return tokenSrcID;
    }

    public int getBeginLine() {
        return beginLine;
    }

    /**
     * The column number where this token begins.
     * returns -1 if not available
     * @return the begin column number
     */
    public int getBeginColumn() {
        return beginColumn; // TODO Java 1.8 make optional
    }

    /**
     * The column number where this token ends.
     * returns -1 if not available
     * @return the end column number
     */
    public int getEndColumn() {
        return endColumn; // TODO Java 1.8 make optional
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TokenEntry)) {
            return false;
        }
        TokenEntry other = (TokenEntry) o;
        return other.hashCode == hashCode;
    }

    @Override
    public int compareTo(TokenEntry other) {
        return getIndex() - other.getIndex();
    }

    @Override
    public String toString() {
        if (this == EOF) {
            return "EOF";
        }
        for (Map.Entry<String, Integer> e : TOKENS.get().entrySet()) {
            if (e.getValue().intValue() == identifier) {
                return e.getKey();
            }
        }
        return "--unkown--";
    }

    final void setImage(String image) {
        Integer i = TOKENS.get().get(image);
        if (i == null) {
            i = TOKENS.get().size() + 1;
            TOKENS.get().put(image, i);
        }
        this.identifier = i.intValue();
    }
}
