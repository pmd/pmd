/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexCompilerSoqlTest {

    private static final String CODE = "public class Foo {\n"
            + "   public List<SObject> test1() {\n"
            + "       return Database.query(\'Select Id from Account LIMIT 100\');\n"
            + "   }\n"
            + "}\n";

    @Test
    public void testSoqlCompilation() {
        ApexParser parser = new ApexParser(new ApexParserOptions());
        ApexNode<Compilation> cu = parser.parse(new StringReader(CODE));
        Assert.assertNotNull(cu);
    }
}
