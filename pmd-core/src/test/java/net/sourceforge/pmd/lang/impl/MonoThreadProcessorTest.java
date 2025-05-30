/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.event.Level;

import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.rule.RuleSet;

class MonoThreadProcessorTest extends AbstractPMDProcessorTest {

    @Override
    protected int getThreads() {
        return 0;
    }

    @Override
    protected Class<? extends AbstractPMDProcessor> getExpectedImplementation() {
        return MonoThreadProcessor.class;
    }

    @Test
    void errorsShouldBeThrown() {
        try (PmdAnalysis pmd = createPmdAnalysis()) {
            pmd.addRuleSet(RuleSet.forSingleRule(new RuleThatThrowsError()));
            Error exception = assertThrows(Error.class, pmd::performAnalysis);
            assertEquals("test error", exception.getMessage());
        }

        // in mono thread, files are processed one after another.
        // in case of error, we abort at the first error, so in this test case
        // we abort at the first file, so only 1 file is processed.
        assertEquals(1, reportListener.files.get());
        Mockito.verify(reporter, Mockito.times(1)).log(Level.DEBUG, "Using main thread for analysis");
        // in mono thread, the error just falls through, we don't additionally catch and log it.
        Mockito.verifyNoMoreInteractions(reporter);
    }
}
