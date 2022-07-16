/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.helpers.NOPLogger;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Reports errors that occur after parsing. This may be used to implement
 * semantic checks in a language specific way.
 */
class SemanticErrorReporterTest {

    private static final String MOCK_FILENAME = "dummy/file.txt";
    MessageReporter mockReporter;
    Logger mockLogger;

    @BeforeEach
    void setup() {
        mockReporter = mock(MessageReporter.class);
        when(mockReporter.isLoggable(Level.ERROR)).thenReturn(true);
        mockLogger = spy(NOPLogger.class);
    }

    @Test
    void testErrorLogging() {
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
    void testEscaping() {
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
        Language language = DummyLanguageModule.getInstance();
        Parser parser = language.getDefaultVersion().getLanguageVersionHandler().getParser();
        TextDocument doc = TextDocument.readOnlyString("(mock (node))", MOCK_FILENAME, language.getDefaultVersion());
        return parser.parse(new ParserTask(
            doc,
            reporter
        ));
    }

}
