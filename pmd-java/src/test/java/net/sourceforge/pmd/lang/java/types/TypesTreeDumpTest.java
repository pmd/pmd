/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

/**
 *
 */
public class TypesTreeDumpTest extends BaseTreeDumpTest {

    public TypesTreeDumpTest() {
        super(new JavaTypeAttrPrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return JavaParsingHelper.WITH_PROCESSING.withResourceContext(getClass());
    }

    @Test
    public void testBigFile() {
        doTest("IteratorBasedNStream");
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
        }

        @Override
        protected boolean ignoreAttribute(@NonNull Node node, @NonNull Attribute attribute) {
            return true;
        }
    }
}
