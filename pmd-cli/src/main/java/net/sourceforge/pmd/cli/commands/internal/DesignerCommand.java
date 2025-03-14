/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.util.fxdesigner.DesignerStarter;
import net.sourceforge.pmd.util.fxdesigner.DesignerStarter.ExitStatus;
import net.sourceforge.pmd.util.fxdesigner.DesignerVersion;

import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

@Command(name = "designer", showDefaultValues = true,
    versionProvider = DesignerVersionProvider.class,
    description = "The PMD visual rule designer")
public class DesignerCommand extends AbstractPmdSubcommand {

    @SuppressWarnings("unused")
    @Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionRequested;

    @Override
    protected CliExitCode execute() {
        final String[] rawArgs = spec.commandLine().getParseResult().expandedArgs().toArray(new String[0]);
        final ExitStatus status = DesignerStarter.launchGui(rawArgs);

        return status == ExitStatus.OK ? CliExitCode.OK : CliExitCode.ERROR;
    }
}

class DesignerVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD Rule Designer " + DesignerVersion.getCurrentVersion() };
    }
    
}
