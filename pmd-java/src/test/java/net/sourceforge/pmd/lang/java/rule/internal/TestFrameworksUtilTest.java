package net.sourceforge.pmd.lang.java.rule.internal;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.assertj.core.api.Assertions.assertThat;

class TestFrameworksUtilTest {

    protected final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Test
    void testIsProbableAssertCallWithoutExtraMethodNames() {
        ASTCompilationUnit root = java.parse("class A { { assertThat(1); } }");
        ASTMethodCall m = root.descendants(ASTMethodCall.class).toList().get(0);
        assertThat(TestFrameworksUtil.isProbableAssertCall(m, Collections.emptyList())).isTrue();
    }

    @Test
    void testIsProbableAssertCallWithExtraMethodNames() {
        ASTCompilationUnit root = java.parse("class A { { expectTrue(1); } }");
        ASTMethodCall m = root.descendants(ASTMethodCall.class).toList().get(0);
        assertThat(TestFrameworksUtil.isProbableAssertCall(m, listOf("expectTrue"))).isTrue();
    }
}
