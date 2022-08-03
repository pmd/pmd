package net.sourceforge.pmd.cli.commands.internal;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.cli.PmdCli.CpdPicoCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(name = "pmd", mixinStandardHelpOptions = true,
    versionProvider = PMDVersionProvider.class,
    exitCodeListHeading = "Exit Codes:%n",
    exitCodeList = { "0:Succesful analysis, no violations found", "1:An unexpected error occurred during execution",
            "2:Usage error, please refer to the command help", "4:Successful analysis, at least 1 violation found" },
    subcommands = { PmdCommand.class, CpdPicoCli.class })
public class PmdRootCommand {

}

class PMDVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD " + PMDVersion.VERSION };
    }
}