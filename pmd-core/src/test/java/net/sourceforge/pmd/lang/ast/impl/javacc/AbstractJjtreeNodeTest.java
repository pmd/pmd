package net.sourceforge.pmd.lang.ast.impl.javacc;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractJjtreeNodeTest {

    @Nested
    class InsertChild {

        FooNode subject;
        FooNode child0;
        FooNode child1;

        @BeforeEach
        void setup() {
            subject = new FooNode(0);

            child0 = createChildWithTokens(1);
            child1 = createChildWithTokens(3);

            subject.addChild(child0, 0);
            subject.addChild(child1, 1);

            subject.setFirstToken(child0.getFirstToken());
            subject.setLastToken(child1.getLastToken());
        }

        @Test
        void insertAtBeginning() {
            FooNode newChild = createChildWithTokens(0);
            when(newChild.getFirstToken().compareTo(any())).thenReturn(-1);

            subject.insertChild(newChild, 0);

            assertSame(newChild, subject.getFirstChild());
            assertSame(newChild.getFirstToken(), subject.getFirstToken());
            assertSame(child1.getLastToken(), subject.getLastToken());
        }

        @Test
        void insertInTheMiddle() {
            FooNode newChild = createChildWithTokens(2);

            subject.insertChild(newChild, 1);

            assertSame(newChild, subject.getChild(1));
            assertSame(child0.getFirstToken(), subject.getFirstToken());
            assertSame(child1.getLastToken(), subject.getLastToken());
        }

        @Test
        void insertAtEnd() {
            FooNode newChild = createChildWithTokens(4);
            when(newChild.getLastToken().compareTo(any())).thenReturn(1);

            subject.insertChild(newChild, 2);

            assertSame(newChild, subject.getLastChild());
            assertSame(child0.getFirstToken(), subject.getFirstToken());
            assertSame(newChild.getLastToken(), subject.getLastToken());
        }

        private FooNode createChildWithTokens(int id) {
            FooNode child = new FooNode(id);
            child.setFirstToken(mock());
            child.setLastToken(mock());
            return child;
        }
    }

    private static class FooNode extends AbstractJjtreeNode<FooNode, FooNode> {
        private FooNode(int id) {
            super(id);
        }

        @Override
        public String getXPathNodeName() {
            return "";
        }
    }
}
