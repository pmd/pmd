package net.sourceforge.pmd.cpd.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import org.junit.jupiter.api.Test;

class BaseTokenFilterDiffblueTest {
    /**
     * Method under test: {@link BaseTokenFilter#getNextToken()}
     */
    @Test
    void testGetNextToken() {
        // Arrange
        TokenManager<BaseTokenFilterTest.StringToken> tokenManager = mock(TokenManager.class);
        BaseTokenFilterTest.StringToken stringToken = new BaseTokenFilterTest.StringToken("Text");
        when(tokenManager.getNextToken()).thenReturn(stringToken);
        BaseTokenFilter<BaseTokenFilterTest.StringToken> baseTokenFilter = new BaseTokenFilter<>(tokenManager);

        // Act
        BaseTokenFilterTest.StringToken actualNextToken = baseTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertSame(stringToken, actualNextToken);
    }

    /**
     * Method under test: {@link BaseTokenFilter#getNextToken()}
     */
    @Test
    void testGetNextToken2() {
        // Arrange
        TokenManager<BaseTokenFilterTest.StringToken> tokenManager = mock(TokenManager.class);
        when(tokenManager.getNextToken()).thenReturn(new BaseTokenFilterTest.StringToken(null));
        BaseTokenFilter<BaseTokenFilterTest.StringToken> baseTokenFilter = new BaseTokenFilter<>(tokenManager);

        // Act
        BaseTokenFilterTest.StringToken actualNextToken = baseTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertNull(actualNextToken);
    }

    /**
     * Method under test: {@link BaseTokenFilter#getNextToken()}
     */
    @Test
    void testGetNextToken3() {
        // Arrange
        TokenManager<BaseTokenFilterTest.StringToken> tokenManager = mock(TokenManager.class);
        BaseTokenFilterTest.StringToken stringToken = new BaseTokenFilterTest.StringToken("Text");
        when(tokenManager.getNextToken()).thenReturn(stringToken);
        BaseTokenFilterTest.DummyTokenFilter<BaseTokenFilterTest.StringToken> dummyTokenFilter = new BaseTokenFilterTest.DummyTokenFilter<>(
                tokenManager);

        // Act
        BaseTokenFilterTest.StringToken actualNextToken = dummyTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertSame(stringToken, actualNextToken);
    }

    /**
     * Method under test: {@link BaseTokenFilter#shouldStopProcessing(GenericToken)}
     */
    @Test
    void testShouldStopProcessing() {
        // Arrange
        BaseTokenFilter<BaseTokenFilterTest.StringToken> baseTokenFilter = new BaseTokenFilter<>(mock(TokenManager.class));

        // Act and Assert
        assertFalse(baseTokenFilter.shouldStopProcessing(new BaseTokenFilterTest.StringToken("Text")));
    }

    /**
     * Method under test: {@link BaseTokenFilter#shouldStopProcessing(GenericToken)}
     */
    @Test
    void testShouldStopProcessing2() {
        // Arrange
        BaseTokenFilterTest.DummyTokenFilter<BaseTokenFilterTest.StringToken> dummyTokenFilter = new BaseTokenFilterTest.DummyTokenFilter<>(
                mock(TokenManager.class));

        // Act and Assert
        assertTrue(dummyTokenFilter.shouldStopProcessing(null));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link BaseTokenFilter#analyzeToken(GenericToken)}
     *   <li>{@link BaseTokenFilter#analyzeTokens(GenericToken, Iterable)}
     *   <li>{@link BaseTokenFilter#isLanguageSpecificDiscarding()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        BaseTokenFilter<BaseTokenFilterTest.StringToken> baseTokenFilter = new BaseTokenFilter<>(mock(TokenManager.class));

        // Act
        baseTokenFilter.analyzeToken(new BaseTokenFilterTest.StringToken("Text"));
        BaseTokenFilterTest.StringToken stringToken = new BaseTokenFilterTest.StringToken("Text");
        baseTokenFilter.analyzeTokens(stringToken, new ArrayList<>());

        // Assert that nothing has changed
        assertFalse(baseTokenFilter.isLanguageSpecificDiscarding());
    }

    /**
     * Method under test: {@link BaseTokenFilter#BaseTokenFilter(TokenManager)}
     */
    @Test
    void testNewBaseTokenFilter() {
        // Arrange
        TokenManager<BaseTokenFilterTest.StringToken> tokenManager = mock(TokenManager.class);
        BaseTokenFilterTest.StringToken stringToken = new BaseTokenFilterTest.StringToken("Text");
        when(tokenManager.getNextToken()).thenReturn(stringToken);

        // Act
        BaseTokenFilter<BaseTokenFilterTest.StringToken> actualBaseTokenFilter = new BaseTokenFilter<>(tokenManager);
        BaseTokenFilterTest.StringToken actualNextToken = actualBaseTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertFalse(actualBaseTokenFilter.isLanguageSpecificDiscarding());
        assertSame(stringToken, actualNextToken);
    }

    /**
     * Method under test: {@link BaseTokenFilter#BaseTokenFilter(TokenManager)}
     */
    @Test
    void testNewBaseTokenFilter2() {
        // Arrange
        TokenManager<BaseTokenFilterTest.StringToken> tokenManager = mock(TokenManager.class);
        when(tokenManager.getNextToken()).thenReturn(new BaseTokenFilterTest.StringToken(null));

        // Act
        BaseTokenFilter<BaseTokenFilterTest.StringToken> actualBaseTokenFilter = new BaseTokenFilter<>(tokenManager);
        BaseTokenFilterTest.StringToken actualNextToken = actualBaseTokenFilter.getNextToken();

        // Assert
        verify(tokenManager).getNextToken();
        assertNull(actualNextToken);
        assertFalse(actualBaseTokenFilter.isLanguageSpecificDiscarding());
    }
}
