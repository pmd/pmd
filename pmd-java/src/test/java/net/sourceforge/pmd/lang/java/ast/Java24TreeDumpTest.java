/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java24TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java24 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("24")
                    .withResourceContext(Java24TreeDumpTest.class, "jdkversiontests/java24/");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java24;
    }

}
