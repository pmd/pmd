/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class JavaTokensTokenizerTest extends CpdTextComparisonTest {

    public JavaTokensTokenizerTest() {
        super(".java");
    }

    @Override
    public Tokenizer newTokenizer(@NotNull Properties properties) {
        JavaTokenizer javaTokenizer = new JavaTokenizer();
        javaTokenizer.setProperties(properties);
        return javaTokenizer;
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/java/cpd/testdata";
    }

    @Test
    public void testCommentsIgnored() {
        doTest("simpleClassWithComments");
    }

    @Test
    public void testSlice() {
        SourceCode sourceCode = sourceCodeOf("public class Foo {\n" +
                                                 "public void bar() {}\n" +
                                                 "public void buz() {}\n" +
                                                 "}");

        assertEquals("public class Foo {\n" +
                         "public void bar() {}", sourceCode.getSlice(1, 2));
    }


    @Test
    public void testDiscardedElements() {
        doTest("discardedElements", "_ignore_annots", ignoreAnnotations(true));
    }

    @Test
    public void testDiscardedElementsExceptAnnots() {
        doTest("discardedElements", "_no_ignore_annots", ignoreAnnotations(false));
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    public void testIgnoreBetweenSpecialAnnotation() {
        doTest("ignoreSpecialAnnotations");
    }

    @Test
    public void testIgnoreBetweenSpecialAnnotationAndIgnoreAnnotations() {
        doTest("ignoreSpecialAnnotations", "_ignore_annots", ignoreAnnotations(true));
    }

    @Test
    public void testIgnoreIdentifiersDontAffectConstructors() {
        doTest("ignoreIdentsPreservesCtor", "", ignoreIdents(true));
    }

    @Test
    public void testIgnoreIdentifiersHandlesEnums() {
        doTest("ignoreIdentsPreservesEnum", "", ignoreIdents(true));
    }

    @Test
    public void testIgnoreIdentifiersWithClassKeyword() {
        doTest("ignoreIdentsPreservesClassLiteral", "", ignoreIdents(true));
    }


    private static Properties ignoreAnnotations(boolean doIgnore) {
        return properties(doIgnore, false, false);
    }

    private static Properties ignoreIdents(boolean doIgnore) {
        return properties(false, false, doIgnore);
    }


    @NotNull
    @Override
    public Properties defaultProperties() {
        return properties(false, false, false);
    }

    private static Properties properties(boolean ignoreAnnotations,
                                         boolean ignoreLiterals,
                                         boolean ignoreIdents) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.IGNORE_ANNOTATIONS, Boolean.toString(ignoreAnnotations));
        properties.setProperty(Tokenizer.IGNORE_IDENTIFIERS, Boolean.toString(ignoreIdents));
        properties.setProperty(Tokenizer.IGNORE_LITERALS, Boolean.toString(ignoreLiterals));
        return properties;
    }


}
