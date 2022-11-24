/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

// TODO - enable test
@Disabled("Needs to be enabled after java-grammar changes are finalized")
class JavaTokenizerTest extends CpdTextComparisonTest {

    JavaTokenizerTest() {
        super(".java");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        JavaTokenizer javaTokenizer = new JavaTokenizer();
        javaTokenizer.setProperties(properties);
        return javaTokenizer;
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/java/cpd/testdata";
    }

    @Test
    void testCommentsIgnored() {
        doTest("simpleClassWithComments");
    }

    @Test
    void testDiscardedElements() {
        doTest("discardedElements", "_ignore_annots", ignoreAnnotations());
    }

    @Test
    void testDiscardedElementsExceptAnnots() {
        doTest("discardedElements", "_no_ignore_annots");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testIgnoreBetweenSpecialAnnotation() {
        doTest("ignoreSpecialAnnotations");
    }

    @Test
    void testIgnoreBetweenSpecialAnnotationAndIgnoreAnnotations() {
        doTest("ignoreSpecialAnnotations", "_ignore_annots", ignoreAnnotations());
    }

    @Test
    void testIgnoreIdentifiersDontAffectConstructors() {
        doTest("ignoreIdentsPreservesCtor", "", ignoreIdents());
    }

    @Test
    void testIgnoreIdentifiersHandlesEnums() {
        doTest("ignoreIdentsPreservesEnum", "", ignoreIdents());
    }

    @Test
    void testIgnoreIdentifiersWithClassKeyword() {
        doTest("ignoreIdentsPreservesClassLiteral", "", ignoreIdents());
    }

    @Test
    void testIgnoreLiterals() {
        doTest("ignoreLiterals", "", ignoreLiterals());
    }

    @Test
    void testNoIgnoreLiterals() {
        doTest("ignoreLiterals", "_noignore");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }


    private static Properties ignoreAnnotations() {
        return properties(true, false, false);
    }

    private static Properties ignoreIdents() {
        return properties(false, false, true);
    }

    private static Properties ignoreLiterals() {
        return properties(false, true, false);
    }


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
