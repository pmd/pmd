/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

public abstract class EcmascriptParserTestBase {
    public ASTAstRoot parse(String code) {
        EcmascriptParser parser = new EcmascriptParser(new EcmascriptParserOptions());
        Reader sourceCode = new StringReader(code);
        return (ASTAstRoot)parser.parse(sourceCode);
    }

    public ASTAstRoot parse18(String code) {
        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        parserOptions.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_1_8);
        EcmascriptParser parser = new EcmascriptParser(parserOptions);
        Reader sourceCode = new StringReader(code);
        return (ASTAstRoot)parser.parse(sourceCode);
    }

    public String dump(EcmascriptNode<?> node) {
        DumpFacade dumpFacade = new DumpFacade();
        StringWriter writer = new StringWriter();
        dumpFacade.initializeWith(writer, "", true, node);
        dumpFacade.visit(node, "");
        return writer.toString();
    }
}
