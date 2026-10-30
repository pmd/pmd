/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

/**
 * Base class for tree dump tests for Kotlin. It uses the simple node printer, which prints the node type and its image.
 */
public class BaseKotlinTreeDumpTest extends BaseTreeDumpTest {

    private static class KotlinAttributePrinter extends RelevantAttributePrinter {
        @Override
        protected boolean ignoreAttribute(@NotNull Node node, @NotNull Attribute attribute) {
            // ignore empty collections to remove noise from tree dumps. E.g.
            // "@AnnotationFqNames = ()" is not output.
            Object value = attribute.getValue();
            if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                return true;
            }
            return super.ignoreAttribute(node, attribute);
        }
    }

    public BaseKotlinTreeDumpTest() {
        super(new KotlinAttributePrinter(), ".kt");
    }

    @NonNull
    @Override
    public KotlinParsingHelper getParser() {
        return KotlinParsingHelper.DEFAULT.withResourceContext(getClass(), "testdata");
    }
}
