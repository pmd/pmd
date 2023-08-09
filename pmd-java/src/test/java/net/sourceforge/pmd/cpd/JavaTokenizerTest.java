/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

// TODO - enable tests
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
    void testSimpleClass() {
        doTest("SimpleClass");
    }

    @Test
    void testStringTemplateReduction() {
        doTest("StringTemplateReduction");
    }

    @Test
    void testStringTemplateReduction2() {
        doTest("StringTemplateReduction2");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testCommentsIgnored() {
        doTest("simpleClassWithComments");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testDiscardedElements() {
        doTest("discardedElements", "_ignore_annots", ignoreAnnotations());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testDiscardedElementsExceptAnnots() {
        doTest("discardedElements", "_no_ignore_annots");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreBetweenSpecialAnnotation() {
        doTest("ignoreSpecialAnnotations");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreBetweenSpecialAnnotationAndIgnoreAnnotations() {
        doTest("ignoreSpecialAnnotations", "_ignore_annots", ignoreAnnotations());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreIdentifiersDontAffectConstructors() {
        doTest("ignoreIdentsPreservesCtor", "", ignoreIdents());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreIdentifiersHandlesEnums() {
        doTest("ignoreIdentsPreservesEnum", "", ignoreIdents());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreIdentifiersWithClassKeyword() {
        doTest("ignoreIdentsPreservesClassLiteral", "", ignoreIdents());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testIgnoreLiterals() {
        doTest("ignoreLiterals", "", ignoreLiterals());
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
    @Test
    void testNoIgnoreLiterals() {
        doTest("ignoreLiterals", "_noignore");
    }

    @Disabled("Needs to be enabled after java-grammar changes are finalized")
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
