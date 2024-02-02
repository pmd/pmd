/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.cpd.test.LanguagePropertyConfig;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

// TODO - enable tests
class JavaCpdLexerTest extends CpdTextComparisonTest {

    JavaCpdLexerTest() {
        super(JavaLanguageModule.getInstance(), ".java");
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
    void testLexExceptionLocation() {
        CpdLexer cpdLexer = newCpdLexer(defaultProperties());
        Tokens tokens = new Tokens();
        LexException lexException = assertThrows(LexException.class, () ->
            CpdLexer.tokenize(cpdLexer,
                    // note: the source deliberately contains an unbalanced quote, unterminated string literal
                    TextDocument.readOnlyString("class F {\n    String s=\"abc\";\"\n}\n", FileId.UNKNOWN, getLanguage().getDefaultVersion()),
                    tokens)
        );
        // this shouldn't throw a IllegalArgumentException
        assertThat(lexException.getMessage(), containsString("at line 3, column 1"));
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


    private static LanguagePropertyConfig ignoreAnnotations() {
        return properties(true, false, false);
    }

    private static LanguagePropertyConfig ignoreIdents() {
        return properties(false, false, true);
    }

    private static LanguagePropertyConfig ignoreLiterals() {
        return properties(false, true, false);
    }


    @Override
    public LanguagePropertyConfig defaultProperties() {
        return properties(false, false, false);
    }

    private static LanguagePropertyConfig properties(boolean ignoreAnnotations,
                                                     boolean ignoreLiterals,
                                                     boolean ignoreIdents) {
        return properties -> {
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_METADATA, ignoreAnnotations);
            properties.setProperty(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS, ignoreIdents);
            properties.setProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS, ignoreLiterals);
        };
    }


}
