/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jaxen.JaxenException;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.typeresolution.testdata.SuperClass;
import net.sourceforge.pmd.typeresolution.testdata.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.ThisExpression;
import net.sourceforge.pmd.typeresolution.testdata.UsesJavaStreams;
import net.sourceforge.pmd.typeresolution.testdata.UsesRepeatableAnnotations;



public class ClassTypeResolverJava8Test {

    @Test
    public void interfaceMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesJavaStreams.class);
    }

    @Test
    public void repeatableAnnotationsMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesRepeatableAnnotations.class);
    }

    @Test
    public void testThisExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(ThisExpression.class);

        List<ASTPrimaryExpression> expressions = convertList(
                acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression"),
                ASTPrimaryExpression.class);
        List<ASTPrimaryPrefix> prefixes = convertList(
                acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"),
                ASTPrimaryPrefix.class);

        int index = 0;

        assertEquals(ThisExpression.class, expressions.get(index).getType());
        assertEquals(ThisExpression.class, prefixes.get(index++).getType());
        assertEquals(ThisExpression.PrimaryThisInterface.class, expressions.get(index).getType());
        assertEquals(ThisExpression.PrimaryThisInterface.class, prefixes.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
        assertEquals("All expressions not tested", index, prefixes.size());
    }

    @Test
    public void testSuperExpression() throws JaxenException {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(SuperExpression.class);

        List<AbstractJavaTypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"),
                AbstractJavaTypeNode.class);

        int index = 0;

        assertEquals(SuperClass.class, expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }

    private static <T> List<T> convertList(List<Node> nodes, Class<T> target) {
        List<T> converted = new ArrayList<>();
        for (Node n : nodes) {
            converted.add(target.cast(n));
        }
        return converted;
    }

    private ASTCompilationUnit parseAndTypeResolveForClass18(Class<?> clazz) {
        return parseAndTypeResolveForClass(clazz, "1.8");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the regress folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndTypeResolveForClass(Class<?> clazz, String version) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverJava8Test.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new InputStreamReader(is));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getQualifiedNameResolutionFacade(ClassTypeResolverJava8Test.class.getClassLoader()).start(acu);
        languageVersionHandler.getTypeResolutionFacade(ClassTypeResolverJava8Test.class.getClassLoader()).start(acu);
        return acu;
    }
}
