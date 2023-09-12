/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.typescript.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.ecmascript.cpd.EcmascriptTokenizer;
import net.sourceforge.pmd.lang.typescript.TsLanguageModule;

class TypeScriptTokenizerTest extends CpdTextComparisonTest {

    TypeScriptTokenizerTest() {
        super(TsLanguageModule.getInstance(), ".ts");
    }

    @Test
    void greeterTest() {
        doTest("greeter");
    }

    @Test
    void apiSampleWatchTest() {
        doTest("APISample_Watch");
    }
    
    @Test
    void testDecorators() throws IOException {
        Tokenizer t = new EcmascriptTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("// A simple decorator" + PMD.EOL
                + "@annotation" + PMD.EOL
                + "class MyClass { }" + PMD.EOL
                + "" + PMD.EOL
                + "function annotation(target) {" + PMD.EOL
                + "   // Add a property on target" + PMD.EOL
                + "   target.annotated = true;" + PMD.EOL
                + "}" + PMD.EOL
        ));
        final Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals("@annotation", tokens.getTokens().get(0).toString());
    }
}
