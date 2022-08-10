package net.sourceforge.pmd.cli.commands.internal;

import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.util.fxdesigner.DesignerStarter;
import net.sourceforge.pmd.util.fxdesigner.DesignerVersion;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

@Command(name = "designer", showDefaultValues = true,
    versionProvider = DesignerVersionProvider.class,
    description = "The PMD visual rule designer")
public class DesignerCommand extends AbstractPmdSubcommand {

    @Option(names = {"-V", "--version"}, versionHelp = true, description = "Print version information and exit.")
    private boolean versionRequested;

    private static final int OK = 0;

    @Override
    protected ExecutionResult execute() {
        final String[] rawArgs = spec.commandLine().getParseResult().expandedArgs().toArray(new String[0]);
        final int status = DesignerStarter.launchGui(rawArgs);

        return status == OK ? ExecutionResult.OK : ExecutionResult.ERROR;
    }
}

class DesignerVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { "PMD Rule Designer " + DesignerVersion.getCurrentVersion() };
    }
    
}