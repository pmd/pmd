/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java8TreeDumpTest extends BaseJavaTreeDumpTest {

    private final JavaParsingHelper java8 = JavaParsingHelper.DEFAULT
        .withDefaultVersion("8")
        .withResourceContext(Java8TreeDumpTest.class, "jdkversiontests/java8");

    @Test
    void unnamedVariablesAreAllowedWithJava8() {
        doTest("UnnamedVariable");
    }

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return java8;
    }
}
