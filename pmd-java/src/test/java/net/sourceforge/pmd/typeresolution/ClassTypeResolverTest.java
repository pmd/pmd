/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static junit.framework.TestCase.assertTrue;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition.forClass;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.LOWER_WILDCARD;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.UPPER_WILDCARD;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.LOOSE_INVOCATION;
import static net.sourceforge.pmd.lang.java.typeresolution.typeinference.InferenceRuleType.SUBTYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.java.typeresolution.MethodType;
import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Bound;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Constraint;
import net.sourceforge.pmd.lang.java.typeresolution.typeinference.Variable;
import net.sourceforge.pmd.typeresolution.testdata.AbstractReturnTypeUseCase;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousClassFromInterface;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.AnoymousExtendingObject;
import net.sourceforge.pmd.typeresolution.testdata.ArrayAccess;
import net.sourceforge.pmd.typeresolution.testdata.ArrayListFound;
import net.sourceforge.pmd.typeresolution.testdata.ArrayTypes;
import net.sourceforge.pmd.typeresolution.testdata.ArrayVariableDeclaration;
import net.sourceforge.pmd.typeresolution.testdata.DefaultJavaLangImport;
import net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.ExtraTopLevelClass;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccess;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericBounds;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericNested;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericParameter;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericRaw;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessPrimaryGenericSimple;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessShadow;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessStatic;
import net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper;
import net.sourceforge.pmd.typeresolution.testdata.GenericMethodsImplicit;
import net.sourceforge.pmd.typeresolution.testdata.GenericsArrays;
import net.sourceforge.pmd.typeresolution.testdata.InnerClass;
import net.sourceforge.pmd.typeresolution.testdata.JavaTypeDefinitionToStringNPE;
import net.sourceforge.pmd.typeresolution.testdata.Literals;
import net.sourceforge.pmd.typeresolution.testdata.LocalGenericClass;
import net.sourceforge.pmd.typeresolution.testdata.MethodAccessibility;
import net.sourceforge.pmd.typeresolution.testdata.MethodCallExpressionTypes;
import net.sourceforge.pmd.typeresolution.testdata.MethodFirstPhase;
import net.sourceforge.pmd.typeresolution.testdata.MethodGenericExplicit;
import net.sourceforge.pmd.typeresolution.testdata.MethodGenericParam;
import net.sourceforge.pmd.typeresolution.testdata.MethodMostSpecific;
import net.sourceforge.pmd.typeresolution.testdata.MethodPotentialApplicability;
import net.sourceforge.pmd.typeresolution.testdata.MethodSecondPhase;
import net.sourceforge.pmd.typeresolution.testdata.MethodStaticAccess;
import net.sourceforge.pmd.typeresolution.testdata.MethodThirdPhase;
import net.sourceforge.pmd.typeresolution.testdata.NestedAllocationExpressions;
import net.sourceforge.pmd.typeresolution.testdata.NestedAnonymousClass;
import net.sourceforge.pmd.typeresolution.testdata.Operators;
import net.sourceforge.pmd.typeresolution.testdata.OverloadedMethodsUsage;
import net.sourceforge.pmd.typeresolution.testdata.PmdStackOverflow;
import net.sourceforge.pmd.typeresolution.testdata.Promotion;
import net.sourceforge.pmd.typeresolution.testdata.SubTypeUsage;
import net.sourceforge.pmd.typeresolution.testdata.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.ThisExpression;
import net.sourceforge.pmd.typeresolution.testdata.VarArgsMethodUseCase;
import net.sourceforge.pmd.typeresolution.testdata.VarargsAsFixedArity;
import net.sourceforge.pmd.typeresolution.testdata.VarargsZeroArity;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.Converter;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.JavaTypeDefinitionEquals;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB2;

// TODO split that class
public class ClassTypeResolverTest {

    private final JavaParsingHelper java8 = JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("1.8");
    private final JavaParsingHelper java4 = java8.withDefaultVersion("1.4");
    private final JavaParsingHelper java5 = java8.withDefaultVersion("1.5");
    private final JavaParsingHelper java7 = java8.withDefaultVersion("1.7");
    private final JavaParsingHelper java11 = java8.withDefaultVersion("11");
    private final JavaParsingHelper java9 = java8.withDefaultVersion("9");


    @Test
    public void stackOverflowTest() {
        // See #831 https://github.com/pmd/pmd/issues/831 - [java] StackOverflow in JavaTypeDefinitionSimple.toString
        java5.parseClass(PmdStackOverflow.class);
    }

    @Test
    public void testClassNameExists() {
        ClassTypeResolver classTypeResolver = new ClassTypeResolver();
        assertEquals(true, classTypeResolver.classNameExists("java.lang.System"));
        assertEquals(false, classTypeResolver.classNameExists("im.sure.that.this.does.not.Exist"));
        assertEquals(true, classTypeResolver.classNameExists("java.awt.List"));
    }

    @Test
    public void acceptanceTest() {
        ASTCompilationUnit acu = java5.parseClass(ArrayListFound.class);
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

        acu = java5.parseClass(DefaultJavaLangImport.class);
        assertEquals(String.class, acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType());
        assertEquals(Override.class, acu.findDescendantsOfType(ASTName.class).get(1).getType());
    }

    /**
     * See bug #1138 Anonymous inner class in enum causes NPE
     */
    @Test
    public void testEnumAnonymousInnerClass() {
        ASTCompilationUnit acu = java5.parseClass(EnumWithAnonymousInnerClass.class);
        // try it in jshell, an enum constant with a body is compiled to an anonymous class,
        // the counter is shared with other anonymous classes of the enum
        Class<?> enumAnon = acu.getFirstDescendantOfType(ASTEnumConstant.class).getQualifiedName().getType();
        assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$1", enumAnon.getName());

        Class<?> inner = acu.getFirstDescendantOfType(ASTAllocationExpression.class)
                .getFirstDescendantOfType(ASTClassOrInterfaceType.class).getType();
        assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$2", inner.getName());
    }

    /**
     * See bug #899 toString causes NPE
     */
    @Test
    public void testNPEInJavaTypeDefinitionToString() {
        // Just parsing this file throws a NPE
        java8.parseClass(JavaTypeDefinitionToStringNPE.class);
    }

    @Test
    public void testExtraTopLevelClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = java5.parseClass(ExtraTopLevelClass.class);
        Class<?> theExtraTopLevelClass = Class
                .forName("net.sourceforge.pmd.typeresolution.testdata.TheExtraTopLevelClass");
        // First class
        ASTTypeDeclaration typeDeclaration = (ASTTypeDeclaration) acu.getChild(1);
        assertEquals(ExtraTopLevelClass.class, typeDeclaration.getType());
        assertEquals(ExtraTopLevelClass.class,
                     typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
        // Second class
        typeDeclaration = (ASTTypeDeclaration) acu.getChild(2);
        assertEquals(theExtraTopLevelClass, typeDeclaration.getType());
        assertEquals(theExtraTopLevelClass,
                     typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
    }

    @Test
    public void testInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = java5.parseClass(InnerClass.class);
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
        assertEquals(theInnerClass, formalParameter.getType());
    }

    /**
     * If we don't have the auxclasspath, we might not find the inner class. In
     * that case, we'll need to search by name for a match.
     *
     * @throws Exception
     */
    @Test
    public void testInnerClassNotCompiled() throws Exception {
        Node acu = java8.parse("public class TestInnerClass {\n" + "    public void foo() {\n"
                                                        + "        Statement statement = new Statement();\n" + "    "
                                                        + "}\n" + "    static class Statement {\n"
                                                        + "    }\n"
                                                        + "}");
        ASTClassOrInterfaceType statement = acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        Assert.assertTrue(statement.isReferenceToClassSameCompilationUnit());
    }

    @Test
    public void testAnonymousClassFromInterface() throws Exception {
        Node acu = java8.parseClass(AnonymousClassFromInterface.class);
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = (TypeNode) allocationExpression.getChild(0);
        Assert.assertTrue(Comparator.class.isAssignableFrom(child.getType()));
        Assert.assertSame(Integer.class, child.getTypeDefinition().getGenericType(0).getType());
    }

    @Test
    public void testNestedAnonymousClass() throws Exception {
        Node acu = java8.parseClass(NestedAnonymousClass.class);
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        ASTAllocationExpression nestedAllocation
                = allocationExpression.getFirstDescendantOfType(ASTClassOrInterfaceBodyDeclaration.class) // get the declaration (boundary)
                .getFirstDescendantOfType(ASTAllocationExpression.class); // and dive for the nested allocation
        TypeNode child = (TypeNode) nestedAllocation.getChild(0);
        Assert.assertTrue(Converter.class.isAssignableFrom(child.getType()));
        Assert.assertSame(String.class, child.getTypeDefinition().getGenericType(0).getType());
    }

    @Test
    public void testAnonymousExtendingObject() throws Exception {
        Node acu = java8.parseClass(AnoymousExtendingObject.class);
        ASTAllocationExpression allocationExpression = acu.getFirstDescendantOfType(ASTAllocationExpression.class);
        TypeNode child = (TypeNode) allocationExpression.getChild(0);
        Assert.assertTrue(Object.class.isAssignableFrom(child.getType()));
    }

    @Test
    public void testAnonymousInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = java5.parseClass(AnonymousInnerClass.class);
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
        List<ASTLiteral> literals = selectNodes(Literals.class, ASTLiteral.class);
        int index = 0;

        // String s = "s";
        assertEquals(0, literals.get(index).getNumChildren());
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
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // byte byte2 = 0x0F;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // byte byte3 = -007;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short1 = 0;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short2 = 0x0F;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // short short3 = -007;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char1 = 0;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char2 = 0x0F;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char3 = 007;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // char char4 = 'a';
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // int int1 = 0;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int2 = 0x0F;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int3 = -007;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // int int4 = 'a';
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // long long1 = 0;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long2 = 0x0F;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long3 = -007;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // long long4 = 0L;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long5 = 0x0Fl;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long6 = -007L;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Long.TYPE, literals.get(index++).getType());

        // long long7 = 'a';
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // float float1 = 0.0f;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float2 = -10e+01f;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float3 = 0x08.08p3f;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Float.TYPE, literals.get(index++).getType());

        // float float4 = 0xFF;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // float float5 = 'a';
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // double double1 = 0.0;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double2 = -10e+01;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double3 = 0x08.08p3;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Double.TYPE, literals.get(index++).getType());

        // double double4 = 0xFF;
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Integer.TYPE, literals.get(index++).getType());

        // double double5 = 'a';
        assertEquals(0, literals.get(index).getNumChildren());
        assertEquals(Character.TYPE, literals.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All literals not tested", index, literals.size());
    }

    @Test
    public void testUnaryNumericPromotion() throws JaxenException {
        ASTCompilationUnit acu = java5.parseClass(Promotion.class);
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
        ASTCompilationUnit acu = java5.parseClass(Promotion.class);
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
        ASTCompilationUnit acu = java5.parseClass(Promotion.class);
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
        ASTCompilationUnit acu = java5.parseClass(Operators.class);
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
        ASTCompilationUnit acu = java5.parseClass(Operators.class);
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
        ASTCompilationUnit acu = java5.parseClass(Operators.class);
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

    @Test
    public void testBinaryNumericOperators() throws JaxenException {
        ASTCompilationUnit acu = java5.parseClass(Operators.class);
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
        ASTCompilationUnit acu = java5.parseClass(Operators.class);
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
        ASTCompilationUnit acu = java5.parse(source);
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
    public void testThisExpression() {
        ASTCompilationUnit compilationUnit = java5.parseClass(ThisExpression.class);
        // need to cross borders, to find expressions of the nested classes
        List<ASTPrimaryExpression> expressions = compilationUnit.findDescendantsOfType(ASTPrimaryExpression.class, true);
        List<ASTPrimaryPrefix> prefixes = compilationUnit.findDescendantsOfType(ASTPrimaryPrefix.class, true);

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
        assertEquals(ThisExpression.class, ((TypeNode) expressions.get(index++).getChild(1)).getType());

        assertEquals(ThisExpression.ThisExprStaticNested.class, expressions.get(index).getType());
        assertEquals(ThisExpression.ThisExprStaticNested.class, prefixes.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
        assertEquals("All expressions not tested", index, prefixes.size());
    }

    @Test
    public void testSuperExpression() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(SuperExpression.class, AbstractJavaTypeNode.class,
                                                             "//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix");

        int index = 0;

        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperClassA.class, expressions.get(index++).getType());
        assertEquals(SuperExpression.class, ((TypeNode) expressions.get(index).getParent().getChild(0))
                .getType());
        assertEquals(SuperClassA.class, ((TypeNode) expressions.get(index++).getParent().getChild(1)).getType());

        assertEquals(SuperExpression.class, expressions.get(index++).getType());
        assertEquals(SuperExpression.class, expressions.get(index++).getType());


        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testArrayTypes() throws JaxenException {
        // We must not select the expression in the dimensions
        List<ASTExpression> expressions = selectNodes(ArrayTypes.class, ASTExpression.class, "//VariableInitializer/Expression");

        int index = 0;

        // int[] a = new int[1];
        //           ----------
        assertEquals(int[].class, expressions.get(index++).getType());

        // Object[][] b = new Object[1][0];
        //                ----------------
        assertEquals(Object[][].class, expressions.get(index++).getType());

        // ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };
        //                      ---------------------------------------------
        assertEquals(ArrayTypes[][][].class, expressions.get(index++).getType());

        // ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };
        //                                             --------------------
        assertEquals(ArrayTypes[][].class, expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testArrayAccess() throws JaxenException {
        List<ASTExpression> expressions = selectNodes(ArrayAccess.class, ASTExpression.class, "//VariableInitializer/Expression");

        int index = 1;
        // int aElement = a[0];
        assertEquals(int.class, expressions.get(index).getType());

        index += 2;
        // Object bElement = b[0][0];
        assertEquals(Object.class, expressions.get(index).getType());

        index += 3;
        // ArrayAccess cElement = c[0][0][0];
        assertEquals(ArrayAccess.class, expressions.get(index).getType());
    }

    @Test
    public void testReferenceType() {
        List<ASTReferenceType> referenceTypes = selectNodes(ArrayTypes.class, ASTReferenceType.class);

        int index = 0;

        // int[] a = new int[1];
        // -----
        assertEquals(int[].class, referenceTypes.get(index++).getType());

        // Object[][] b = new Object[1][0];
        // ----------
        assertEquals(Object[][].class, referenceTypes.get(index++).getType());

        // ArrayTypes[][][] c = new ArrayTypes[][][] { ... };
        // ----------------
        assertEquals(ArrayTypes[][][].class, referenceTypes.get(index++).getType());


        // Make sure we got them all
        assertEquals("All expressions not tested", index, referenceTypes.size());
    }


    @Test
    public void testHeterogeneousArrayFieldDeclaration() throws JaxenException {
        List<ASTFieldDeclaration> fields = selectNodes(ArrayVariableDeclaration.class, ASTFieldDeclaration.class);
        List<ASTLocalVariableDeclaration> locals = selectNodes(ArrayVariableDeclaration.class, ASTLocalVariableDeclaration.class);

        // public int[] a, b[];
        testPrimitiveTypeFieldDecl(fields.get(0));
        testPrimitiveTypeFieldDecl(locals.get(0));

        // public String[] c, d[];
        testRefTypeFieldDecl(fields.get(1));
        testRefTypeFieldDecl(locals.get(1));
    }


    // subtest
    private void testPrimitiveTypeFieldDecl(Node declaration) throws JaxenException {
        // public int[] a, b[];

        ASTReferenceType typeNode = declaration.getFirstChildOfType(ASTType.class).getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(typeNode);
        assertTrue(typeNode.isArray());
        assertEquals(1, typeNode.getArrayDepth());
        assertEquals("int", typeNode.getFirstChildOfType(ASTPrimitiveType.class).getImage());

        ASTVariableDeclaratorId aID = declaration.getFirstChildOfType(ASTVariableDeclarator.class).getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertNotNull(aID);
        assertEquals("a", aID.getImage());
        assertFalse(aID.isArray());
        assertEquals(0, aID.getArrayDepth());
        assertEquals(int[].class, aID.getType());

        ASTVariableDeclaratorId bID = declaration.findChildrenOfType(ASTVariableDeclarator.class).get(1).getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertNotNull(bID);
        assertEquals("b", bID.getImage());
        assertTrue(bID.isArray());
        assertEquals(1, bID.getArrayDepth());
        assertEquals(int[][].class, bID.getType());
    }


    // subtest
    private void testRefTypeFieldDecl(Node declaration) throws JaxenException {

        // public String[] c, d[];

        ASTReferenceType typeNode = declaration.getFirstChildOfType(ASTType.class).getFirstChildOfType(ASTReferenceType.class);
        assertNotNull(typeNode);
        assertTrue(typeNode.isArray());
        assertEquals(1, typeNode.getArrayDepth());
        assertEquals("String", typeNode.getFirstChildOfType(ASTClassOrInterfaceType.class).getImage());

        ASTVariableDeclaratorId cID = declaration.getFirstChildOfType(ASTVariableDeclarator.class).getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertNotNull(cID);
        assertEquals("c", cID.getImage());
        assertFalse(cID.isArray());
        assertEquals(0, cID.getArrayDepth());
        assertEquals(String[].class, cID.getType());

        ASTVariableDeclaratorId dID = declaration.findChildrenOfType(ASTVariableDeclarator.class).get(1).getFirstChildOfType(ASTVariableDeclaratorId.class);
        assertNotNull(dID);
        assertEquals("d", dID.getImage());
        assertTrue(dID.isArray());
        assertEquals(1, dID.getArrayDepth());
        assertEquals(String[][].class, dID.getType());
    }


    @Test
    public void testFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccess.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessNested.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessShadow.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessSuper.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericBounds.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");


        int index = 0;

        // superGeneric.first = ""; // ? super String
        assertEquals(forClass(LOWER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(LOWER_WILDCARD, String.class), getChildTypeDef(expressions.get(index++), 0));

        // superGeneric.second = null; // ?
        assertEquals(forClass(UPPER_WILDCARD, Object.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(UPPER_WILDCARD, Object.class), getChildTypeDef(expressions.get(index++), 0));

        // inheritedSuperGeneric.first = ""; // ? super String
        assertEquals(forClass(LOWER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(LOWER_WILDCARD, String.class), getChildTypeDef(expressions.get(index++), 0));

        // inheritedSuperGeneric.second = null; // ?
        assertEquals(forClass(UPPER_WILDCARD, Object.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(UPPER_WILDCARD, Object.class), getChildTypeDef(expressions.get(index++), 0));

        // upperBound.first = null; // ? extends Number
        assertEquals(forClass(UPPER_WILDCARD, Number.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(UPPER_WILDCARD, Number.class), getChildTypeDef(expressions.get(index++), 0));

        // inheritedUpperBound.first = null; // ? extends String
        assertEquals(forClass(UPPER_WILDCARD, String.class), expressions.get(index).getTypeDefinition());
        assertEquals(forClass(UPPER_WILDCARD, String.class), getChildTypeDef(expressions.get(index++), 0));


        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testParameterGenericFieldAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericParameter.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");


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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericSimple.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericRaw.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");


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
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessPrimaryGenericSimple.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");


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

    @Test
    public void testFieldAccessGenericNested() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessGenericNested.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

        int index = 0;

        // n.field = null;
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // n.generic.first = null;
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testFieldAccessStatic() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(FieldAccessStatic.class, AbstractJavaTypeNode.class,
                                                             "//StatementExpression/PrimaryExpression");

        int index = 0;

        // staticPrimitive = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // staticGeneric.first = new Long(0);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));

        // StaticMembers.staticPrimitive = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers.staticPrimitive = 10;
        assertEquals(Integer.TYPE, expressions.get(index).getType());
        assertEquals(Integer.TYPE, getChildType(expressions.get(index++), 0));

        // net.sourceforge.pmd.typeresolution.testdata.dummytypes.StaticMembers
        //       .staticGeneric.generic.second = new Long(10);
        assertEquals(Long.class, expressions.get(index).getType());
        assertEquals(Long.class, getChildType(expressions.get(index++), 0));

        // staticPrimitive = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));

        // staticChar = 3.1; // it's a double
        assertEquals(Double.TYPE, expressions.get(index).getType());
        assertEquals(Double.TYPE, getChildType(expressions.get(index++), 0));

        // FieldAccessStatic.Nested.staticPrimitive = "";
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index++), 0));


        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }


    @Test
    public void testMethodPotentialApplicability() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodPotentialApplicability.class, AbstractJavaTypeNode.class,
                                                             "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // int a = vararg("");
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // int b = vararg("", 10);
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // String c = notVararg(0, 0);
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 0));
        assertEquals(String.class, getChildType(expressions.get(index++), 1));

        // Number d = noArguments();
        assertEquals(Number.class, expressions.get(index).getType());
        assertEquals(Number.class, getChildType(expressions.get(index), 0));
        assertEquals(Number.class, getChildType(expressions.get(index++), 1));

        // Number e = field.noArguments();
        assertEquals(Number.class, expressions.get(index).getType());
        assertEquals(Number.class, getChildType(expressions.get(index), 0));
        assertEquals(Number.class, getChildType(expressions.get(index++), 1));

        // int f = this.vararg("");
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 1));
        assertEquals(int.class, getChildType(expressions.get(index++), 2));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodAccessibility() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodAccessibility.class, AbstractJavaTypeNode.class,
                                                             "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // SuperClassA a = inheritedA();
        assertEquals(SuperClassA.class, expressions.get(index).getType());
        assertEquals(SuperClassA.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassA.class, getChildType(expressions.get(index++), 1));

        // SuperClassB b = inheritedB();
        assertEquals(SuperClassB.class, expressions.get(index).getType());
        assertEquals(SuperClassB.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassB.class, getChildType(expressions.get(index++), 1));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }


    @Test
    public void testMethodFirstPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodFirstPhase.class, "1.8", AbstractJavaTypeNode.class,
                                                             "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // int a = subtype(10, 'a', "");
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // Exception b = vararg((Object) null);
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));

        // Set<String> set = new HashSet<>();
        assertEquals(HashSet.class, expressions.get(index++).getType());

        // List<String> myList = new ArrayList<>();
        assertEquals(ArrayList.class, expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodMostSpecific() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodMostSpecific.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // String a = moreSpecific((Number) null, (AbstractCollection) null);
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 0));
        assertEquals(String.class, getChildType(expressions.get(index++), 1));

        // Exception b = moreSpecific((Integer) null, (AbstractList) null);
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));

        // int c = moreSpecific((Double) null, (RoleList) null);
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodSecondPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodSecondPhase.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // String a = boxing(10, "");
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 0));
        assertEquals(String.class, getChildType(expressions.get(index++), 1));
        // Exception b = boxing('a', "");
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));
        // int c = boxing(10L, "");
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // String d = unboxing("", (Integer) null);
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 0));
        assertEquals(String.class, getChildType(expressions.get(index++), 1));
        // Exception e = unboxing("", (Character) null);
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));
        // int f = unboxing("", (Byte) null);
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodThirdPhase() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodThirdPhase.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // Exception a = vararg(10, (Number) null, (Number) null);
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));
        // Exception b = vararg(10);
        assertEquals(Exception.class, expressions.get(index).getType());
        assertEquals(Exception.class, getChildType(expressions.get(index), 0));
        assertEquals(Exception.class, getChildType(expressions.get(index++), 1));
        // int c = vararg(10, "", "", "");
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));
        // String d = mostSpecific(10, 10, 10);
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 0));
        assertEquals(String.class, getChildType(expressions.get(index++), 1));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodStaticAccess() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodStaticAccess.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;


        // int a = primitiveStaticMethod();
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(int.class, getChildType(expressions.get(index), 0));
        assertEquals(int.class, getChildType(expressions.get(index++), 1));

        // StaticMembers b = staticInstanceMethod();
        assertEquals(StaticMembers.class, expressions.get(index).getType());
        assertEquals(StaticMembers.class, getChildType(expressions.get(index), 0));
        assertEquals(StaticMembers.class, getChildType(expressions.get(index++), 1));

        // int c = StaticMembers.primitiveStaticMethod();
        assertEquals(int.class, expressions.get(index).getType());
        assertEquals(StaticMembers.class, getChildType(expressions.get(index), 0)); // PrimaryPrefix
        assertEquals(int.class, getChildType(expressions.get(index++), 1)); // PrimarySuffix

        // String c = MethodStaticAccess.Nested.primitiveStaticMethod();
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(MethodStaticAccess.Nested.class, getChildType(expressions.get(index), 0)); // PrimaryPrefix
        assertEquals(String.class, getChildType(expressions.get(index++), 1)); // PrimarySuffix

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodGenericExplicit() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(MethodGenericExplicit.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // String s = this.<String>foo();
        assertEquals(String.class, expressions.get(index).getType());
        assertEquals(String.class, getChildType(expressions.get(index), 1));
        assertEquals(String.class, getChildType(expressions.get(index++), 2));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }


    @Test
    public void testGenericArrays() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(GenericsArrays.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // List<String> var = Arrays.asList(params);
        AbstractJavaTypeNode expression = expressions.get(index++);
        // TODO : Type inference is still incomplete, we fail to detect the return type of the method
        //assertEquals(List.class, expression.getTypeDefinition().getType());
        //assertEquals(String.class, expression.getTypeDefinition().getGenericType(0).getType());

        // List<String> var2 = Arrays.<String>asList(params);
        AbstractJavaTypeNode expression2 = expressions.get(index++);
        assertEquals(List.class, expression2.getTypeDefinition().getType());
        assertEquals(String.class, expression2.getTypeDefinition().getGenericType(0).getType());

        // List<String[]> var3 = Arrays.<String[]>asList(params);
        AbstractJavaTypeNode expression3 = expressions.get(index++);
        assertEquals(List.class, expression3.getTypeDefinition().getType());
        assertEquals(String[].class, expression3.getTypeDefinition().getGenericType(0).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInference() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(GenericMethodsImplicit.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // SuperClassA2 a = bar((SuperClassA) null, (SuperClassAOther) null, null, (SuperClassAOther2) null);
        assertEquals(SuperClassA2.class, expressions.get(index).getType());
        assertEquals(SuperClassA2.class, getChildType(expressions.get(index), 0));
        assertEquals(SuperClassA2.class, getChildType(expressions.get(index++), 1));

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInferenceVarargsZeroArity() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(VarargsZeroArity.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // int var = aMethod();
        assertEquals(int.class, expressions.get(index++).getType());

        //String var2 = aMethod("");
        assertEquals(String.class, expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testMethodTypeInferenceVarargsAsFixedArity() throws JaxenException {
        List<AbstractJavaTypeNode> expressions = selectNodes(VarargsAsFixedArity.class, AbstractJavaTypeNode.class, "//VariableInitializer/Expression/PrimaryExpression");

        int index = 0;

        // int var = aMethod("");
        assertEquals(int.class, expressions.get(index++).getType());

        // String var2 = aMethod();
        assertEquals(String.class, expressions.get(index++).getType());

        // String var3 = aMethod("", "");
        assertEquals(String.class, expressions.get(index++).getType());

        // String var4 = aMethod(new Object[] { null });
        assertEquals(String.class, expressions.get(index++).getType());

        // null literal has null type
        assertNull(expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testJavaTypeDefinitionEquals() {
        JavaTypeDefinition a = forClass(Integer.class);
        JavaTypeDefinition b = forClass(Integer.class);

        // test non-generic types
        assertEquals(a, b);
        assertNotEquals(a, null);

        // test generic arg equality
        b = forClass(List.class, a);
        a = forClass(List.class, a);

        assertEquals(a, b);
        a = forClass(List.class, forClass(String.class));
        assertNotEquals(a, b);
        assertNotEquals(b, a);


        // test raw vs proper, proper vs raw
        a = forClass(JavaTypeDefinitionEquals.class);
        b = forClass(JavaTypeDefinitionEquals.class,
                                        forClass(List.class, a));
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    public void testJavaTypeDefinitionGetSuperTypeSet() {
        JavaTypeDefinition originalTypeDef = forClass(List.class,
                                                                         forClass(Integer.class));
        Set<JavaTypeDefinition> set = originalTypeDef.getSuperTypeSet();

        assertEquals(set.size(), 4);
        assertTrue(set.contains(forClass(Object.class)));
        assertTrue(set.contains(originalTypeDef));
        assertTrue(set.contains(forClass(Collection.class,
                                         forClass(Integer.class))));
        assertTrue(set.contains(forClass(Iterable.class,
                                         forClass(Integer.class))));
    }

    @Test
    public void testJavaTypeDefinitionGetErasedSuperTypeSet() {
        JavaTypeDefinition originalTypeDef = forClass(List.class,
                                                                         forClass(Integer.class));
        Set<Class<?>> set = originalTypeDef.getErasedSuperTypeSet();
        assertEquals(set.size(), 4);
        assertTrue(set.contains(Object.class));
        assertTrue(set.contains(Collection.class));
        assertTrue(set.contains(Iterable.class));
        assertTrue(set.contains(List.class));
    }

    @Test
    public void testMethodInitialBounds() throws NoSuchMethodException {
        JavaTypeDefinition context = forClass(GenericMethodsImplicit.class,
                                                                 forClass(Thread.class));
        List<Variable> variables = new ArrayList<>();
        List<Bound> initialBounds = new ArrayList<>();
        Method method = GenericMethodsImplicit.class.getMethod("foo");
        MethodTypeResolution.produceInitialBounds(method, context, variables, initialBounds);

        assertEquals(initialBounds.size(), 6);
        // A
        assertTrue(initialBounds.contains(new Bound(variables.get(0),
                                                    forClass(Object.class), SUBTYPE)));
        // B
        assertTrue(initialBounds.contains(new Bound(variables.get(1),
                                                    forClass(Number.class), SUBTYPE)));
        assertTrue(initialBounds.contains(new Bound(variables.get(1),
                                                    forClass(Runnable.class), SUBTYPE)));
        // C
        assertTrue(initialBounds.contains(new Bound(variables.get(2), variables.get(3), SUBTYPE)));
        assertTrue(initialBounds.contains(new Bound(variables.get(2),
                                                    forClass(Object.class), SUBTYPE)));
        // D
        assertTrue(initialBounds.contains(new Bound(variables.get(3),
                                                    forClass(Thread.class), SUBTYPE)));
    }

    @Test
    public void testMethodInitialConstraints() throws NoSuchMethodException, JaxenException {
        List<AbstractJavaNode> expressions = selectNodes(GenericMethodsImplicit.class, AbstractJavaNode.class, "//ArgumentList");

        List<Variable> variables = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            variables.add(new Variable());
        }
        Method method = GenericMethodsImplicit.class.getMethod("bar", Object.class, Object.class,
                                                               Integer.class, Object.class);
        ASTArgumentList argList = (ASTArgumentList) expressions.get(0);

        List<Constraint> constraints = MethodTypeResolution.produceInitialConstraints(method, argList, variables);

        assertEquals(constraints.size(), 3);
        // A
        assertTrue(constraints.contains(new Constraint(forClass(SuperClassA.class),
                                                       variables.get(0), LOOSE_INVOCATION)));
        assertTrue(constraints.contains(new Constraint(forClass(SuperClassAOther.class),
                                                       variables.get(0),
                                                       LOOSE_INVOCATION)));
        // B
        assertTrue(constraints.contains(new Constraint(forClass(SuperClassAOther2.class),
                                                       variables.get(1),
                                                       LOOSE_INVOCATION)));
    }

    @Test
    public void testMethodParameterization() throws NoSuchMethodException {
        ASTArgumentList argList = selectNodes(GenericMethodsImplicit.class, ASTArgumentList.class).get(0);

        JavaTypeDefinition context = forClass(GenericMethodsImplicit.class, forClass(Thread.class));
        Method method = GenericMethodsImplicit.class.getMethod("bar", Object.class, Object.class,
                                                               Integer.class, Object.class);

        MethodType inferedMethod = MethodTypeResolution.parameterizeInvocation(context, method, argList);

        assertEquals(inferedMethod.getParameterTypes().get(0),
                     forClass(SuperClassA2.class));
        assertEquals(inferedMethod.getParameterTypes().get(1),
                     forClass(SuperClassA2.class));
        assertEquals(inferedMethod.getParameterTypes().get(2),
                     forClass(Integer.class));
        assertEquals(inferedMethod.getParameterTypes().get(3),
                     forClass(SuperClassAOther2.class));
    }


    @Test
    public void testNestedAllocationExpressions() {
        ASTCompilationUnit acu = java5.parseClass(NestedAllocationExpressions.class);
        List<ASTAllocationExpression> allocs = acu.findDescendantsOfType(ASTAllocationExpression.class);

        assertFalse(allocs.get(0).isAnonymousClass());
        assertEquals(Thread.class, allocs.get(0).getType());

        assertTrue(allocs.get(1).isAnonymousClass());
        // FUTURE 1.8 use Class.getTypeName() instead of toString
        assertTrue(allocs.get(1).getType().toString().endsWith("NestedAllocationExpressions$1"));
    }

    @Test
    public void testAnnotatedTypeParams() {
        java8.parse("public class Foo { public static <T extends @NonNull Enum<?>> T getEnum() { return null; } }");
    }

    @Test
    public void testMethodOverrides() throws Exception {
        java8.parseClass(SubTypeUsage.class);
    }

    @Test
    public void testMethodWildcardParam() throws Exception {
        java8.parseClass(MethodGenericParam.class);
    }

    @Test
    public void testAbstractMethodReturnType() throws Exception {
        java8.parseClass(AbstractReturnTypeUseCase.class);
    }

    @Test
    public void testMethodOverloaded() throws Exception {
        java8.parseClass(OverloadedMethodsUsage.class);
    }

    @Test
    public void testVarArgsMethodUseCase() throws Exception {
        java8.parseClass(VarArgsMethodUseCase.class);
    }

    @Test
    public void testLocalGenericClass() throws Exception {
        java9.parseClass(LocalGenericClass.class);
    }

    @Test
    public void testMethodCallExpressionTypes() throws Exception {
        ASTCompilationUnit cu = java11.parseClass(MethodCallExpressionTypes.class);
        ASTPrimaryExpression expr = cu.getFirstDescendantOfType(ASTPrimaryExpression.class);
        assertEquals(forClass(String.class), expr.getTypeDefinition());
        assertEquals(forClass(Objects.class), expr.getFirstChildOfType(ASTPrimaryPrefix.class).getTypeDefinition());
    }

    private JavaTypeDefinition getChildTypeDef(Node node, int childIndex) {
        return ((TypeNode) node.getChild(childIndex)).getTypeDefinition();
    }

    private Class<?> getChildType(Node node, int childIndex) {
        return ((TypeNode) node.getChild(childIndex)).getType();
    }


    private <T> List<T> selectNodes(String source, Class<T> resultType, String xpath) throws JaxenException {
        return selectNodes(source, "1.5", resultType, xpath);
    }

    // This is the master overload, others just default the parameters
    private <T> List<T> selectNodes(String source, String version, Class<T> resultType, String xpath) throws JaxenException {
        ASTCompilationUnit acu = java4.parse(source, version);
        return convertList(acu.findChildNodesWithXPath(xpath), resultType);
    }


    private <T> List<T> selectNodes(Class<?> source, Class<T> resultType) {
        return java5.parseClass(source).findDescendantsOfType(resultType);
    }

    private <T> List<T> selectNodes(Class<?> source, Class<T> resultType, String xpath) throws JaxenException {
        return selectNodes(source, "1.5", resultType, xpath);
    }


    private <T> List<T> selectNodes(Class<?> source, String version, Class<T> resultType, String xpath) throws JaxenException {
        ASTCompilationUnit acu = java4.parseClass(source, version);
        return convertList(acu.findChildNodesWithXPath(xpath), resultType);
    }


    private void assertChildTypeArgsEqualTo(Node node, int childIndex, Class<?>... classes) {
        JavaTypeDefinition typeDef = ((TypeNode) node.getChild(childIndex)).getTypeDefinition();

        for (int index = 0; index < classes.length; ++index) {
            assertSame(classes[index], typeDef.getGenericType(index).getType());
        }
    }


    private static <T> List<T> convertList(List<Node> nodes, Class<T> target) {
        List<T> converted = new ArrayList<>();
        for (Node n : nodes) {
            converted.add(target.cast(n));
        }
        return converted;
    }
}
