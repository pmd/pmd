/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

/**
 *
 */
class TypesTreeDumpTest extends BaseTreeDumpTest {

    TypesTreeDumpTest() {
        super(new JavaTypeAttrPrinter(), ".java");
    }

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return JavaParsingHelper.DEFAULT.withResourceContext(getClass(), "dumptests");
    }

    @Test
    void testIteratorUtilCopy() {
        doTest("IteratorUtilCopy");
    }

    @Test
    void testSwitchExpressionWithPatterns() {
        doTest("SwitchExpressionWithPatterns");
    }

    @Test
    void testUnnamedPatterns() {
        doTest("UnnamedPatterns");
    }

    @Test
    void testNestedLambdasAndMethodCalls() {
        doTest("NestedLambdasAndMethodCalls");
    }

    @Test
    void testUnresolvedThings() {
        doTest("UnresolvedThings");
    }

    @Override
    protected @NonNull String normalize(@NonNull String str) {
        return super.normalize(str)
                    // capture IDs are unstable from run to run
                    .replaceAll("capture#-?\\d+", "capture#...");
    }

    /**
     * Only prints the type of type nodes
     */
    private static class JavaTypeAttrPrinter extends RelevantAttributePrinter {

        @Override
        protected void fillAttributes(@NonNull Node node, @NonNull List<AttributeInfo> result) {
            if (node instanceof TypeNode) {
                result.add(new AttributeInfo("TypeMirror", ((TypeNode) node).getTypeMirror().toString()));
            }

            if (node instanceof InvocationNode) {
                InvocationNode invoc = (InvocationNode) node;
                result.add(new AttributeInfo("MethodName", invoc.getMethodName()));
                result.add(new AttributeInfo("VarargsCall", invoc.getOverloadSelectionInfo().isVarargsCall()));
                result.add(new AttributeInfo("Unchecked", invoc.getOverloadSelectionInfo().needsUncheckedConversion()));
                result.add(new AttributeInfo("Failed", invoc.getOverloadSelectionInfo().isFailed()));
                result.add(new AttributeInfo("Function", TypePrettyPrint.prettyPrint(invoc.getMethodType())));
            }
            if (node instanceof ASTNamedReferenceExpr) {
                result.add(new AttributeInfo("Name", ((ASTNamedReferenceExpr) node).getName()));
            }
            if (node instanceof ASTVariableId) {
                result.add(new AttributeInfo("Name", ((ASTVariableId) node).getName()));
            }
            if (node instanceof ASTMethodDeclaration) {
                result.add(new AttributeInfo("Name", ((ASTMethodDeclaration) node).getName()));
            }
        }

        @Override
        protected boolean ignoreAttribute(@NonNull Node node, @NonNull Attribute attribute) {
            return true;
        }
    }
}
