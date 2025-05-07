/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.jupiter.api.Test;

class ApexCompilerSoqlTest extends ApexParserTestBase {

    private static final String CODE = """
        public class Foo {
           public List<SObject> test1() {
               return Database.query('Select Id from Account LIMIT 100');
           }
        }
        """;

    @Test
    void testSoqlCompilation() {
        apex.parse(CODE);
    }
}
