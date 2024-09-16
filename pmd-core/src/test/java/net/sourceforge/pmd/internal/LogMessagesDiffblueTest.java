package net.sourceforge.pmd.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LogMessagesDiffblueTest {
    /**
     * Method under test: {@link LogMessages#errorDetectedMessage(int, String)}
     */
    @Test
    void testErrorDetectedMessage() {
        // Arrange, Act and Assert
        assertEquals(
                "-1 errors occurred while executing Program.\n" + "Run in verbose mode to see a stack-trace.\n"
                        + "If you think this is a bug in Program, please report this issue at https://github.com/pmd/pmd/issues"
                        + "/new/choose\n" + "If you do so, please include a stack-trace, the code sample\n"
                        + " causing the issue, and details about your run configuration.",
                LogMessages.errorDetectedMessage(-1, "Program"));
        assertEquals(
                "An error occurred while executing Program.\n" + "Run in verbose mode to see a stack-trace.\n"
                        + "If you think this is a bug in Program, please report this issue at https://github.com/pmd/pmd/issues"
                        + "/new/choose\n" + "If you do so, please include a stack-trace, the code sample\n"
                        + " causing the issue, and details about your run configuration.",
                LogMessages.errorDetectedMessage(1, "Program"));
    }
}
