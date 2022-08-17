/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import net.sourceforge.pmd.cli.commands.internal.PmdRootCommand;

import picocli.CommandLine;

public final class PmdCli {

    private PmdCli() { }

    public static void main(String[] args) {
        new CommandLine(new PmdRootCommand()).setCaseInsensitiveEnumValuesAllowed(true)
                .execute("designer", "-v");
        //        .execute("run", "--use-version", "scala-2.11", "--use-version", "apex", "--use-version",
        //                "ecmascript-latest", "-P", "foo=bar", "-R", "foo,bar", "-R", "baz", "-d",
        //                "src/main/java", "-f", "xml");
    }
}
