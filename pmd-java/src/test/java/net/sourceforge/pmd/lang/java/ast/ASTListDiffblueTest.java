package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.NodeStream;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ASTListDiffblueTest {
    /**
     * Method under test: {@link ASTList.ASTMaybeEmptyListOf#toStream()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testASTMaybeEmptyListOfToStream() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Can't find a suitable constructor.
        //   Diffblue Cover was unable to construct an instance of ASTList.ASTMaybeEmptyListOf.
        //   No suitable constructor or factory method found. Please check that the class
        //   under test has a non-private constructor or factory method.
        //   See https://diff.blue/R083 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTList.ASTMaybeEmptyListOf<JavaNode> astMaybeEmptyListOf = null;

        // Act
        NodeStream<JavaNode> actualToStreamResult = astMaybeEmptyListOf.toStream();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTList#size()}
     */
    @Test
    void testSize() {
        // Arrange, Act and Assert
        assertEquals(0, (new ASTAnnotationMemberList(1)).size());
    }

    /**
     * Method under test: {@link ASTList#size()}
     */
    @Test
    void testSize2() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        astAnnotationMemberList.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertEquals(0, astAnnotationMemberList.size());
    }

    /**
     * Method under test: {@link ASTList#isEmpty()}
     */
    @Test
    void testIsEmpty() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotationMemberList(1)).isEmpty());
    }

    /**
     * Method under test: {@link ASTList#isEmpty()}
     */
    @Test
    void testIsEmpty2() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        astAnnotationMemberList.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astAnnotationMemberList.isEmpty());
    }

    /**
     * Method under test: {@link ASTList#isEmpty()}
     */
    @Test
    void testIsEmpty3() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        astAnnotationMemberList.setSymbolTable(mock(JSymbolTable.class));
        astAnnotationMemberList.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertFalse(astAnnotationMemberList.isEmpty());
    }

    /**
     * Method under test: {@link ASTList#toList()}
     */
    @Test
    void testToList() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotationMemberList(1)).toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTList#toList()}
     */
    @Test
    void testToList2() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        astAnnotationMemberList.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertTrue(astAnnotationMemberList.toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTList#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     ArrayItr.a
        //     ArrayItr.cursor

        // Arrange
        // TODO: Populate arranged inputs
        ASTList<ASTMemberValuePair> astList = null;

        // Act
        Iterator<ASTMemberValuePair> actualIteratorResult = astList.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTList#get(int)}
     */
    @Test
    void testGet() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astAnnotationMemberList.addChild(child, 1);

        // Act and Assert
        assertSame(child, astAnnotationMemberList.get(1));
    }

    /**
     * Method under test: {@link ASTList#get(int)}
     */
    @Test
    void testGet2() {
        // Arrange
        ASTAnnotationMemberList astAnnotationMemberList = new ASTAnnotationMemberList(1);
        astAnnotationMemberList.setSymbolTable(mock(JSymbolTable.class));
        ASTAmbiguousName child = new ASTAmbiguousName(1);
        astAnnotationMemberList.addChild(child, 1);

        // Act and Assert
        assertSame(child, astAnnotationMemberList.get(1));
    }

    /**
     * Method under test: {@link ASTList#orEmpty(ASTList)}
     */
    @Test
    void testOrEmpty() {
        // Arrange and Act
        List<JavaNode> actualOrEmptyResult = ASTList.orEmpty(null);

        // Assert
        assertTrue(actualOrEmptyResult.isEmpty());
    }

    /**
     * Method under test: {@link ASTList#orEmptyStream(ASTList)}
     */
    @Test
    void testOrEmptyStream() {
        // Arrange and Act
        NodeStream<JavaNode> actualOrEmptyStreamResult = ASTList.orEmptyStream(null);

        // Assert
        assertTrue(actualOrEmptyStreamResult.toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTList#sizeOrZero(ASTList)}
     */
    @Test
    void testSizeOrZero() {
        // Arrange, Act and Assert
        assertEquals(0, ASTList.sizeOrZero(new ASTAnnotationMemberList(1)));
        assertEquals(0, ASTList.sizeOrZero(null));
        assertEquals(0, ASTList.sizeOrZero(new ASTAnnotationTypeBody(1)));
    }

    /**
     * Method under test: {@link ASTList#sizeOrZero(ASTList)}
     */
    @Test
    void testSizeOrZero2() {
        // Arrange
        ASTAnnotationMemberList list = new ASTAnnotationMemberList(1);
        list.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertEquals(0, ASTList.sizeOrZero(list));
    }

    /**
     * Method under test: {@link ASTList#sizeOrZero(ASTList)}
     */
    @Test
    void testSizeOrZero3() {
        // Arrange
        ASTAnnotationTypeBody list = new ASTAnnotationTypeBody(1);
        list.addChild(new ASTAmbiguousName(1), 2);

        // Act and Assert
        assertEquals(0, ASTList.sizeOrZero(list));
    }

    /**
     * Method under test: {@link ASTList#sizeOrZero(ASTList)}
     */
    @Test
    void testSizeOrZero4() {
        // Arrange
        ASTAnnotationTypeBody list = new ASTAnnotationTypeBody(1);
        list.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertEquals(0, ASTList.sizeOrZero(list));
    }

    /**
     * Method under test: {@link ASTList#sizeOrZero(ASTList)}
     */
    @Test
    void testSizeOrZero5() {
        // Arrange
        ASTAnnotationTypeBody list = new ASTAnnotationTypeBody(1);
        list.addChild(new ASTImplicitClassDeclaration(1), 0);

        // Act and Assert
        assertEquals(1, ASTList.sizeOrZero(list));
    }

    /**
     * Method under test: {@link ASTList#singleOrNull(ASTList)}
     */
    @Test
    void testSingleOrNull() {
        // Arrange, Act and Assert
        assertNull(ASTList.singleOrNull(null));
    }
}
