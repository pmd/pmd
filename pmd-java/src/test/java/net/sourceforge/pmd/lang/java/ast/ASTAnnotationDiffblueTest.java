package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.JClassType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ASTAnnotationDiffblueTest {
    /**
     * Method under test: {@link ASTAnnotation#getTypeNode()}
     */
    @Test
    void testGetTypeNode() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAnnotation.getTypeNode());
    }

    /**
     * Method under test: {@link ASTAnnotation#getTypeNode()}
     */
    @Test
    void testGetTypeNode2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAnnotation.getTypeNode());
    }

    /**
     * Method under test: {@link ASTAnnotation#getTypeMirror()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetTypeMirror() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTAnnotation astAnnotation = null;

        // Act
        JClassType actualTypeMirror = astAnnotation.getTypeMirror();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTAnnotation#getSimpleName()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetSimpleName() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        ASTAnnotation astAnnotation = null;

        // Act
        String actualSimpleName = astAnnotation.getSimpleName();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTAnnotation#getMemberList()}
     */
    @Test
    void testGetMemberList() {
        // Arrange, Act and Assert
        assertNull((new ASTAnnotation(1)).getMemberList());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMemberList()}
     */
    @Test
    void testGetMemberList2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));

        // Act and Assert
        assertNull(astAnnotation.getMemberList());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotation(1)).getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers3() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers4() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers5() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getMembers()}
     */
    @Test
    void testGetMembers6() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getMembers().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#iterator()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testIterator() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ASTAnnotation astAnnotation = null;

        // Act
        Iterator<ASTMemberValuePair> actualIteratorResult = astAnnotation.iterator();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotation(1)).getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue3() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue4() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue5() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue6() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue7() {
        // Arrange
        ASTAnnotationMemberList child = new ASTAnnotationMemberList(1);
        child.addChild(new ASTAmbiguousName(1), 1);

        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(child, 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValue(String)}
     */
    @Test
    void testGetFlatValue8() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValue("Attr Name").toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues() {
        // Arrange, Act and Assert
        assertTrue((new ASTAnnotation(1)).getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues3() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues4() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues5() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues6() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getFlatValues()}
     */
    @Test
    void testGetFlatValues7() {
        // Arrange
        ASTAnnotationMemberList child = new ASTAnnotationMemberList(1);
        child.addChild(new ASTAmbiguousName(1), 1);

        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(child, 1);

        // Act and Assert
        assertTrue(astAnnotation.getFlatValues().toList().isEmpty());
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute() {
        // Arrange, Act and Assert
        assertNull((new ASTAnnotation(1)).getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute2() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute3() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.setSymbolTable(mock(JSymbolTable.class));
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute4() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 0);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute5() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute6() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAmbiguousName(1), 1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute7() {
        // Arrange
        ASTAnnotationMemberList child = new ASTAnnotationMemberList(1);
        child.addChild(new ASTAmbiguousName(1), 1);

        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(child, 0);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#getAttribute(String)}
     */
    @Test
    void testGetAttribute8() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 1);
        astAnnotation.addChild(new ASTAnnotationMemberList(1), 0);

        // Act and Assert
        assertNull(astAnnotation.getAttribute("Attr Name"));
    }

    /**
     * Method under test: {@link ASTAnnotation#acceptVisitor(JavaVisitor, Object)}
     */
    @Test
    void testAcceptVisitor() {
        // Arrange
        ASTAnnotation astAnnotation = new ASTAnnotation(1);
        JavaVisitor<Object, Object> visitor = mock(JavaVisitor.class);
        when(visitor.visit(Mockito.<ASTAnnotation>any(), Mockito.<Object>any())).thenReturn("Visit");

        // Act
        Object actualAcceptVisitorResult = astAnnotation.acceptVisitor(visitor, "Data");

        // Assert
        verify(visitor).visit(isA(ASTAnnotation.class), isA(Object.class));
        assertEquals("Visit", actualAcceptVisitorResult);
    }

    /**
     * Method under test: {@link ASTAnnotation#ASTAnnotation(int)}
     */
    @Test
    void testNewASTAnnotation() {
        // Arrange and Act
        ASTAnnotation actualAstAnnotation = new ASTAnnotation(1);

        // Assert
        assertNull(actualAstAnnotation.getImage());
        assertNull(actualAstAnnotation.getFirstToken());
        assertNull(actualAstAnnotation.getLastToken());
        assertNull(actualAstAnnotation.getTypeMirrorInternal());
        assertEquals(0, actualAstAnnotation.getIndexInParent());
    }
}
