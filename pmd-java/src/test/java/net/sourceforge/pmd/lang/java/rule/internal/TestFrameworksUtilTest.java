/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;

class TestFrameworksUtilTest {

    protected final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Test
    void testIsProbableAssertCallWithoutExtraMethodNames() {
        ASTCompilationUnit root = java.parse("class A { { assertThat(1); } }");
        ASTMethodCall m = root.descendants(ASTMethodCall.class).toList().get(0);
        assertThat(TestFrameworksUtil.isProbableAssertCall(m)).isTrue();
    }

}
