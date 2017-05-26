/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexParserTestHelpers {
    private ApexParserTestHelpers() { }

    public static ApexNode<Compilation> parse(String code) {
        ApexParser parser = new ApexParser(new ApexParserOptions());
        Reader reader = new StringReader(code);
        return parser.parse(reader);
    }

    public static void dumpNode(Node node) {
        DumpFacade facade = new DumpFacade();
        StringWriter writer = new StringWriter();
        facade.initializeWith(writer, "", true, (ApexNode<?>) node);
        facade.visit((ApexNode<?>) node, "");
        System.out.println(writer.toString());
    }
}
