/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.cli.internal.PmdRootLogger;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;

public abstract class AbstractAnalysisPmdSubcommand<C extends AbstractConfiguration> extends AbstractPmdSubcommand {

    @CommandLine.Spec
    protected CommandSpec spec; // injected by PicoCli, needed for validations

    protected static final String FILE_COLLECTION_OPTION_HEADER = "Input files specification";


    protected static class FileCollectionOptions<C extends AbstractConfiguration> {

        @Option(names = {"--encoding", "-e"}, description = "Specifies the character set encoding of the source code files",
                defaultValue = "UTF-8")
        private Charset encoding;


        @Option(names = "--file-list",
                description =
                        "Path to a file containing a list of files to analyze, one path per line. "
                        + "One of --dir, --file-list or --uri must be provided.")
        private Path fileListPath;

        @Option(names = {"--uri", "-u"},
                description = "Database URI for sources. "
                              + "One of --dir, --file-list or --uri must be provided.")
        private URI uri;


        boolean usesDeprecatedIgnoreOption = false;
        boolean usesDeprecatedIgnoreListOption = false;

        @Option(names = "--ignore", arity = "1..*", description = "(DEPRECATED: use --exclude) Files to be excluded from the analysis")
        @Deprecated
        protected void setIgnoreSpecificPaths(List<Path> rootPaths) {
            this.excludeFiles = CollectionUtil.makeUnmodifiableAndNonNull(rootPaths);
            this.usesDeprecatedIgnoreOption = true;
        }

        @Option(names = "--ignore-list",
                description = "(DEPRECATED: use --exclude-file-list) Path to a file containing a list of files to exclude from the analysis, one path per line. "
                              + "This option can be combined with --dir, --file-list and --uri.")
        @Deprecated
        protected void setExcludeFileList(Path path) {
            this.excludeFileListPath = path;
            this.usesDeprecatedIgnoreListOption = true;
        }

        @Option(names = "--exclude", arity = "1..*", description = "Files to be excluded from the analysis")
        private List<Path> excludeFiles = new ArrayList<>();

        @Option(names = "--exclude-file-list",
                description = "Path to a file containing a list of files to exclude from the analysis, one path per line. "
                              + "This option can be combined with --dir, --file-list and --uri.")
        private Path excludeFileListPath;


        @Option(names = {"--relativize-paths-with", "-z"}, description = "Path relative to which directories are rendered in the report. "
                                                                         + "This option allows shortening directories in the report; "
                                                                         + "without it, paths are rendered as mentioned in the source directory (option \"--dir\"). "
                                                                         + "The option can be repeated, in which case the shortest relative path will be used. "
                                                                         + "If the root path is mentioned (e.g. \"/\" or \"C:\\\"), then the paths will be rendered as absolute.",
                arity = "1..*", split = ",")
        private List<Path> relativizeRootPaths;

        // see the setters #setInputPaths and setPositionalInputPaths for @Option and @Parameters annotations
        // Note: can't use annotations on the fields here, as otherwise the complete list would be replaced
        // rather than accumulated.
        private Set<Path> inputPaths;

        @Option(names = {"--dir", "-d"},
                description = "Path to a source file, or directory containing source files to analyze. "
                              + "Zip and Jar files are also supported, if they are specified directly "
                              + "(archive files found while exploring a directory are not recursively expanded). "
                              + "This option can be repeated, and multiple arguments can be provided to a single occurrence of the option. "
                              + "One of --dir, --file-list or --uri must be provided.",
                arity = "1..*", split = ",")
        protected void setInputPaths(final List<Path> inputPaths) {
            if (this.inputPaths == null) {
                this.inputPaths = new LinkedHashSet<>(); // linked hashSet in order to maintain order
            }

            this.inputPaths.addAll(inputPaths);
        }

        @Option(names = "--non-recursive", description = "Don't scan subdirectiories when using the --d (-dir) option.")
        private boolean nonRecursive;


        @Parameters(arity = "*", description = "Path to a source file, or directory containing source files to analyze. "
                                               + "Equivalent to using --dir.")
        protected void setPositionalInputPaths(final List<Path> inputPaths) {
            this.setInputPaths(inputPaths);
        }


        protected void configureFilesToAnalyze(C configuration) {
            // Configure input paths
            if (inputPaths != null) {
                configuration.setInputPathList(new ArrayList<>(inputPaths));
            }
            configuration.setExcludes(excludeFiles);
            configuration.collectFilesRecursively(!nonRecursive);
            configuration.setInputFilePath(fileListPath);
            configuration.setIgnoreFilePath(excludeFileListPath);
            configuration.setInputUri(uri);
            if (relativizeRootPaths != null) {
                configuration.addRelativizeRoots(relativizeRootPaths);
            }
            configuration.setSourceEncoding(encoding);

            if (usesDeprecatedIgnoreOption) {
                configuration.getReporter().warn("Option name --ignore is deprecated. Use --exclude instead.");
            }
            if (usesDeprecatedIgnoreListOption) {
                configuration.getReporter().warn("Option name --ignore-list is deprecated. Use --exclude-file-list instead.");
            }
        }


        protected void validate(CommandSpec spec) throws ParameterException {

            if ((inputPaths == null || inputPaths.isEmpty()) && uri == null && fileListPath == null) {
                throw new ParameterException(spec.commandLine(),
                        "Please provide a parameter for source root directory (--dir or -d), "
                        + "database URI (--uri or -u), or file list path (--file-list)");
            }

            if (relativizeRootPaths != null) {
                for (Path path : this.relativizeRootPaths) {
                    if (Files.isRegularFile(path)) {
                        throw new ParameterException(spec.commandLine(),
                                "Expected a directory path for option '--relativize-paths-with', found a file: " + path);
                    }
                }
            }
        }

    }

    @Option(names = "--no-fail-on-violation",
            description = "By default PMD exits with status 4 if violations or duplications are found. "
                    + "Disable this option with '--no-fail-on-violation' to exit with 0 instead. In any case a report with the found violations or duplications will be written.",
            defaultValue = "true", negatable = true)
    private boolean failOnViolation;

    @Option(names = "--no-fail-on-error",
            description = "By default PMD exits with status 5 if recoverable errors occurred (whether or not there are violations or duplications). "
                    + "Disable this option with '--no-fail-on-error' to exit with 0 instead. In any case, a report with the found violations or duplications will be written.",
            defaultValue = "true", negatable = true)
    private boolean failOnError;


    @Option(names = { "--report-file", "-r" },
            description = "Path to a file to which report output is written. "
                          + "The file is created if it does not exist. "
                          + "If this option is not specified, the report is rendered to standard output.")
    private Path reportFile;


    private int threads;
    @Option(names = { "--threads", "-t" }, description = "Sets the number of threads used by PMD.",
        defaultValue = "1")
    public void setThreads(final int threads) {
        if (threads < 0) {
            throw new ParameterException(spec.commandLine(), "Thread count should be a positive number or zero, found " + threads + " instead.");
        }

        this.threads = threads;
    }

    protected abstract C toConfiguration();

    protected abstract CliExitCode doExecute(C conf);


    @Override
    protected CliExitCode execute() {
        final C configuration = toConfiguration();
        return PmdRootLogger.executeInLoggingContext(configuration,
                                                     debug,
                                                     this::doExecute);
    }

    protected abstract FileCollectionOptions<C> getFileCollectionOptions();

    @Override
    protected void validate() throws ParameterException {
        super.validate();
        getFileCollectionOptions().validate(spec);
    }

    protected final void setCommonConfigProperties(C configuration) {
        SimpleMessageReporter reporter = new SimpleMessageReporter(LoggerFactory.getLogger(PmdCommand.class));
        // Setup CLI message reporter
        configuration.setReporter(reporter);

        getFileCollectionOptions().configureFilesToAnalyze(configuration);

        configuration.setThreads(threads);

        // reporting logic
        configuration.setReportFile(reportFile);
        // configuration.setReportProperties(properties);
        configuration.setFailOnViolation(failOnViolation);
        configuration.setFailOnError(failOnError);

    }

}
