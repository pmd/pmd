/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers.parse;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexCompilerTest {

    @Test(expected = ParseException.class)
    public void compileShouldFail() {
        ApexNode<Compilation> node = parse("public class Foo { private String myField = \"a\"; }");
    }
}
