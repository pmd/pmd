/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java27TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java27 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("27")
                    .withResourceContext(Java27TreeDumpTest.class, "jdkversiontests/java27/");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java27;
    }

    // Java 27 didn't finalize any new features, thus there are no tests
}
