/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.MatchAlgorithm;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MatchAlgorithmTest extends TestCase {

    public static final String LINE_1 = "public class Foo { ";
    public static final String LINE_2 = " public void bar() {";
    public static final String LINE_3 = "  System.out.println(\"hello\");";
    public static final String LINE_4 = "  System.out.println(\"hello\");";
    public static final String LINE_5 = " }";
    public static final String LINE_6 = "}";

    public static String getSampleCode() {
        return
            LINE_1 + PMD.EOL +
            LINE_2 + PMD.EOL +
            LINE_3 + PMD.EOL +
            LINE_4 + PMD.EOL +
            LINE_5 + PMD.EOL +
            LINE_6;
    }

    public void testSimple() throws Throwable {
        String code = getSampleCode();
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("Foo.java");
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens, new StringReader(code));
        assertEquals(29, tokens.size());
        Map codeMap = new HashMap();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens);
        matchAlgorithm.findMatches(5);

        Iterator matches = matchAlgorithm.matches();
        Match match = (Match)matches.next();
        assertFalse(matches.hasNext());

        Iterator marks = match.iterator();
        Mark mark1 = (Mark)marks.next();
        Mark mark2 = (Mark)marks.next();
        assertTrue(!marks.hasNext());

        assertEquals(2, mark2.getBeginLine());
        assertEquals(3, mark1.getBeginLine());
        assertTrue("Foo.java" == mark1.getTokenSrcID() && "Foo.java" == mark2.getTokenSrcID());
        assertEquals(LINE_3, match.getSourceCodeSlice());
    }
}
