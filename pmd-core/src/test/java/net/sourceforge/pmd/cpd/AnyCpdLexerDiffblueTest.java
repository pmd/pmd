package net.sourceforge.pmd.cpd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.junit.jupiter.api.Test;

class AnyCpdLexerDiffblueTest {
    /**
     * Method under test: {@link AnyCpdLexer#AnyCpdLexer()}
     */
    @Test
    void testNewAnyCpdLexer() {
        // Arrange and Act
        AnyCpdLexer actualAnyCpdLexer = new AnyCpdLexer();
        TextDocument document = mock(TextDocument.class);
        when(document.getText()).thenReturn(Chars.EMPTY);
        actualAnyCpdLexer.tokenize(document, mock(TokenFactory.class));

        // Assert that nothing has changed
        verify(document).getText();
    }

    /**
     * Method under test: {@link AnyCpdLexer#tokenize(TextDocument, TokenFactory)}
     */
    @Test
    void testTokenize() {
        // Arrange
        AnyCpdLexer anyCpdLexer = new AnyCpdLexer();
        TextDocument document = mock(TextDocument.class);
        when(document.getText()).thenReturn(Chars.EMPTY);

        // Act
        anyCpdLexer.tokenize(document, mock(TokenFactory.class));

        // Assert that nothing has changed
        verify(document).getText();
    }

    /**
     * Method under test: {@link AnyCpdLexer#AnyCpdLexer(String)}
     */
    @Test
    void testNewAnyCpdLexer2() {
        // Arrange and Act
        AnyCpdLexer actualAnyCpdLexer = new AnyCpdLexer("Eol Comment Start");
        TextDocument document = mock(TextDocument.class);
        when(document.getText()).thenReturn(Chars.EMPTY);
        actualAnyCpdLexer.tokenize(document, mock(TokenFactory.class));

        // Assert that nothing has changed
        verify(document).getText();
    }
}
