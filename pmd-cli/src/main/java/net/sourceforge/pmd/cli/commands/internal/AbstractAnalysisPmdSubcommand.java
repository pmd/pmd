package net.sourceforge.pmd.cli.commands.internal;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

public abstract class AbstractAnalysisPmdSubcommand extends AbstractPmdSubcommand {

    @Option(names = { "--encoding", "-e" }, description = "Specifies the character set encoding of the source code files",
            defaultValue = "UTF-8")
    protected Charset encoding;
    
    @Option(names = { "--dir", "-d" },
            description = "Path to a source file, or directory containing source files to analyze. "
                          + "Zip and Jar files are also supported, if they are specified directly "
                          + "(archive files found while exploring a directory are not recursively expanded). "
                          + "This option can be repeated, and multiple arguments can be provided to a single occurrence of the option. "
                          + "One of --dir, --file-list or --uri must be provided. ",
            arity = "1..*")
    protected List<Path> inputPaths;

    @Option(names = { "--file-list" },
            description =
                "Path to a file containing a list of files to analyze, one path per line. "
                + "One of --dir, --file-list or --uri must be provided.")
    protected Path fileListPath;
    
    @Option(names = { "--uri", "-u" },
            description = "Database URI for sources. "
                          + "One of --dir, --file-list or --uri must be provided.")
    protected URI uri;
    
    @Option(names = { "--fail-on-violation" },
            description = "By default PMD exits with status 4 if violations are found. "
                    + "Disable this option with '--fail-on-violation false' to exit with 0 instead and just write the report.",
            defaultValue = "true", arity = "1")
    protected boolean failOnViolation;

    @Override
    protected final void validate() throws ParameterException {
        super.validate();

        if ((inputPaths == null || inputPaths.isEmpty()) && uri == null && fileListPath == null) {
            throw new ParameterException(spec.commandLine(),
                    "Please provide a parameter for source root directory (--dir or -d), "
                            + "database URI (--uri or -u), or file list path (--file-list)");
        }
    }
}
