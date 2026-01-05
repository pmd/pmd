/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java26PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java26p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("26-preview")
                    .withResourceContext(Java26PreviewTreeDumpTest.class, "jdkversiontests/java26p/");
    private final JavaParsingHelper java26 = java26p.withDefaultVersion("26");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java26p;
    }

}
