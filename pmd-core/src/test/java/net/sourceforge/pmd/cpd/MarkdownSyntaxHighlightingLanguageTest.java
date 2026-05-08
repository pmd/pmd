/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.FileId;

class MarkdownSyntaxHighlightingLanguageTest {

    private static final List<Arguments> MARKDOWN_FOR_LANGUAGE_ID = Arrays.asList(
            Arguments.of("cpp", "cpp"),
            Arguments.of("java", "java"),
            Arguments.of("ecmascript", "js"),
            Arguments.of("typescript", "ts")
    );

    @ParameterizedTest
    @FieldSource("MARKDOWN_FOR_LANGUAGE_ID")
    void testGetMarkdownLanguageTag(String langId, String expected) {
        LanguageVersion langVer = mock();
        Language lang = mock();
        when(langVer.getLanguage()).thenReturn(lang);
        when(lang.getId()).thenReturn(langId);

        CpdTestUtils.CpdReportBuilder builder = new CpdTestUtils.CpdReportBuilder();

        FileId foo = CpdTestUtils.FOO_FILE_ID;

        builder.setFileContent(foo, langVer);

        int lineCount1 = 6;
        Mark mark1 = builder.createMark("public", foo, 48, lineCount1);
        Mark mark2 = builder.createMark("void", foo, 73, lineCount1);
        builder.addMatch(new Match(75, mark1, mark2));

        MarkdownRenderer subject = new MarkdownRenderer();
        CPDReport report = builder.build();

        String got = subject.getMarkdownLanguageTag(report, foo);

        assertEquals(expected, got);
    }
}
