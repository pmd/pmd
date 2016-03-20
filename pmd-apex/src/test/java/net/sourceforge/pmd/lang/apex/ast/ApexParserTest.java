package net.sourceforge.pmd.lang.apex.ast;

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexParser;

import org.junit.Test;

public class ApexParserTest {

    private String code1 = "public class HelloWorld { public void foo() {} private static int bar() { return 1; } }";
    private String code2 = "public class SimpleClass {\n" +
                    "    public void methodWithManyParams(String a, String b, String c, String d, String e, String f, String g) {\n" +
                    "        \n" +
                    "    }\n" +
                    "}";

    @Test
    public void testParse() {
        ASTUserClass rootNode = parse(code1);

        List<ASTMethod> methods = rootNode.findDescendantsOfType(ASTMethod.class);
        assertEquals(2, methods.size());
    }
    
    private ASTUserClass parse(String code) {
        ApexParser parser = new ApexParser(new ApexParserOptions());
        Reader reader = new StringReader(code);
        return (ASTUserClass) parser.parse(reader);
    }
}
