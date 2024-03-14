/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import net.sourceforge.pmd.cli.commands.internal.PmdRootCommand;

import picocli.CommandLine;

public final class PmdCli {

    private PmdCli() { }

    public static void main(String[] args) {
        // See https://github.com/remkop/picocli/blob/main/RELEASE-NOTES.md#-picocli-470
        // and https://picocli.info/#_closures_in_annotations
        // we don't use this feature. Disabling it avoids leaving the groovy jar open
        // caused by Class.forName("groovy.lang.Closure")
        System.setProperty("picocli.disable.closures", "true");
        final CommandLine cli = new CommandLine(new PmdRootCommand())
                .setCaseInsensitiveEnumValuesAllowed(true);
        
        System.exit(cli.execute(args));
    }
}
