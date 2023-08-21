/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Abstraction over system streams. Useful to unit-test a CLI program.
 *
 * @author Clément Fournier
 */
final class Io {

    public final PrintStream stdout;
    public final PrintStream stderr;
    public final InputStream stdin;

    Io(PrintStream stdout, PrintStream stderr, InputStream stdin) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.stdin = stdin;
    }
    
    public static Io system() {
        return new Io(System.out, System.err, System.in);
    }
}
