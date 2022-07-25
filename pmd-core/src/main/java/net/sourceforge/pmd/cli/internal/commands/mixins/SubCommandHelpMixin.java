package net.sourceforge.pmd.cli.internal.commands.mixins;

import picocli.CommandLine.Option;

/**
 * A mixin for subcommands that need to show help,
 * but don't want to also display a version as done with {@code mixinStandardHelpOptions = true}
 */
public class SubCommandHelpMixin {
    @SuppressWarnings("unused")
    @Option(names = {"-h", "--help"}, usageHelp = true, descriptionKey = "mixinStandardHelpOptions.help",
            description = "Show this help message and exit.")
    private boolean helpRequested;
}
