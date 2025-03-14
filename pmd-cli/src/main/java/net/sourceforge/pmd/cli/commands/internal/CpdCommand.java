/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.cli.commands.typesupport.internal.CpdLanguageTypeSupport;
import net.sourceforge.pmd.cli.internal.CliExitCode;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CpdAnalysis;
import net.sourceforge.pmd.cpd.internal.CpdLanguagePropertiesDefaults;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.util.StringUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

@Command(name = "cpd", showDefaultValues = true,
    description = "Copy/Paste Detector - find duplicate code")
public class CpdCommand extends AbstractAnalysisPmdSubcommand<CPDConfiguration> {

    @Option(names = { "--language", "-l" }, description = "The source code language.%nValid values: ${COMPLETION-CANDIDATES}",
            defaultValue = CPDConfiguration.DEFAULT_LANGUAGE, converter = CpdLanguageTypeSupport.class, completionCandidates = CpdLanguageTypeSupport.class)
    private Language language;

    @Option(names = "--minimum-tokens",
            description = "The minimum token length which should be reported as a duplicate.", required = true)
    private int minimumTokens;

    @Option(names = "--skip-duplicate-files",
            description = "Ignore multiple copies of files of the same name and length in comparison.")
    private boolean skipDuplicates;

    @Option(names = { "--format", "-f" },
            description = "Report format.%nValid values: ${COMPLETION-CANDIDATES}%n"
                        + "Alternatively, you can provide the fully qualified name of a custom CpdRenderer in the classpath.",
            defaultValue = CPDConfiguration.DEFAULT_RENDERER, completionCandidates = CpdSupportedReportFormatsCandidates.class)
    private String rendererName;

    @Option(names = "--ignore-literals",
            description = "Ignore literal values such as numbers and strings when comparing text.")
    private boolean ignoreLiterals;

    @Option(names = "--ignore-identifiers",
            description = "Ignore names of classes, methods, variables, constants, etc. when comparing text.")
    private boolean ignoreIdentifiers;

    @Option(names = "--ignore-annotations", description = "Ignore language annotations when comparing text.")
    private boolean ignoreAnnotations;

    @Option(names = "--ignore-usings", description = "Ignore using directives in C#")
    private boolean ignoreUsings;

    @Option(names = "--ignore-literal-sequences", description = "Ignore sequences of literals such as list initializers.")
    private boolean ignoreLiteralSequences;

    @Option(names = "--ignore-sequences", description = "Ignore sequences of identifiers and literals")
    private boolean ignoreIdentifierAndLiteralSequences;

    /**
     * @deprecated Use {@link #failOnError} instead.
     */
    @Option(names = "--skip-lexical-errors",
            description = "Skip files which can't be tokenized due to invalid characters, instead of aborting with an error. Deprecated - use --[no-]fail-on-error instead.")
    @Deprecated
    private boolean skipLexicalErrors;

    @Option(names = "--no-skip-blocks",
            description = "Do not skip code blocks marked with --skip-blocks-pattern (e.g. #if 0 until #endif).")
    private boolean noSkipBlocks;

    @Option(names = "--skip-blocks-pattern",
            description = "Pattern to find the blocks to skip. Start and End pattern separated by |.",
            defaultValue = CpdLanguagePropertiesDefaults.DEFAULT_SKIP_BLOCKS_PATTERN)
    private String skipBlocksPattern;

    @Option(names = "--exclude", arity = "1..*", description = "Files to be excluded from the analysis")
    private List<Path> excludes = new ArrayList<>();

    @Option(names = "--non-recursive", description = "Don't scan subdirectiories.")
    private boolean nonRecursive;


    /**
     * Converts these parameters into a configuration.
     *
     * @return A new CPDConfiguration corresponding to these parameters
     *
     * @throws ParameterException if the parameters are inconsistent or incomplete
     */
    @Override
    protected CPDConfiguration toConfiguration() {
        final CPDConfiguration configuration = new CPDConfiguration();
        configuration.setExcludes(excludes);
        if (relativizeRootPaths != null) {
            configuration.addRelativizeRoots(relativizeRootPaths);
        }
        configuration.setFailOnViolation(failOnViolation);
        configuration.setFailOnError(failOnError);
        configuration.setInputFilePath(fileListPath);
        if (inputPaths != null) {
            configuration.setInputPathList(new ArrayList<>(inputPaths));
        }
        configuration.setIgnoreAnnotations(ignoreAnnotations);
        configuration.setIgnoreIdentifiers(ignoreIdentifiers);
        configuration.setIgnoreLiterals(ignoreLiterals);
        configuration.setIgnoreLiteralSequences(ignoreLiteralSequences);
        configuration.setIgnoreUsings(ignoreUsings);
        configuration.setOnlyRecognizeLanguage(language);
        configuration.setMinimumTileSize(minimumTokens);
        configuration.collectFilesRecursively(!nonRecursive);
        configuration.setNoSkipBlocks(noSkipBlocks);
        configuration.setRendererName(rendererName);
        configuration.setSkipBlocksPattern(skipBlocksPattern);
        configuration.setSkipDuplicates(skipDuplicates);
        configuration.setSourceEncoding(encoding.getEncoding());
        configuration.setInputUri(uri);

        if (skipLexicalErrors) {
            configuration.getReporter().warn("--skip-lexical-errors is deprecated. Use --no-fail-on-error instead.");
            configuration.setFailOnError(false);
        }

        return configuration;
    }

    @Override
    protected @NonNull CliExitCode doExecute(CPDConfiguration configuration) {
        try (CpdAnalysis cpd = CpdAnalysis.create(configuration)) {

            MutableBoolean hasViolations = new MutableBoolean();
            cpd.performAnalysis(report -> hasViolations.setValue(!report.getMatches().isEmpty()));

            boolean hasErrors = configuration.getReporter().numErrors() > 0;
            if (hasErrors && configuration.isFailOnError()) {
                return CliExitCode.RECOVERED_ERRORS_OR_VIOLATIONS;
            }

            if (hasViolations.booleanValue() && configuration.isFailOnViolation()) {
                return CliExitCode.VIOLATIONS_FOUND;
            }
        } catch (IOException | RuntimeException e) {
            configuration.getReporter().errorEx("Exception while running CPD.", e);
            configuration.getReporter().info(StringUtil.quoteMessageFormat(LogMessages.errorDetectedMessage(1, "cpd")));
            return CliExitCode.ERROR;
        }

        return CliExitCode.OK;
    }

    /**
     * Provider of candidates for valid report formats.
     */
    private static final class CpdSupportedReportFormatsCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return CPDConfiguration.getRenderers().stream().sorted().iterator();
        }
    }
}
