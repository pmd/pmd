/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java26TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java26 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("26")
                    .withResourceContext(Java26TreeDumpTest.class, "jdkversiontests/java26/");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java26;
    }

    // Java 26 didn't finalize any new features, thus there are no tests
}
