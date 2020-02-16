/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;

public class ApexCompilerTest extends ApexParserTestBase {

    @Test(expected = ParseException.class)
    public void compileShouldFail() {
        apex.parse("public class Foo { private String myField = \"a\"; }");
    }
}
