package net.sourceforge.pmd.cli.internal;

import net.sourceforge.pmd.cli.internal.commands.PMDBaseCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public class PMDCLI {

    public static void main(String[] args) {
        tryPicoCli();
    }

    private static void tryPicoCli() {
        new CommandLine(new PMDBaseCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
//			    .addSubcommand(new PMDCommand())
//			    .addSubcommand(new CPDPicoCli())
                .execute("run", "-h");
//                .execute("run", "-t", "4", "-P", "foo=bar", "-R" , "foo,bar", "-R", "baz", "-d", "src/main/java", "-f", "xml");
    }

    @Command(name = "cpd", mixinStandardHelpOptions = true, description = "The Copy Paste Detector")
    public static class CPDPicoCli implements Runnable {
        @SuppressWarnings("unused")
        @Option(names = "--minimum-tokens",
                description = "The minimum token length which should be reported as a duplicate.",
                required = true)
        private int minimumTileSize;

        @SuppressWarnings("unused")
        @Option(names = "--skip-duplicate-files",
                description = "Ignore multiple copies of files of the same name and length in comparison",
                required = false)
        private boolean skipDuplicates;

        @Override
        public void run() {
            System.out.println("running cpd command");
        }
    }
}
