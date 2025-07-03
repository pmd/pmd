/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java25TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java25 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("25")
                    .withResourceContext(Java25TreeDumpTest.class, "jdkversiontests/java25/");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java25;
    }

}
