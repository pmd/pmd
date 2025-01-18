/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java24PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java24p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("24-preview")
                    .withResourceContext(Java24PreviewTreeDumpTest.class, "jdkversiontests/java24p/");
    private final JavaParsingHelper java24 = java24p.withDefaultVersion("24");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java24p;
    }

}
