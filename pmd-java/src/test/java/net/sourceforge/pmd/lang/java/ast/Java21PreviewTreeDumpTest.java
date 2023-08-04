/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java21PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java21p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("21-preview")
                    .withResourceContext(Java21PreviewTreeDumpTest.class, "jdkversiontests/java21p/");
    private final JavaParsingHelper java21 = java21p.withDefaultVersion("21");

    Java21PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java21p;
    }

}
