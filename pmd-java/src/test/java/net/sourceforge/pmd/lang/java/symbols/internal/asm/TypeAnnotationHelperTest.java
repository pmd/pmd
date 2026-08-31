/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Regression tests for TypeAnnotationHelper.
 */
class TypeAnnotationHelperTest {

    /**
     * Regression test for <a href="https://github.com/pmd/pmd/issues/5041">#5041</a>.
     *
     * <p>ASM's {@code TypePath.getStep(i)} does pure array arithmetic with no bounds
     * check. When the loop consumed the last valid INNER_TYPE step and called
     * {@code getStep} one past the end, it read the high byte of the following
     * {@code type_index} field. That byte equals 1 (= INNER_TYPE) when the CP index
     * is in [256, 511], over-incrementing {@code selectionDepth} and crashing on
     * {@code enclosingTypes.get(-1)}.
     *
     * <p>{@code TestClass} has 300 dummy fields to push the Ann descriptor past CP
     * index 255, reliably reproducing the crash.
     */
    @Test
    void testInnerTypePathBoundsCheck() {
        TypeSystem ts = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());

        String pkg = "net.sourceforge.pmd.lang.java.symbols.testdata.innerannot.";
        JClassType type = (JClassType) ts.declaration(ts.getClassSymbol(pkg + "TestClass"));

        JMethodSig method = type.streamMethods(m -> m.nameEquals("testMethod"))
                                .findFirst().orElseThrow(AssertionError::new);
        JClassType returnType = (JClassType) method.getReturnType();

        assertThat(returnType.getTypeAnnotations(), hasSize(1));
        assertThat(returnType.getEnclosingType().getTypeAnnotations(), empty());
    }
}
