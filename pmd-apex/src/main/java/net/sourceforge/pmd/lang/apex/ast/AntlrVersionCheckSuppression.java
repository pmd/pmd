/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.antlr.v4.runtime.CharStreams;

import com.nawforce.apexparser.ApexLexer;
import com.nawforce.apexparser.ApexParser;

/**
 * Suppresses the warnings about mismatched ANTLR runtime/compilation version.
 * <p>ApexParser is using ANTLR 4.8, but PMD is using ANTLR 4.9.</p>
 */
final class AntlrVersionCheckSuppression {
    private AntlrVersionCheckSuppression() {
        // utility
    }

    static void initApexLexer() {
        @SuppressWarnings("PMD.CloseResource") // ok not to close; is save/restore pattern
        PrintStream err = System.err;
        try {
            // Redirect System.err to suppress ANTLR warning about runtime/compilation version mismatch.
            // See: org.antlr.v4.runtime.RuntimeMetadata#checkVersion
            System.setErr(new PrintStream(new ByteArrayOutputStream()));

            // explicitly call the static initializer on these two classes
            Class.forName(ApexLexer.class.getName());
            Class.forName(ApexParser.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            System.setErr(err);
        }
    }
}
