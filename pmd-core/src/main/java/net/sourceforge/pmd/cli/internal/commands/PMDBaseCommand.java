package net.sourceforge.pmd.cli.internal.commands;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.cli.internal.PMDCLI.CPDPicoCli;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(name = "pmd", mixinStandardHelpOptions = true,
    versionProvider = PMDVersionProvider.class,
    subcommands = { PMDCommand.class, CPDPicoCli.class })
public class PMDBaseCommand {

}

class PMDVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD " + PMDVersion.VERSION };
    }
}