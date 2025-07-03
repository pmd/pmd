/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java25PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java25p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("25-preview")
                    .withResourceContext(Java25PreviewTreeDumpTest.class, "jdkversiontests/java25p/");
    private final JavaParsingHelper java25 = java25p.withDefaultVersion("25");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java25p;
    }

}
