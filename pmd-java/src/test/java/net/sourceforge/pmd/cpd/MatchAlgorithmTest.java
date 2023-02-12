/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

class MatchAlgorithmTest {

    private static final String LINE_1 = "public class Foo { ";
    private static final String LINE_2 = " public void bar() {";
    private static final String LINE_3 = "  System.out.println(\"hello\");";
    private static final String LINE_4 = "  System.out.println(\"hello\");";
    private static final String LINE_5 = "  int i = 5";
    private static final String LINE_6 = "  System.out.print(\"hello\");";
    private static final String LINE_7 = " }";
    private static final String LINE_8 = "}";

    private static String getSampleCode() {
        return LINE_1 + "\n" + LINE_2 + "\n" + LINE_3 + "\n" + LINE_4 + "\n" + LINE_5 + "\n" + LINE_6
                + "\n" + LINE_7 + "\n" + LINE_8;
    }

    @Test
    void testSimple() throws IOException {
        Language java = JavaLanguageModule.getInstance();
        Tokenizer tokenizer = java.createCpdTokenizer(java.newPropertyBundle());
        String fileName = "Foo.java";
        TextFile textFile = TextFile.forCharSeq(getSampleCode(), fileName, java.getDefaultVersion());
        SourceManager sourceManager = new SourceManager(listOf(textFile));
        Tokens tokens = new Tokens();
        TextDocument sourceCode = sourceManager.get(textFile);
        Tokenizer.tokenize(tokenizer, sourceCode, tokens);
        assertEquals(41, tokens.size());

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        assertFalse(matches.hasNext());

        Iterator<Mark> marks = match.iterator();
        Mark mark1 = marks.next();
        Mark mark2 = marks.next();
        assertFalse(marks.hasNext());

        assertEquals(3, mark1.getBeginLine());
        assertEquals(fileName, mark1.getFilename());
        assertEquals(Chars.wrap(LINE_3), sourceManager.getSlice(mark1));

        assertEquals(4, mark2.getBeginLine());
        assertEquals(fileName, mark2.getFilename());
        assertEquals(Chars.wrap(LINE_4), sourceManager.getSlice(mark2));
    }

    @Test
    void testIgnore() throws IOException {
        Language java = JavaLanguageModule.getInstance();
        LanguagePropertyBundle bundle = java.newPropertyBundle();
        bundle.setProperty(Tokenizer.CPD_ANONYMIZE_IDENTIFIERS, true);
        bundle.setProperty(Tokenizer.CPD_ANONYMiZE_LITERALS, true);
        Tokenizer tokenizer = java.createCpdTokenizer(bundle);
        TextDocument sourceCode = TextDocument.readOnlyString(getSampleCode(), "Foo.java", java.getDefaultVersion());
        Tokens tokens = new Tokens();
        Tokenizer.tokenize(tokenizer, sourceCode, tokens);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        assertFalse(matches.hasNext());

        Iterator<Mark> marks = match.iterator();
        marks.next();
        marks.next();
        marks.next();
        assertFalse(marks.hasNext());
    }
}
