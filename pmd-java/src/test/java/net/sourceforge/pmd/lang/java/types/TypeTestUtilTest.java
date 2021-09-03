/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.java.types.testdata.SomeClassWithAnon;

public class TypeTestUtilTest extends BaseNonParserTest {

    @Rule
    public final ExpectedException expect = ExpectedException.none();

    @Test
    public void testIsAFallback() {

        ASTClassOrInterfaceDeclaration klass =
            java.parse("package org; import java.io.Serializable; "
                           + "class FooBar implements Serializable {}")
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        Assert.assertTrue(TypeTestUtil.isA("java.io.Serializable", klass));
        Assert.assertTrue(TypeTestUtil.isA(Serializable.class, klass));
    }

    @Test
    public void testIsAFallbackWithUnresolvedClassReference() { // != declaration

        ASTAnnotation annot =
            java.parse("import a.b.Test;"
                           + "class FooBar { @Test void bar() {} }")
                .getFirstDescendantOfType(ASTAnnotation.class);

        Assert.assertTrue(TypeTestUtil.isA("a.b.Test", annot));
        Assert.assertTrue(TypeOps.isUnresolved(annot.getTypeMirror()));

        Assert.assertFalse(TypeTestUtil.isA(org.junit.Test.class, annot));
        Assert.assertFalse(TypeTestUtil.isA("org.junit.Test", annot));
        Assert.assertFalse(TypeTestUtil.isA(Override.class, annot));
        Assert.assertFalse(TypeTestUtil.isA("java.lang.Override", annot));
    }


    @Test
    public void testIsAFallbackEnum() {

        ASTEnumDeclaration klass =
            java.parse("package org; "
                           + "enum FooBar implements Iterable {}")
                .getFirstDescendantOfType(ASTEnumDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        assertIsStrictSubtype(klass, Iterable.class);
        assertIsStrictSubtype(klass, Enum.class);
        assertIsStrictSubtype(klass, Serializable.class);
        assertIsStrictSubtype(klass, Object.class);
    }


    @Test
    public void testIsAnArrayClass() {

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
    public void testIsAnAnnotationClass() {

        ASTType arrayT =
            java.parse("class Foo { org.junit.Test field; }")
                .getFirstDescendantOfType(ASTType.class);


        assertIsExactlyA(arrayT, Test.class);
        assertIsStrictSubtype(arrayT, Annotation.class);
        assertIsStrictSubtype(arrayT, Object.class);
    }

    @Test
    public void testIsAPrimitiveArrayClass() {

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
    public void testIsAPrimitiveSubtype() {

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
    public void testIsAFallbackAnnotation() {

        ASTAnnotationTypeDeclaration klass =
            java.parse("package org; import foo.Stuff;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnnotationTypeDeclaration.class);


        Assert.assertNull(klass.getType());
        Assert.assertTrue(TypeTestUtil.isA("org.FooBar", klass));
        assertIsA(klass, Annotation.class);
        assertIsA(klass, Object.class);
    }

    @Test
    public void testIsATypeVarWithUnresolvedBound() {
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
    public void testIsAStringWithTypeArguments() {

        ASTAnyTypeDeclaration klass =
            java.parse("package org;"
                           + "public class FooBar {}")
                .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);


        expect.expect(IllegalArgumentException.class);
        TypeTestUtil.isA("java.util.List<java.lang.String>", klass);
    }

    @Test
    public void testIsAStringWithTypeArgumentsAnnotation() {

        ASTAnyTypeDeclaration klass =
            java.parse("package org;"
                           + "public @interface FooBar {}")
                .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);


        expect.expect(IllegalArgumentException.class);
        TypeTestUtil.isA("java.util.List<java.lang.String>", klass);
    }

    @Test
    public void testAnonClassTypeNPE() {
        // #2756

        ASTAnonymousClassDeclaration anon =
            java.parseClass(SomeClassWithAnon.class)
                .getFirstDescendantOfType(ASTAnonymousClassDeclaration.class);


        Assert.assertTrue("Anon class", anon.getSymbol().isAnonymousClass());
        Assert.assertTrue("Should be a Runnable", TypeTestUtil.isA(Runnable.class, anon));

        // This is not a canonical name, so we give up early
        Assert.assertFalse(TypeTestUtil.isA(SomeClassWithAnon.class.getName() + "$1", anon));
        Assert.assertFalse(TypeTestUtil.isExactlyA(SomeClassWithAnon.class.getName() + "$1", anon));

        // this is the failure case: if the binary name doesn't match, we test the canoname, which was null
        Assert.assertFalse(TypeTestUtil.isA(Callable.class, anon));
        Assert.assertFalse(TypeTestUtil.isA(Callable.class.getCanonicalName(), anon));
        Assert.assertFalse(TypeTestUtil.isExactlyA(Callable.class, anon));
        Assert.assertFalse(TypeTestUtil.isExactlyA(Callable.class.getCanonicalName(), anon));
    }

    /**
     * If we don't have the annotation on the classpath,
     * we should resolve the full name via the import, if possible
     * and compare then. Only after that, we should compare the
     * simple names.
     */
    @Test
    public void testIsAFallbackAnnotationSimpleNameImport() {
        ASTAnnotation annotation = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);

        Assert.assertNull(annotation.getType());
        Assert.assertTrue(TypeTestUtil.isA("foo.Stuff", annotation));
        Assert.assertFalse(TypeTestUtil.isA("other.Stuff", annotation));
        // we know it's not Stuff, it's foo.Stuff
        Assert.assertFalse(TypeTestUtil.isA("Stuff", annotation));
    }

    @Test
    public void testNullNode() {
        Assert.assertFalse(TypeTestUtil.isA(String.class, (TypeNode) null));
        Assert.assertFalse(TypeTestUtil.isA("java.lang.String", (JTypeMirror) null));
        Assert.assertFalse(TypeTestUtil.isA("java.lang.String", (TypeNode) null));
        Assert.assertFalse(TypeTestUtil.isExactlyA(String.class, (TypeNode) null));
        Assert.assertFalse(TypeTestUtil.isExactlyA("java.lang.String", null));
    }

    @Test
    public void testNullClass() {
        final ASTAnnotation node = java.parse("package org; import foo.Stuff; @Stuff public class FooBar {}")
                                       .getFirstDescendantOfType(ASTAnnotation.class);
        Assert.assertNotNull(node);

        Assert.assertThrows(NullPointerException.class, () -> TypeTestUtil.isA((String) null, node));
        Assert.assertThrows(NullPointerException.class, () -> TypeTestUtil.isA((Class<?>) null, node));
        Assert.assertThrows(NullPointerException.class, () -> TypeTestUtil.isExactlyA((Class<?>) null, node));
        Assert.assertThrows(NullPointerException.class, () -> TypeTestUtil.isExactlyA((String) null, node));
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
        Assert.assertEquals("TypeTestUtil::isA with class arg: " + type.getCanonicalName(),
                            expectTrue,
                            exactly ? TypeTestUtil.isExactlyA(type, node)
                                    : TypeTestUtil.isA(type, node));
        Assert.assertEquals("TypeTestUtil::isA with string arg: " + type.getCanonicalName(),
                            expectTrue,
                            exactly ? TypeTestUtil.isExactlyA(type.getCanonicalName(), node)
                                    : TypeTestUtil.isA(type.getCanonicalName(), node));
    }


}
