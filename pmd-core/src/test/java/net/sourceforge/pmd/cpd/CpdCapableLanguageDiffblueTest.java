package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import net.sourceforge.pmd.lang.CpdOnlyDummyLanguage;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.junit.jupiter.api.Test;

class CpdCapableLanguageDiffblueTest {
    /**
     * Method under test:
     * {@link CpdCapableLanguage#createCpdLexer(LanguagePropertyBundle)}
     */
    @Test
    void testCreateCpdLexer() throws IOException {
        // Arrange
        Dummy2LanguageModule dummy2LanguageModule = new Dummy2LanguageModule();

        // Act
        CpdLexer actualCreateCpdLexerResult = dummy2LanguageModule
                .createCpdLexer(new LanguagePropertyBundle(new CpdOnlyDummyLanguage()));
        TextDocument textDocument = mock(TextDocument.class);
        when(textDocument.getText()).thenReturn(Chars.EMPTY);
        actualCreateCpdLexerResult.tokenize(textDocument, mock(TokenFactory.class));

        // Assert that nothing has changed
        verify(textDocument).getText();
        assertTrue(actualCreateCpdLexerResult instanceof AnyCpdLexer);
    }
}
