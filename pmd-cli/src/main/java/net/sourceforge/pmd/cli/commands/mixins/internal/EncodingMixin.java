/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.mixins.internal;

import java.nio.charset.Charset;

import picocli.CommandLine.Option;

/**
 * A mixin for source code encoding. Used to ensure consistency among commands.
 */
public class EncodingMixin {

    @Option(names = { "--encoding", "-e" }, description = "Specifies the character set encoding of the source code files",
            defaultValue = "UTF-8")
    private Charset encoding;

    public Charset getEncoding() {
        return encoding;
    }
}
