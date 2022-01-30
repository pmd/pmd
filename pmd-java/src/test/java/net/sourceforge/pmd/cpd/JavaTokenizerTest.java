/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

@Ignore("Needs to be enabled after java-grammar changes are finalized")
public class JavaTokenizerTest extends CpdTextComparisonTest {

    public JavaTokenizerTest() {
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
    public void testCommentsIgnored() {
        doTest("simpleClassWithComments");
    }

    @Test
    public void testDiscardedElements() {
        doTest("discardedElements", "_ignore_annots", ignoreAnnotations());
    }

    @Test
    public void testDiscardedElementsExceptAnnots() {
        doTest("discardedElements", "_no_ignore_annots");
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
        doTest("ignoreSpecialAnnotations", "_ignore_annots", ignoreAnnotations());
    }

    @Test
    public void testIgnoreIdentifiersDontAffectConstructors() {
        doTest("ignoreIdentsPreservesCtor", "", ignoreIdents());
    }

    @Test
    public void testIgnoreIdentifiersHandlesEnums() {
        doTest("ignoreIdentsPreservesEnum", "", ignoreIdents());
    }

    @Test
    public void testIgnoreIdentifiersWithClassKeyword() {
        doTest("ignoreIdentsPreservesClassLiteral", "", ignoreIdents());
    }

    @Test
    public void testIgnoreLiterals() {
        doTest("ignoreLiterals", "", ignoreLiterals());
    }

    @Test
    public void testNoIgnoreLiterals() {
        doTest("ignoreLiterals", "_noignore");
    }

    @Test
    public void testTabWidth() {
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
