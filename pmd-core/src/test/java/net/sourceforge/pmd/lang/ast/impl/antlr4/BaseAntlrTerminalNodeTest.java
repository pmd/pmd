/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

class BaseAntlrTerminalNodeTest {

    static class TestTerminalNode extends BaseAntlrTerminalNode<TestTerminalNode> {

        private final String text;

        TestTerminalNode(Token token, String text) {
            super(token);
            this.text = text;
        }

        @Override
        @NonNull
        public String getText() {
            return text;
        }

        @Override
        @NonNull
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

        @Override
        public int getType() {
            return type;
        }

        @Override
        public int getTokenIndex() {
            return index;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getCharPositionInLine() {
            return 0;
        }

        @Override
        public int getChannel() {
            return 0;
        }

        @Override
        public int getStartIndex() {
            return 0;
        }

        @Override
        public int getStopIndex() {
            return 0;
        }

        @Override
        public TokenSource getTokenSource() {
            return null;
        }

        @Override
        public CharStream getInputStream() {
            return null;
        }
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
