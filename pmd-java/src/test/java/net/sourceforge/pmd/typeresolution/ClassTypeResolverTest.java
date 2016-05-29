/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jaxen.JaxenException;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
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
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.ArrayListFound;
import net.sourceforge.pmd.typeresolution.testdata.DefaultJavaLangImport;
import net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass;
import net.sourceforge.pmd.typeresolution.testdata.ExtraTopLevelClass;
import net.sourceforge.pmd.typeresolution.testdata.InnerClass;
import net.sourceforge.pmd.typeresolution.testdata.Literals;
import net.sourceforge.pmd.typeresolution.testdata.Operators;
import net.sourceforge.pmd.typeresolution.testdata.Promotion;


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
        assertEquals(ArrayListFound.class, acu.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
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
        assertEquals("net.sourceforge.pmd.typeresolution.testdata.EnumWithAnonymousInnerClass$1",
                inner.getName());
    }

    @Test
    public void testExtraTopLevelClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(ExtraTopLevelClass.class);
        Class<?> theExtraTopLevelClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.TheExtraTopLevelClass");
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
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
        assertEquals(InnerClass.class, outerClassDeclaration.getType());
        // Inner class
        assertEquals(theInnerClass,
                outerClassDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class).getType());
        // Method parameter as inner class
        ASTFormalParameter formalParameter = typeDeclaration.getFirstDescendantOfType(ASTFormalParameter.class);
        assertEquals(theInnerClass, formalParameter.getTypeNode().getType());
    }

    /**
     * If we don't have the auxclasspath, we might not find the inner class. In that case,
     * we'll need to search by name for a match.
     * @throws Exception
     */
    @Test
    public void testInnerClassNotCompiled() throws Exception {
        LanguageVersion language = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion();
        LanguageVersionHandler languageHandler = language.getLanguageVersionHandler();
        Parser parser = languageHandler.getParser(languageHandler.getDefaultParserOptions());
        Node acu = parser.parse("test", new StringReader("public class TestInnerClass {\n" + 
                "    public void foo() {\n" + 
                "        Statement statement = new Statement();\n" + 
                "    }\n" + 
                "    static class Statement {\n" + 
                "    }\n" + 
                "}"));
        ASTClassOrInterfaceType statement = acu.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        Assert.assertTrue(statement.isReferenceToClassSameCompilationUnit());
    }

    @Test
    public void testAnonymousInnerClass() throws ClassNotFoundException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(AnonymousInnerClass.class);
        Class<?> theAnonymousInnerClass = Class.forName("net.sourceforge.pmd.typeresolution.testdata.AnonymousInnerClass$1");
        // Outer class
        ASTTypeDeclaration typeDeclaration = acu.getFirstDescendantOfType(ASTTypeDeclaration.class);
        assertEquals(AnonymousInnerClass.class, typeDeclaration.getType());
        ASTClassOrInterfaceDeclaration outerClassDeclaration = typeDeclaration.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
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
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericPromotion']]//Expression[UnaryExpression]"), ASTExpression.class);
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
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryNumericPromotion']]//Expression[AdditiveExpression]"), ASTExpression.class);
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
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryStringPromotion']]//Expression"), ASTExpression.class);
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
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryLogicalOperators']]//Expression"), ASTExpression.class);
        int index = 0;

        assertEquals(Boolean.TYPE, expressions.get(index++).getType());
        assertEquals(Boolean.TYPE, expressions.get(index++).getType());

        // Make sure we got them all.
        assertEquals("All expressions not tested", index, expressions.size());
    }

    @Test
    public void testBinaryLogicalOperators() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass15(Operators.class);
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryLogicalOperators']]//Expression"), ASTExpression.class);
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
        expressions.addAll(convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//Expression"), TypeNode.class));
        expressions.addAll(convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//PostfixExpression"), TypeNode.class));
        expressions.addAll(convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//PreIncrementExpression"), TypeNode.class));
        expressions.addAll(convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'unaryNumericOperators']]//PreDecrementExpression"), TypeNode.class));

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
        List<ASTExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'binaryNumericOperators']]//Expression"), ASTExpression.class);
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
        List<ASTStatementExpression> expressions = convertList(acu.findChildNodesWithXPath("//Block[preceding-sibling::MethodDeclarator[@Image = 'assignmentOperators']]//StatementExpression"), ASTStatementExpression.class);
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

    private ASTCompilationUnit parseAndTypeResolveForClass15(Class<?> clazz) {
        return parseAndTypeResolveForClass(clazz, "1.5");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you _must_ have the regress folder in
    // the classpath.  Normally the IDE doesn't put source directories themselves directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndTypeResolveForClass(Class<?> clazz, String version) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverTest.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new InputStreamReader(is));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(ClassTypeResolverTest.class.getClassLoader()).start(acu);
        return acu;
    }
}
