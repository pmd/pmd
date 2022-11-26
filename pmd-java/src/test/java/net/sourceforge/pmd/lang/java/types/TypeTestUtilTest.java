/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.testdata.SomeClassWithAnon;

class TypeTestUtilTest extends BaseParserTest {

    @Test
    void testIsAFallback() {

        ASTClassOrInterfaceDeclaration klass =
            java.parse("package org; import java.io.Serializable; "
                           + "class FooBar implements Serializable {}")
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);


        assertNull(klass.getType());
        assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        assertTrue(TypeTestUtil.isA("java.io.Serializable", klass));
        assertTrue(TypeTestUtil.isA(Serializable.class, klass));
    }

    @Test
    void testIsAFallbackWithUnresolvedClassReference() { // != declaration

        ASTAnnotation annot =
            java.parse("import a.b.Test;"
                           + "class FooBar { @Test void bar() {} }")
                .getFirstDescendantOfType(ASTAnnotation.class);

        assertTrue(TypeTestUtil.isA("a.b.Test", annot));
        assertTrue(TypeOps.isUnresolved(annot.getTypeMirror()));

        assertFalse(TypeTestUtil.isA(org.junit.Test.class, annot));
        assertFalse(TypeTestUtil.isA("org.junit.Test", annot));
        assertFalse(TypeTestUtil.isA(Override.class, annot));
        assertFalse(TypeTestUtil.isA("java.lang.Override", annot));
    }


    @Test
    void testIsAFallbackEnum() {

        ASTEnumDeclaration klass =
            java.parse("package org; "
                           + "enum FooBar implements Iterable {}")
                .getFirstDescendantOfType(ASTEnumDeclaration.class);


        assertNull(klass.getType());
        assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        assertIsStrictSubtype(klass, Iterable.class);
        assertIsStrictSubtype(klass, Enum.class);
        assertIsStrictSubtype(klass, Serializable.class);
        assertIsStrictSubtype(klass, Object.class);
    }


    @Test
    void testIsAnArrayClass() {

        ASTType arrayT =
            java.parse("import java.io.ObjectStreamField; "
                           + "class Foo { private static final ObjectStreamField[] serialPersistentFields; }")
                .getFirstDescendantOfType(ASTType.class);


        assertIsExactlyA(arrayT, ObjectStreamField[].class);
        assertIsStrictSubtype(arrayT, Object[].class);
        assertIsStrictSubtype(arrayT, Serializable.class);
        assertIsNot(arrayT, Serializable[].class);
        assertIsStrictSubtype(arrayT, Object.class);
    }

    @Test
    void testIsAnAnnotationClass() {

        ASTType arrayT =
            java.parse("class Foo { org.junit.Test field; }")
                .getFirstDescendantOfType(ASTType.class);


        assertIsExactlyA(arrayT, org.junit.Test.class);
        assertIsStrictSubtype(arrayT, Annotation.class);
        assertIsStrictSubtype(arrayT, Object.class);
    }

    @Test
    void testIsAPrimitiveArrayClass() {

        ASTType arrayT =
            java.parse("import java.io.ObjectStreamField; "
                           + "class Foo { private static final int[] serialPersistentFields; }")
                .getFirstDescendantOfType(ASTType.class);


        assertIsExactlyA(arrayT, int[].class);
        assertIsNot(arrayT, long[].class);
        assertIsNot(arrayT, Object[].class);

        assertIsStrictSubtype(arrayT, Serializable.class);
        assertIsStrictSubtype(arrayT, Object.class);
    }

    @Test
    void testIsAPrimitiveSubtype() {

        ASTType arrayT =
            java.parse("import java.io.ObjectStreamField; "
                           + "class Foo { private static final int serialPersistentFields; }")
                .getFirstDescendantOfType(ASTType.class);


        assertIsExactlyA(arrayT, int.class);
        assertIsNot(arrayT, long.class);
        assertIsNot(arrayT, double.class);
        assertIsNot(arrayT, float.class);
        assertIsNot(arrayT, Object.class);
    }


    @Test
    void testIsAFallbackAnnotation() {

        ASTAnnotationTypeDeclaration klass =
            java.parse("package org; import foo.Stuff;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnnotationTypeDeclaration.class);


        assertNull(klass.getType());
        assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        assertIsA(klass, Annotation.class);
        assertIsA(klass, Object.class);
    }

    @Test
    void testIsATypeVarWithUnresolvedBound() {
        // a type var with an unresolved bound should not be considered
        // a subtype of everything

        ASTType field =
            java.parse("class Foo<T extends Unresolved> {\n"
                           + "\tT field;\n"
                           + "}")
                .descendants(ASTFieldDeclaration.class)
                .firstOrThrow().getTypeNode();

        assertIsA(field, Object.class);
        assertIsNot(field, String.class);
    }

    @Test
    void testIsAStringWithTypeArguments() {

        ASTAnyTypeDeclaration klass =
            java.parse("package org;"
                           + "public class FooBar {}")
                .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);


        assertThrows(IllegalArgumentException.class,
                () -> TypeTestUtil.isA("java.util.List<java.lang.String>", klass));
    }

    @Test
    void testIsAStringWithTypeArgumentsAnnotation() {

        ASTAnyTypeDeclaration klass =
            java.parse("package org;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);


        assertThrows(IllegalArgumentException.class, () ->
            TypeTestUtil.isA("java.util.List<java.lang.String>", klass));
    }

    @Test
    void testAnonClassTypeNPE() {
        // #2756

        ASTAnonymousClassDeclaration anon =
            java.parseClass(SomeClassWithAnon.class)
                .getFirstDescendantOfType(ASTAnonymousClassDeclaration.class);


        assertTrue(anon.getSymbol().isAnonymousClass(), "Anon class");
        assertTrue(TypeTestUtil.isA(Runnable.class, anon), "Should be a Runnable");

        // This is not a canonical name, so we give up early
        assertFalse(TypeTestUtil.isA(SomeClassWithAnon.class.getName() + "$1", anon));
        assertFalse(TypeTestUtil.isExactlyA(SomeClassWithAnon.class.getName() + "$1", anon));

        // this is the failure case: if the binary name doesn't match, we test the canoname, which was null
        assertFalse(TypeTestUtil.isA(Callable.class, anon));
        assertFalse(TypeTestUtil.isA(Callable.class.getCanonicalName(), anon));
        assertFalse(TypeTestUtil.isExactlyA(Callable.class, anon));
        assertFalse(TypeTestUtil.isExactlyA(Callable.class.getCanonicalName(), anon));
    }

    /**
     * If we don't have the annotation on the classpath,
     * we should resolve the full name via the import, if possible
     * and compare then. Only after that, we should compare the
     * simple names.
     */
    @Test
    void testIsAFallbackAnnotationSimpleNameImport() {
        ASTAnnotation annotation = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);

        assertNull(annotation.getType());
        assertTrue(TypeTestUtil.isA("foo.Stuff", annotation));
        assertFalse(TypeTestUtil.isA("other.Stuff", annotation));
        // we know it's not Stuff, it's foo.Stuff
        assertFalse(TypeTestUtil.isA("Stuff", annotation));
    }

    @Test
    void testNullNode() {
        assertFalse(TypeTestUtil.isA(String.class, (TypeNode) null));
        assertFalse(TypeTestUtil.isA("java.lang.String", (JTypeMirror) null));
        assertFalse(TypeTestUtil.isA("java.lang.String", (TypeNode) null));
        assertFalse(TypeTestUtil.isExactlyA(String.class, (TypeNode) null));
        assertFalse(TypeTestUtil.isExactlyA("java.lang.String", null));
    }

    @Test
    void testNullClass() {
        final ASTAnnotation node = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);
        assertNotNull(node);

        assertThrows(NullPointerException.class, () -> TypeTestUtil.isA((String) null, node));
        assertThrows(NullPointerException.class, () -> TypeTestUtil.isA((Class<?>) null, node));
        assertThrows(NullPointerException.class, () -> TypeTestUtil.isExactlyA((Class<?>) null, node));
        assertThrows(NullPointerException.class, () -> TypeTestUtil.isExactlyA((String) null, node));
    }

    private void assertIsA(TypeNode node, Class<?> type) {
        assertIsA(node, type, false, true);
    }

    private void assertIsExactlyA(TypeNode node, Class<?> type) {
        assertIsA(node, type, true, true);
        assertIsA(node, type, false, true);
    }

    private void assertIsNot(TypeNode node, Class<?> type) {
        assertIsA(node, type, true, false);
        assertIsA(node, type, false, false);
    }

    private void assertIsNotExactly(TypeNode node, Class<?> type) {
        assertIsA(node, type, true, false);
    }

    private void assertIsStrictSubtype(TypeNode node, Class<?> type) {
        assertIsNotExactly(node, type);
        assertIsA(node, type);
    }

    private void assertIsA(TypeNode node, Class<?> type, boolean exactly, boolean expectTrue) {
        assertEquals(expectTrue,
                     exactly ? TypeTestUtil.isExactlyA(type, node)
                             : TypeTestUtil.isA(type, node),
                "TypeTestUtil::isA with class arg: " + type.getCanonicalName());
        assertEquals(expectTrue,
                     exactly ? TypeTestUtil.isExactlyA(type.getCanonicalName(), node)
                             : TypeTestUtil.isA(type.getCanonicalName(), node),
                "TypeTestUtil::isA with string arg: " + type.getCanonicalName());
    }


}
