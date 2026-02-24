package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseAntlrTerminalNodeTest {
    static class TestTerminalNode extends BaseAntlrTerminalNode<TestTerminalNode> {
        private final String text;
        TestTerminalNode(Token token, String text) {
            super(token);
            this.text = text;
        }
        @Override
        public String getText() {
            return text;
        }
        @Override
        public String getXPathNodeName() {
            return "TestTerminalNode";
        }
    }

    static class DummyToken implements Token {
        private final int type;
        private final int index;
        DummyToken(int type, int index) {
            this.type = type;
            this.index = index;
        }
        @Override public int getType() { return type; }
        @Override public int getTokenIndex() { return index; }
        @Override public String getText() { return null; }
        @Override public int getLine() { return 0; }
        @Override public int getCharPositionInLine() { return 0; }
        @Override public int getChannel() { return 0; }
        @Override public int getStartIndex() { return 0; }
        @Override public int getStopIndex() { return 0; }
        @Override public org.antlr.v4.runtime.TokenSource getTokenSource() { return null; }
        @Override public org.antlr.v4.runtime.CharStream getInputStream() { return null; }
    }

    @Test
    void getTokenKindReturnsTokenType() {
        int expectedType = 42;
        int dummyIndex = 99;
        DummyToken token = new DummyToken(expectedType, dummyIndex);
        TestTerminalNode node = new TestTerminalNode(token, "foo");
        // This should return the token type, not the index
        assertEquals(expectedType, node.getTokenKind(), "getTokenKind() should return token type");
    }
}
