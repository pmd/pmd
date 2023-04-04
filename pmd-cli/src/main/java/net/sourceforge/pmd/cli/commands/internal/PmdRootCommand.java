/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import net.sourceforge.pmd.PMDVersion;

import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(name = "pmd", mixinStandardHelpOptions = true,
    versionProvider = PMDVersionProvider.class,
    exitCodeListHeading = "Exit Codes:%n",
    exitCodeList = { "0:Successful analysis, no violations found", "1:An unexpected error occurred during execution",
        "2:Usage error, please refer to the command help", "4:Successful analysis, at least 1 violation found" },
    subcommands = { PmdCommand.class, CpdCommand.class, DesignerCommand.class, CpdGuiCommand.class, TreeExportCommand.class })
public class PmdRootCommand {

}

class PMDVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD " + PMDVersion.VERSION };
    }
}
