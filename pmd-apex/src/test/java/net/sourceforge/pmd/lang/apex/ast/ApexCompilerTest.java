/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;

class ApexCompilerTest extends ApexParserTestBase {

    @Test
    void compileShouldFail() {
        assertThrows(ParseException.class, () -> apex.parse("public class Foo { private String myField = \"a\"; }"));
    }
}
