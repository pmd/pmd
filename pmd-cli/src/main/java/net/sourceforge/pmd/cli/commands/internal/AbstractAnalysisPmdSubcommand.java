/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.AbstractConfiguration;
import net.sourceforge.pmd.cli.commands.mixins.internal.EncodingMixin;
import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.cli.internal.PmdRootLogger;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer.LanguageFilePattern;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;

public abstract class AbstractAnalysisPmdSubcommand<C extends AbstractConfiguration> extends AbstractPmdSubcommand {

    @Mixin
    protected EncodingMixin encoding;

    // see the setters #setInputPaths and setPositionalInputPaths for @Option and @Parameters annotations
    // Note: can't use annotations on the fields here, as otherwise the complete list would be replaced
    // rather than accumulated.
    private Set<Path> inputPaths;

    @Option(names = "--file-list",
            description =
                "Path to a file containing a list of files to analyze, one path per line. "
                + "One of --dir, --file-list or --uri must be provided.")
    protected Path fileListPath;

    @Option(names = { "--uri", "-u" },
            description = "Database URI for sources. "
                          + "One of --dir, --file-list or --uri must be provided.")
    protected URI uri;

    @Option(names = "--no-fail-on-violation",
            description = "By default PMD exits with status 4 if violations are found. "
                    + "Disable this option with '--no-fail-on-violation' to exit with 0 instead and just write the report.",
            defaultValue = "true", negatable = true)
    protected boolean failOnViolation;

    private List<Path> relativizeRootPaths;

    @Option(names = { "--relativize-paths-with", "-z"}, description = "Path relative to which directories are rendered in the report. "
            + "This option allows shortening directories in the report; "
            + "without it, paths are rendered as mentioned in the source directory (option \"--dir\"). "
            + "The option can be repeated, in which case the shortest relative path will be used. "
            + "If the root path is mentioned (e.g. \"/\" or \"C:\\\"), then the paths will be rendered as absolute.",
            arity = "1..*", split = ",")
    protected void setRelativizePathsWith(List<Path> rootPaths) {
        this.relativizeRootPaths = rootPaths;

        for (Path path : this.relativizeRootPaths) {
            if (Files.isRegularFile(path)) {
                throw new ParameterException(spec.commandLine(),
                        "Expected a directory path for option '--relativize-paths-with', found a file: " + path);
            }
        }
    }

    @Option(names = { "--dir", "-d" },
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

    @Parameters(arity = "*", description = "Path to a source file, or directory containing source files to analyze. "
            + "Equivalent to using --dir.")
    protected void setPositionalInputPaths(final List<Path> inputPaths) {
        this.setInputPaths(inputPaths);
    }

    private Path ignoreListPath;

    @Option(names = "--ignore-list",
        description = "Path to a file containing a list of files to exclude from the analysis, one path per line. "
            + "This option can be combined with --dir, --file-list and --uri.")
    public void setIgnoreListPath(final Path ignoreListPath) {
        this.ignoreListPath = ignoreListPath;
    }

    @Option(names="--assign-language", description = "Use a regex pattern to assign filenames to a language."
        + " Eg `--assign-language html '.*\\.twig'` will recognize files with extension \\.twig and assign them the language HTML."
        + " This only affects language assignment for the files that are mentioned with other options like --dir, it will not search for "
        + " new files outside of these. These patterns take precedence over the default language assignment. If several patterns match,"
        + " only the latest pattern to be mentioned on the CLI will be considered. Note that the regex will be applied on the absolute path"
        + " of the file, with file separators normalized to '/'.", parameterConsumer = LanguageFilePatternConverter.class)
    protected List<LanguageFilePattern> languageFilePatterns = new ArrayList<>();


    @Override
    protected final void validate() throws ParameterException {
        super.validate();

        if ((inputPaths == null || inputPaths.isEmpty()) && uri == null && fileListPath == null) {
            throw new ParameterException(spec.commandLine(),
                                         "Please provide a parameter for source root directory (--dir or -d), "
                                             + "database URI (--uri or -u), or file list path (--file-list)");
        }
    }

    protected void configureCommonOptions(C configuration) {

        // Setup CLI message reporter
        configuration.setReporter(new SimpleMessageReporter(LoggerFactory.getLogger(PmdCommand.class)));

        if (inputPaths != null) {
            configuration.setInputPathList(new ArrayList<>(inputPaths));
        }
        configuration.setInputFilePath(fileListPath);
        configuration.setIgnoreFilePath(ignoreListPath);
        configuration.setInputUri(uri);
        configuration.setSourceEncoding(encoding.getEncoding());
        if (relativizeRootPaths != null) {
            configuration.addRelativizeRoots(relativizeRootPaths);
        }
        for (LanguageFilePattern pat : languageFilePatterns) {
            Language lang = configuration.getLanguageRegistry().getLanguageById(pat.getLanguageid());
            if (lang == null) {
                configuration.getReporter().warn("Language {0} mentioned in --assign-language option is not loaded.", pat.getLanguageid());
            }
            configuration.addLanguageFilePattern(pat);
        }
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

    static class LanguageFilePatternConverter implements CommandLine.IParameterConsumer {

        @Override
        public void consumeParameters(Stack<String> args, CommandLine.Model.ArgSpec argSpec, CommandLine.Model.CommandSpec commandSpec) {
            if (args.size() < 2) {
                throw new ParameterException(commandSpec.commandLine(),
                                             "Expected two arguments for the language file pattern: one language ID and one pattern.");
            }
            String langId = args.pop();
            String globString = args.pop();
            Pattern pattern;
            try {
                pattern = Pattern.compile(globString);
            } catch (PatternSyntaxException pse) {
                throw new ParameterException(commandSpec.commandLine(), "Invalid regex specification for language " + langId + ": " + pse.getMessage());
            }
            ((List<LanguageFilePattern>) argSpec.getValue()).add(LanguageFilePattern.ofRegex(pattern, langId));
        }
    }
}
