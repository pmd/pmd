/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.helpers.NOPLogger;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Reports errors that occur after parsing. This may be used to implement
 * semantic checks in a language specific way.
 */
public class SemanticErrorReporterTest {

    private static final String MOCK_FILENAME = "dummy/file.txt";
    MessageReporter mockReporter;
    Logger mockLogger;

    @Before
    public void setup() {
        mockReporter = mock(MessageReporter.class);
        when(mockReporter.isLoggable(Level.ERROR)).thenReturn(true);
        mockLogger = spy(NOPLogger.class);
    }

    @Test
    public void testErrorLogging() {
        SemanticErrorReporter reporter = SemanticErrorReporter.reportToLogger(mockReporter);
        RootNode node = parseMockNode(reporter);

        assertNull(reporter.getFirstError());

        String message = "an error occurred";
        reporter.error(node, message);

        verify(mockReporter).log(eq(Level.ERROR), contains(message));
        verifyNoMoreInteractions(mockLogger);

        assertNotNull(reporter.getFirstError());
    }

    @Test
    public void testEscaping() {
        SemanticErrorReporter reporter = SemanticErrorReporter.reportToLogger(mockReporter);
        RootNode node = parseMockNode(reporter);

        // this is a MessageFormat string
        // what ends up being logged is just '
        String message = "an apostrophe '' ";
        reporter.error(node, message);

        // The backend reporter will do its own formatting once again
        verify(mockReporter).log(eq(Level.ERROR), contains("an apostrophe ''"));
        verifyNoMoreInteractions(mockLogger);
    }

    private RootNode parseMockNode(SemanticErrorReporter reporter) {
        Language language = LanguageRegistry.getLanguage(DummyLanguageModule.NAME);
        Parser parser = language.getDefaultVersion().getLanguageVersionHandler().getParser();
        return parser.parse(new ParserTask(
            language.getDefaultVersion(),
            MOCK_FILENAME,
            "(mock (node))",
            reporter
        ));
    }

}
