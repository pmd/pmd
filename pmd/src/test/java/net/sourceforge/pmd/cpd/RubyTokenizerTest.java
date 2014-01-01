/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

import org.junit.Before;
import org.junit.Test;



public class RubyTokenizerTest extends AbstractTokenizerTest {

	@Before
	@Override
	public void buildTokenizer() {
		this.tokenizer = new RubyTokenizer();
		this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), "server.rb"));
	}

	@Override
	public String getSampleCode() {
		 return "require \"socket\"" + PMD.EOL +
		 "" + PMD.EOL +
		 "gs = TCPServer.open(0)" + PMD.EOL +
		 "addr = gs.addr" + PMD.EOL +
		 "addr.shift" + PMD.EOL +
		 "" + PMD.EOL +
		 "while true" + PMD.EOL +
		 "  ns = gs.accept" + PMD.EOL +
		 "  print(ns, \" is accepted\")" + PMD.EOL +
		 "  Thread.start do" + PMD.EOL +
		 "    s = ns                      # save to dynamic variable" + PMD.EOL +
		 "    while s.gets" + PMD.EOL +
		 "      s.write($_)" + PMD.EOL +
		 "    end" + PMD.EOL +
		 "    print(s, \" is " + PMD.EOL +
		 "               gone" + PMD.EOL +
		 "                       and" + PMD.EOL +
		 "                               dead\")" + PMD.EOL +
		 "    s.close" + PMD.EOL +
		 "  end" + PMD.EOL +
		 "end" + PMD.EOL;
	 }

	@Test
	public void tokenizeTest() throws IOException {
		this.expectedTokenCount = 30;
		super.tokenizeTest();
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(RubyTokenizerTest.class);
    }
}
