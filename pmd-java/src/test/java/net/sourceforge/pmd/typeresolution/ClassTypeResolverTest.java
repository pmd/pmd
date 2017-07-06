/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousClassFromInterface;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.AnoymousExtendingObject;
import net.sourceforge.pmd.typeresolution.testdata.ArrayListFound;
import net.sourceforge.pmd.typeresolution.testdata.DefaultJavaLangImport;
import net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.ExtraTopLevelClass;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccess;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericBounds;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericParameter;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericRaw;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessPrimaryGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessShadow;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper;
import net.sourceforge.pmd.typeresolution.testdata.InnerClass;
import net.sourceforge.pmd.typeresolution.testdata.Literals;
import net.sourceforge.pmd.typeresolution.testdata.NestedAnonymousClass;
import net.sourceforge.pmd.typeresolution.testdata.Operators;
import net.sourceforge.pmd.typeresolution.testdata.Promotion;
import net.sourceforge.pmd.typeresolution.testdata.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.ThisExpression;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.Converter;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB2;


public class ClassTypeResolverTest {

    @Test
    public void testClassNameExists() {
        ClassTypeResolver classTypeResolver = new ClassTypeResolver();
        assertEquals(true, classTypeResolver.classNameExists("java.lang.System"));
        assertEquals(false, classTypeResolver.classNameExists("im.sure.that.this.does.not.Exist"));
        assertEquals(true, classTypeResolver.classNameExists("java.awt.List"));
    }

    @Test
    public void acceptanceTest() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ArrayListFound.class);
        assertEquals(ArrayListFound.class, acu.getFirstDescendantOfType(ASTTypeDeclaration.class).getType());
        assertEquals(ArrayListFound.class,
                     acu.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
        ASTImportDeclaration id = acu.getFirstDescendantOfType(ASTImportDeclaration.class);
        assertEquals("java.util", id.getPackage().getName());
        assertEquals(ArrayList.class, id.getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTReferenceType.class).getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTType.class).getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class).getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTVariableDeclarator.class).getType());
        assertEquals(ArrayList.class, acu.getFirstDescendantOfType(ASTFieldDeclaration.class).getType());

        acu = parseAndTypeResolveForClass15(DefaultJavaLangImport.class);
        assertEquals(String.class, acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType());
        assertEquals(Override.class, acu.findDescendantsOfType(ASTName.class).get(1).getType());
    }

    /**
     * See bug #1138 Anonymous inner class in enum causes NPE
     */
    @Test
    public void testEnumAnonymousInnerClass() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(EnumWithAnonymousInnerClass.class);
        Class<?> inner = acu.getFirstDescendantOfType(ASTAllocationExpression.class)
                .getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType();
        assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$1", inner.getName());
    }

    @Test
    public void testExtraTopLevelClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ExtraTopLevelClass.class);
        Class<?> theExtraTopLevelClass = Class
                .forName("net.sourceforge.pmd.typeresolution.testdata.TheExtraTopLevelClass");
        // First class
        ASTTypeDeclaration typeDeclaration = (ASTTypeDeclaration) acu.jjtGetChild(1);
        assertEquals(ExtraTopLevelClass.class, typeDeclaration.getType());
        assertEquals(ExtraTopLevelClass.class,
                     typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
        // Second class
        typeDeclaration = (ASTTypeDeclaration) acu.jjtGetChild(2);
        assertEquals(theExtraTopLevelClass, typeDeclaration.getType());
        assertEquals(theExtraTopLevelClass,
                     typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
    }

    @Test
    public void testInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(InnerClass.class);
        Class<?> theInnerClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.InnerClass$TheInnerClass");
        // Outer class
        ASTTypeDeclaration typeDeclaration = acu.getFirstDescendantOfType(ASTTypeDeclaration.class);
        assertEquals(InnerClass.class, typeDeclaration.getType());
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        assertEquals(InnerClass.class, outerClassDeclaration.getType());
        // Inner class
        assertEquals(theInnerClass,
                     outerClassDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
        // Method parameter as inner class
        ASTFormalParameter formalParameter = typeDeclaration.getFirstDescendantOfType(ASTFormalParameter.class);
        assertEquals(theInnerClass, formalParameter.getTypeNode().getType());
    }

    /**
     * If we don't have the auxclasspath, we might not find the inner class. In
     * that case, we'll need to search by name for a match.
     *
     * @throws Exception
     */
    @Test
    public void testInnerClassNotCompiled() throws Exception {
        Node acu = parseAndTypeResolveForString("public class TestInnerClass {\n" + "    public void foo() {\n"
                                                        + "        Statement statement = new Statement();\n" + "    "
                                                        + "}\n" + "    static class Statement {\n"
                                                        + "    }\n"
                                                        + "}", "1.8");
        ASTClassOrInterfaceType statement = acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        Assert.assertTrue(statement.isReferenceToClassSameCompilationUnit());
    }

    @Test
    public void testAnonymousClassFromInterface() throws Exception {
        Node acu = parseAndTypeResolveForClass(AnonymousClassFromInterface.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = (TypeNode) allocationExpression.jjtGetChild(0);
        Assert.assertTrue(Comparator.class.isAssignableFrom(child.getType()));
        Assert.assertSame(Integer.class, child.getTypeDefinition().getGenericType(0).getType());
    }
    
    @Test
    public void testNestedAnonymousClass() throws Exception {
        Node acu = parseAndTypeResolveForClass(NestedAnonymousClass.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        ASTAllocationExpression nestedAllocation = allocationExpression.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = (TypeNode) nestedAllocation.jjtGetChild(0);
        Assert.assertTrue(Converter.class.isAssignableFrom(child.getType()));
        Assert.assertSame(String.class, child.getTypeDefinition().getGenericType(0).getType());
    }
    
    @Test
    public void testAnoymousExtendingObject() throws Exception {
        Node acu = parseAndTypeResolveForClass(AnoymousExtendingObject.class, "1.8");
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = (TypeNode) allocationExpression.jjtGetChild(0);
        Assert.assertTrue(Object.class.isAssignableFrom(child.getType()));
    }

    @Test
    public void testAnonymousInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(AnonymousInnerClass.class);
        Class<?> theAnonymousInnerClass = Class
                .forName("net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass$1");
        // Outer class
        ASTTypeDeclaration typeDeclaration = acu.getFirstDescendantOfType(ASTTypeDeclaration.class);
        assertEquals(AnonymousInnerClass.class, typeDeclaration.getType());
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration
                .getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        assertEquals(AnonymousInnerClass.class, outerClassDeclaration.getType());
        // Anonymous Inner class
        assertEquals(theAnonymousInnerClass,
                     outerClassDeclaration.getFirstDescendantOfType(ASTAllocationExpression.class).getType());
    }

    @Test
    public void testLiterals() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Literals.class);
        List<ASTLiteral> literals = convertList(acu.findChildNodesWithXPath("//Literal"), ASTLiteral.class);
        int index = 0;

        // String s = "s";
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(String.class, literals.get(index++).getType());

        // boolean boolean1 = false;
        assertEquals(Boolean.TYPE, literals.get(index).getFirstDescendantOfType(ASTBooleanLiteral.class).getType());
        assertEquals(Boolean.TYPE, literals.get(index++).getType());

        // boolean boolean2 = true;
        assertEquals(Boolean.TYPE, literals.get(index).getFirstDescendantOfType(ASTBooleanLiteral.class).getType());
        assertEquals(Boolean.TYPE, literals.get(index++).getType());

        // Object obj = null;
        assertNull(literals.get(index).getFirstDescendantOfType(ASTNullLiteral.class).getType());
        assertNull(literals.get(index++).getType());

        // byte byte1 = 0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // byte byte2 = 0x0F;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // byte byte3 = -007;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short1 = 0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short2 = 0x0F;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short3 = -007;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char1 = 0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char2 = 0x0F;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char3 = 007;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char4 = 'a';
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // int int1 = 0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int2 = 0x0F;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int3 = -007;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int4 = 'a';
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // long long1 = 0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long2 = 0x0F;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long3 = -007;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long4 = 0L;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long5 = 0x0Fl;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long6 = -007L;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long7 = 'a';
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // float float1 = 0.0f;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float2 = -10e+01f;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float3 = 0x08.08p3f;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float4 = 0xFF;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // float float5 = 'a';
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // double double1 = 0.0;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double2 = -10e+01;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double3 = 0x08.08p3;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double4 = 0xFF;
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // double double5 = 'a';
        assertEquals(0, literals.get(index).jjtGetNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All literals not tested", index, literals.size());
    }

    @Test
    public void testUnaryNumericPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'unaryNumericPromotion']]//Expression[UnaryExpression]"),
                ASTExpression.class);
        int index = 0;

        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryNumericPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'binaryNumericPromotion']]//Expression[AdditiveExpression]"),
                ASTExpression.class);
        int index = 0;

        // LHS = byte
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = short
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = char
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = int
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = long
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = float
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Float.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        // LHS = double
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryStringPromotion() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Promotion.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryStringPromotion']]//Expression"),
                ASTExpression.class);
        int index = 0;

        assertEquals(String.class, expressions.get(index++).getType());
        assertEquals(String.class, expressions.get(index++).getType());
        assertEquals(String.class, expressions.get(index++).getType());
        assertEquals(String.class, expressions.get(index++).getType());
        assertEquals(String.class, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testUnaryLogicalOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryLogicalOperators']]//Expression"),
                ASTExpression.class);
        int index = 0;

        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryLogicalOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryLogicalOperators']]//Expression"),
                ASTExpression.class);
        int index = 0;

        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testUnaryNumericOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<TypeNode> expressions = new ArrayList<>();
        expressions.addAll(convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//Expression"),
                TypeNode.class));
        expressions.addAll(convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'unaryNumericOperators']]//PostfixExpression"),
                TypeNode.class));
        expressions.addAll(convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'unaryNumericOperators']]//PreIncrementExpression"),
                TypeNode.class));
        expressions.addAll(convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'unaryNumericOperators']]//PreDecrementExpression"),
                TypeNode.class));

        int index = 0;
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());
        assertEquals(Double.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    private static <T> List<T> convertList(List<Node> nodes, Class<T> target) {
        List<T> converted = new ArrayList<>();
        for (Node n : nodes) {
            converted.add(target.cast(n));
        }
        return converted;
    }

    @Test
    public void testBinaryNumericOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryNumericOperators']]//Expression"),
                ASTExpression.class);
        int index = 0;

        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());
        assertEquals(Integer.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testAssignmentOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTStatementExpression> expressions = convertList(
                acu.findChildNodesWithXPath(
                        "//Block[preceding-sibling::MethodDeclarator[@Image = "
                                + "'assignmentOperators']]//StatementExpression"),
                ASTStatementExpression.class);
        int index = 0;

        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());
        assertEquals(Long.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    /**
     * The type should be filled also on the ASTVariableDeclaratorId node, not
     * only on the variable name declaration.
     */
    @Test
    public void testFullyQualifiedType() {
        String source = "public class Foo {\n" + "    public void bar() {\n"
                + "        java.util.StringTokenizer st = new StringTokenizer(\"a.b.c.d\", \".\");\n"
                + "        while (st.hasMoreTokens()) {\n" + "            System.out.println(st.nextToken());\n"
                + "        }\n" + "    }\n" + "}";
        ASTCompilationUnit acu = parseAndTypeResolveForString(source, "1.5");
        List<ASTName> names = acu.findDescendantsOfType(ASTName.class);
        ASTName theStringTokenizer = null;
        for (ASTName name : names) {
            if (name.hasImageEqualTo("st.hasMoreTokens")) {
                theStringTokenizer = name;
                break;
            }
        }
        Assert.assertNotNull(theStringTokenizer);
        VariableNameDeclaration declaration = (VariableNameDeclaration) theStringTokenizer.getNameDeclaration();
        Assert.assertNotNull(declaration);
        Assert.assertEquals("java.util.StringTokenizer", declaration.getTypeImage());
        Assert.assertNotNull(declaration.getType());
        Assert.assertSame(StringTokenizer.class, declaration.getType());
        ASTVariableDeclaratorId id = (ASTVariableDeclaratorId) declaration.getNode();
        Assert.assertNotNull(id.getType());
        Assert.assertSame(StringTokenizer.class, id.getType());
    }

    @Test
    public void testThisExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ThisExpression.class);

        List<ASTPrimaryExpression> expressions = convertList(
                acu.findChildNodesWithXPath("//PrimaryExpression"),
                ASTPrimaryExpression.class);
        List<ASTPrimaryPrefix> prefixes = convertList(
                acu.findChildNodesWithXPath("//PrimaryPrefix"),
                ASTPrimaryPrefix.class);

        int index = 0;


        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index++).getType());
        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index++).getType());
        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index++).getType());
        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index++).getType());

        assertEquals(ThisExpression.ThisExprNested.class, expressions.get(index).getType());
        assertEquals(ThisExpression.ThisExprNested.class, prefixes.get(index++).getType());

        // Qualified this
        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index).getType());
        assertEquals(ThisExpression.class, ((TypeNode) expressions.get(index++).jjtGetChild(1)).getType());

        assertEquals(ThisExpression.ThisExprStaticNested.class, expressions.get(index).getType());
        assertEquals(ThisExpression.ThisExprStaticNested.class, prefixes.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
        assertEquals("All expressions not tested", index, prefixes.size());
    }

    @Test
    public void testSuperExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(SuperExpression.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"),
                AbstractJavaTypeNode.class);

        int index = 0;

        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperExpression.class, ((TypeNode) expressions.get(index).jjtGetParent().jjtGetChild(0))
                .getType());
        assertEquals(SuperClassA.class, ((TypeNode) expressions.get(index++).jjtGetParent().jjtGetChild(1)).getType());

        assertEquals(SuperExpression.class, expressions.get(index++).getType());
        assertEquals(SuperExpression.class, expressions.get(index++).getType());


        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }


    @Test
    public void testFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccess.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);

        int index = 0;

        // param.field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // local.field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // f.f.f.field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // (this).f.f.field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(FieldAccess.class, getChildType(expressions.get(index), 0));
        assertEquals(FieldAccess.class, getChildType(expressions.get(index), 1));
        assertEquals(FieldAccess.class, getChildType(expressions.get(index), 2));
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 3));

        // field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessNested() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessNested.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);

        int index = 0;

        // field = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // a = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 0));

        // net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested.Nested.this.a = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 0));
        assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 2));

        // FieldAccessNested.Nested.this.a = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 0));
        assertEquals(FieldAccessNested.Nested.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 2));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessShadow() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessShadow.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);

        int index = 0;

        // field = "shadow";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // this.field = new Integer(10);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(FieldAccessShadow.class, getChildType(expressions.get(index), 0));
        assertEquals(Integer.class, getChildType(expressions.get(index++), 1));

        // (this).field = new Integer(10);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(FieldAccessShadow.class, getChildType(expressions.get(index), 0));
        assertEquals(Integer.class, getChildType(expressions.get(index++), 1));

        // s2 = new SuperClassB2();
        assertEquals(SuperClassB2.class, expressions.get(index).getType());
        assertEquals(SuperClassB2.class, getChildType(expressions.get(index++), 0));

        // privateShadow = 10;
        assertEquals(Number.class, expressions.get(index).getType());
        assertEquals(Number.class, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessSuper() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessSuper.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);

        int index = 0;

        // s = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 0));

        // (this).s.s2 = new SuperClassA2();
        assertEquals(SuperClassA2.class, expressions.get(index).getType());
        assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassA2.class, getChildType(expressions.get(index++), 2));

        // s.s.s2 = new SuperClassA2();
        assertEquals(SuperClassA2.class, expressions.get(index).getType());
        assertEquals(SuperClassA2.class, getChildType(expressions.get(index++), 0));

        // super.s = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(SuperClassA.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 1));

        // net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper.this.s = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 2));

        // s = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 0));

        // bs = new SuperClassB();
        assertEquals(SuperClassB.class, expressions.get(index).getType());
        assertEquals(SuperClassB.class, getChildType(expressions.get(index++), 0));

        // FieldAccessSuper.Nested.super.bs = new SuperClassB();
        assertEquals(SuperClassB.class, expressions.get(index).getType());
        assertEquals(FieldAccessSuper.Nested.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassB.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassB.class, getChildType(expressions.get(index++), 2));

        // FieldAccessSuper.super.s = new SuperClassA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(FieldAccessSuper.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index), 1));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 2));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBoundsGenericFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessGenericBounds.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);


        int index = 0;

        // superGeneric.first = ""; // Object
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));

        // superGeneric.second = null; // Object
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));

        // inheritedSuperGeneric.first = ""; // Object
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));

        // inheritedSuperGeneric.second = null; // Object
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));

        // upperBound.first = null; // Number
        assertEquals(Number.class, expressions.get(index).getType());
        assertEquals(Number.class, getChildType(expressions.get(index++), 0));

        // inheritedUpperBound.first = null; // String
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));


        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testParameterGenericFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessGenericParameter.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);


        int index = 0;

        // classGeneric = null; // Double
        assertEquals(Double.class, expressions.get(index).getType());
        assertEquals(Double.class, getChildType(expressions.get(index++), 0));

        // localGeneric = null; // Character
        assertEquals(Character.class, expressions.get(index).getType());
        assertEquals(Character.class, getChildType(expressions.get(index++), 0));

        // parameterGeneric.second.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));

        // localGeneric = null; // Number
        assertEquals(Number.class, expressions.get(index).getType());
        assertEquals(Number.class, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testSimpleGenericFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessGenericSimple.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);


        int index = 0;

        // genericField.first = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // genericField.second = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertEquals(Double.class, getChildType(expressions.get(index++), 0));

        //genericTypeArg.second.second = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertEquals(Double.class, getChildType(expressions.get(index++), 0));

        // param.first = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));

        // local.second = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));


        // param.generic.first = new Character('c');
        assertEquals(Character.class, expressions.get(index).getType());
        assertEquals(Character.class, getChildType(expressions.get(index++), 0));

        // local.generic.second = new Float(0);
        assertEquals(Float.class, expressions.get(index).getType());
        assertEquals(Float.class, getChildType(expressions.get(index++), 0));

        // genericField.generic.generic.generic.first = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertEquals(Double.class, getChildType(expressions.get(index++), 0));

        // fieldA = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));

        // fieldB.generic.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // fieldAcc.fieldA = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));

        // fieldA = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testRawGenericFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessGenericRaw.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);


        int index = 0;

        // rawGeneric.first = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));

        // rawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));

        // rawGeneric.third = new Object();
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));
        // rawGeneric.fourth.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));
        // rawGeneric.rawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.first = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.third = new Object();
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.fourth.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.rawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.first = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.third = new Object();
        assertEquals(Object.class, expressions.get(index).getType());
        assertEquals(Object.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.fourth.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.rawGeneric.second = new Integer(0);
        assertEquals(Integer.class, expressions.get(index).getType());
        assertEquals(Integer.class, getChildType(expressions.get(index++), 0));

        // bug #471
        // rawGeneric.fifth = new GenericClass();
        assertEquals(GenericClass.class, expressions.get(index).getType());
        assertEquals(GenericClass.class, getChildType(expressions.get(index++), 0));
        // inheritedRawGeneric.fifth = new GenericClass();
        assertEquals(GenericClass.class, expressions.get(index).getType());
        assertEquals(GenericClass.class, getChildType(expressions.get(index++), 0));
        // parameterRawGeneric.fifth = new GenericClass();
        assertEquals(GenericClass.class, expressions.get(index).getType());
        assertEquals(GenericClass.class, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testPrimarySimpleGenericFieldAccess() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(FieldAccessPrimaryGenericSimple.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//StatementExpression/PrimaryExpression"),
                AbstractJavaTypeNode.class);


        int index = 0;

        // this.genericField.first = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 1, String.class, Double.class);
        assertEquals(String.class, getChildType(expressions.get(index++), 2));

        // (this).genericField.second = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 1, String.class, Double.class);
        assertEquals(Double.class, getChildType(expressions.get(index++), 2));

        // this.genericTypeArg.second.second = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 2, Number.class, Double.class);
        assertEquals(Double.class, getChildType(expressions.get(index++), 3));

        // (this).genericField.generic.generic.generic.first = new Double(0);
        assertEquals(Double.class, expressions.get(index).getType());
        assertEquals(Double.class, getChildType(expressions.get(index++), 5));

        // (this).fieldA = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 1));

        // this.fieldB.generic.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 3));

        // super.fieldA = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertChildTypeArgsEqualTo(expressions.get(index), 0, Long.class);
        assertEquals(Long.class, getChildType(expressions.get(index++), 1));

        // super.fieldB.generic.second = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 3));

        // this.field.first = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 2));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    private Class<?> getChildType(Node node, int childIndex) {
        return ((TypeNode) node.jjtGetChild(childIndex)).getType();
    }

    private void assertChildTypeArgsEqualTo(Node node, int childIndex, Class<?>... classes) {
        JavaTypeDefinition typeDef = ((TypeNode) node.jjtGetChild(childIndex)).getTypeDefinition();

        for (int index = 0; index < classes.length; ++index) {
            assertSame(classes[index], typeDef.getGenericType(index).getType());
        }
    }

    private ASTCompilationUnit parseAndTypeResolveForClass15(Class<?> clazz) {
        return parseAndTypeResolveForClass(clazz, "1.5");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the src/test/java folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndTypeResolveForClass(Class<?> clazz, String version) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverTest.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        String source;
        try {
            source = IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseAndTypeResolveForString(source, version);
    }

    private ASTCompilationUnit parseAndTypeResolveForString(String source, String version) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(source));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(ClassTypeResolverTest.class.getClassLoader()).start(acu);
        return acu;
    }
}
