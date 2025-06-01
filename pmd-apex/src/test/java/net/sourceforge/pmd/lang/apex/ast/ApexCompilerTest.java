/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import net.sourceforge.pmd.lang.ast.ParseException;
import org.junit.jupiter.api.Test;

class ApexCompilerTest extends ApexParserTestBase {

    @Test
    void compileShouldFail() {
        assertThrows(ParseException.class, () -> apex.parse("public class Foo { private String myField = \"a\"; }"));
    }
}
