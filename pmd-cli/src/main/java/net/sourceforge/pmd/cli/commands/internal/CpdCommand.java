/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.cli.commands.typesupport.internal.CpdLanguageTypeSupport;
import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CPDReport;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.util.IOUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

@Command(name = "cpd", showDefaultValues = true,
    description = "Copy/Paste Detector - find duplicate code")
public class CpdCommand extends AbstractAnalysisPmdSubcommand {

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

    @Option(names = "--skip-lexical-errors",
            description = "Skip files which can't be tokenized due to invalid characters, instead of aborting with an error.")
    private boolean skipLexicalErrors;

    @Option(names = "--no-skip-blocks",
            description = "Do not skip code blocks marked with --skip-blocks-pattern (e.g. #if 0 until #endif).")
    private boolean noSkipBlocks;

    @Option(names = "--skip-blocks-pattern",
            description = "Pattern to find the blocks to skip. Start and End pattern separated by |.",
            defaultValue = Tokenizer.DEFAULT_SKIP_BLOCKS_PATTERN)
    private String skipBlocksPattern;

    @Option(names = "--exclude", arity = "1..*", description = "Files to be excluded from the analysis")
    private List<File> excludes;

    @Option(names = "--non-recursive", description = "Don't scan subdirectiories.")
    private boolean nonRecursive;

    /**
     * Converts these parameters into a configuration.
     *
     * @return A new CPDConfiguration corresponding to these parameters
     *
     * @throws ParameterException if the parameters are inconsistent or incomplete
     */
    public CPDConfiguration toConfiguration() {
        final CPDConfiguration configuration = new CPDConfiguration();
        configuration.setDebug(debug);
        configuration.setExcludes(excludes);
        configuration.setFailOnViolation(failOnViolation);
        configuration.setFileListPath(fileListPath == null ? null : fileListPath.toString());
        configuration.setFiles(inputPaths == null ? null : inputPaths.stream().map(Path::toFile).collect(Collectors.toList()));
        configuration.setIgnoreAnnotations(ignoreAnnotations);
        configuration.setIgnoreIdentifiers(ignoreIdentifiers);
        configuration.setIgnoreLiterals(ignoreLiterals);
        configuration.setIgnoreLiteralSequences(ignoreLiteralSequences);
        configuration.setIgnoreUsings(ignoreUsings);
        configuration.setLanguage(language);
        configuration.setMinimumTileSize(minimumTokens);
        configuration.setNonRecursive(nonRecursive);
        configuration.setNoSkipBlocks(noSkipBlocks);
        configuration.setRendererName(rendererName);
        configuration.setSkipBlocksPattern(skipBlocksPattern);
        configuration.setSkipDuplicates(skipDuplicates);
        configuration.setSkipLexicalErrors(skipLexicalErrors);
        configuration.setSourceEncoding(encoding.getEncoding().name());
        configuration.setURI(uri == null ? null : uri.toString());

        configuration.postContruct();
        // Pass extra parameters as System properties to allow language
        // implementation to retrieve their associate values...
        CPDConfiguration.setSystemProperties(configuration);

        return configuration;
    }

    @Override
    protected ExecutionResult execute() {
        final Logger logger = LoggerFactory.getLogger(CpdCommand.class);
        
        // TODO : Create a new CpdAnalysis to match PmdAnalysis
        final CPDConfiguration configuration = toConfiguration();
        final CPD cpd = new CPD(configuration);

        try {
            cpd.go();

            final CPDReport report = cpd.toReport();
            configuration.getCPDReportRenderer().render(report, IOUtil.createWriter(Charset.defaultCharset(), null));

            if (cpd.getMatches().hasNext() && configuration.isFailOnViolation()) {
                return ExecutionResult.VIOLATIONS_FOUND;
            }
        } catch (IOException | RuntimeException e) {
            logger.debug(e.toString(), e);
            logger.error(LogMessages.errorDetectedMessage(1, "cpd"));
            return ExecutionResult.ERROR;
        }

        return ExecutionResult.OK;
    }

    /**
     * Provider of candidates for valid report formats.
     */
    private static final class CpdSupportedReportFormatsCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return Arrays.stream(CPDConfiguration.getRenderers()).iterator();
        }
    }
}
