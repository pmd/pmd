/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package me.tongfei.progressbar;

import static me.tongfei.progressbar.TerminalUtils.CARRIAGE_RETURN;

import java.io.PrintStream;

/**
 * This is a friend class for me.tongfei.progressbar, as TerminalUtils is package-private.
 */
public final class PmdProgressBarFriend {
    
    private PmdProgressBarFriend() {
        throw new AssertionError("Can't instantiate utility classes");
    }

    public static ConsoleProgressBarConsumer createConsoleConsumer(PrintStream ps) {
        return TerminalUtils.hasCursorMovementSupport()
            ? new InteractiveConsoleProgressBarConsumer(ps)
            : new PostCarriageReturnConsoleProgressBarConsumer(ps);
    }
    
    private static class PostCarriageReturnConsoleProgressBarConsumer extends ConsoleProgressBarConsumer {

        PostCarriageReturnConsoleProgressBarConsumer(PrintStream out) {
            super(out);
        }

        @Override
        public void accept(String str) {
            // Set the carriage return at the end instead of at the beginning
            out.print(StringDisplayUtils.trimDisplayLength(str, getMaxRenderedLength()) + CARRIAGE_RETURN);
        }
        
        @Override
        public void clear() {
            // do nothing (prints an empty line otherwise)
        }
    }
}
