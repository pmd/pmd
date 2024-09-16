package net.sourceforge.pmd.cpd.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import org.antlr.v4.runtime.CommonToken;
import org.junit.jupiter.api.Test;

class AntlrTokenFilterDiffblueTest {
    /**
     * Method under test: {@link AntlrTokenFilter#AntlrTokenFilter(TokenManager)}
     */
    @Test
    void testNewAntlrTokenFilter() {
        // Arrange
        TokenManager<AntlrToken> tokenManager = mock(TokenManager.class);
        AntlrToken antlrToken = new AntlrToken(new CommonToken(1), null, mock(TextDocument.class));

        when(tokenManager.getNextToken()).thenReturn(antlrToken);

        // Act
        AntlrTokenFilter actualAntlrTokenFilter = new AntlrTokenFilter(tokenManager);
        AntlrToken actualNextToken = actualAntlrTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertFalse(actualAntlrTokenFilter.isLanguageSpecificDiscarding());
        assertSame(antlrToken, actualNextToken);
    }

    /**
     * Method under test: {@link AntlrTokenFilter#AntlrTokenFilter(TokenManager)}
     */
    @Test
    void testNewAntlrTokenFilter2() {
        // Arrange
        TokenManager<AntlrToken> tokenManager = mock(TokenManager.class);
        when(tokenManager.getNextToken()).thenReturn(new AntlrToken(new CommonToken(-1), null, mock(TextDocument.class)));

        // Act
        AntlrTokenFilter actualAntlrTokenFilter = new AntlrTokenFilter(tokenManager);
        AntlrToken actualNextToken = actualAntlrTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertNull(actualNextToken);
        assertFalse(actualAntlrTokenFilter.isLanguageSpecificDiscarding());
    }

    /**
     * Method under test: {@link AntlrTokenFilter#AntlrTokenFilter(TokenManager)}
     */
    @Test
    void testNewAntlrTokenFilter3() {
        // Arrange
        AntlrToken previousComment = mock(AntlrToken.class);
        when(previousComment.getPreviousComment())
                .thenReturn(new AntlrToken(new CommonToken(1, "Text"), null, mock(TextDocument.class)));
        when(previousComment.getImage()).thenReturn("Image");
        AntlrToken antlrToken = new AntlrToken(new CommonToken(1), previousComment, mock(TextDocument.class));

        TokenManager<AntlrToken> tokenManager = mock(TokenManager.class);
        when(tokenManager.getNextToken()).thenReturn(antlrToken);

        // Act
        AntlrTokenFilter actualAntlrTokenFilter = new AntlrTokenFilter(tokenManager);
        AntlrToken actualNextToken = actualAntlrTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        verify(previousComment, atLeast(1)).getImage();
        verify(previousComment).getPreviousComment();
        assertFalse(actualAntlrTokenFilter.isLanguageSpecificDiscarding());
        assertSame(antlrToken, actualNextToken);
    }
}
