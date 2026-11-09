/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import me.tongfei.progressbar.ConsoleProgressBarConsumer;
import me.tongfei.progressbar.InteractiveConsoleProgressBarConsumer;
import me.tongfei.progressbar.ProgressBarConsumer;

/**
 * Creates the ProgressBarConsumer used by the CLI. It differs from the library's
 * default selection ({@code me.tongfei.progressbar.Util#createConsoleConsumer}, which is package-private):
 *
 * <ul>
 *     <li>The progressbar is written to the given PrintStream (e.g. stdout), not to stderr
 *     as with the {@link me.tongfei.progressbar.ProgressBarBuilder} default.</li>
 *     <li>When cursor movement is supported (a real interactive terminal), the library's
 *     {@link InteractiveConsoleProgressBarConsumer} is used.</li>
 *     <li>Otherwise (dumb terminal, e.g. redirected output), a custom "post CR" consumer
 *     is used instead of {@link ConsoleProgressBarConsumer}: it ends each progressbar text with CR
 *     instead of starting with it. The cursor stays at the beginning of the line, so interleaved log
 *     messages start at column 0 and overwrite the bar, which is redrawn below them. With
 *     {@link ConsoleProgressBarConsumer}, they would be appended to the end of the progressbar line.</li>
 * </ul>
 */
final class ProgressBarUtil {
    private static final char CARRIAGE_RETURN = '\r';

    private ProgressBarUtil() {
        throw new AssertionError("Can't instantiate utility classes");
    }

    public static ProgressBarConsumer createConsoleConsumer(PrintStream ps) {
        final boolean cursorMovementSupported;
        try (Terminal terminal = TerminalBuilder.builder().dumb(true).build()) {
            cursorMovementSupported =
                    terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null
                            && terminal.getStringCapability(InfoCmp.Capability.cursor_down) != null;
        } catch (IOException e) {
            throw new RuntimeException("This should never happen! Dumb terminal should have been created.");
        }

        return cursorMovementSupported
                ? new InteractiveConsoleProgressBarConsumer(ps)
                : new PostCarriageReturnConsoleProgressBarConsumer(ps);
    }

    private static class PostCarriageReturnConsoleProgressBarConsumer implements ProgressBarConsumer {
        private final ConsoleProgressBarConsumer delegate;
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final PrintStream out;

        PostCarriageReturnConsoleProgressBarConsumer(PrintStream out) {
            try {
                this.delegate = new ConsoleProgressBarConsumer(new PrintStream(baos, true, Charset.defaultCharset().name()));
                this.out = out;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public int getMaxRenderedLength() {
            return delegate.getMaxRenderedLength();
        }

        @Override
        public void accept(String rendered) {
            try {
                baos.reset();
                delegate.accept(rendered);
                String output = baos.toString(Charset.defaultCharset().name());
                // remove the CR from the beginning and add it at the end
                output = output.substring(1) + CARRIAGE_RETURN;
                out.print(output);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void clear() {
            // do nothing (prints an empty line otherwise)
        }

        @Override
        public void close() {
            out.println();
            out.flush();
            delegate.close();
        }
    }
}
