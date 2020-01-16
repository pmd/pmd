/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Test;

public class ApexCompilerSoqlTest extends ApexParserTestBase {

    private static final String CODE = "public class Foo {\n"
        + "   public List<SObject> test1() {\n"
        + "       return Database.query(\'Select Id from Account LIMIT 100\');\n"
        + "   }\n"
        + "}\n";

    @Test
    public void testSoqlCompilation() {
        apex.parse(CODE);
    }
}
