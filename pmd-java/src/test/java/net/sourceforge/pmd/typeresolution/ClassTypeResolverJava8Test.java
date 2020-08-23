/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static net.sourceforge.pmd.lang.java.JavaParsingHelper.convertList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.typeresolution.testdata.java8.SuperClass;
import net.sourceforge.pmd.typeresolution.testdata.java8.SuperExpression;
import net.sourceforge.pmd.typeresolution.testdata.java8.ThisExpression;



@Ignore
public class ClassTypeResolverJava8Test {
    private final JavaParsingHelper java8 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("8")
                                             .withResourceContext(ClassTypeResolverJava8Test.class);

    @Test
    public void testThisExpression() {
        ASTCompilationUnit acu = java8.parseClass(ThisExpression.class);

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
    public void testSuperExpression() {
        ASTCompilationUnit acu = java8.parseClass(SuperExpression.class);

        List<TypeNode> expressions = convertList(
                acu.findChildNodesWithXPath("//VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix"),
                TypeNode.class);

        int index = 0;

        assertEquals(SuperClass.class, expressions.get(index++).getType());

        // Make sure we got them all
        assertEquals("All expressions not tested", index, expressions.size());
    }
}
