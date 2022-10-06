/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.cli.commands.mixins.internal.EncodingMixin;
import net.sourceforge.pmd.cli.commands.typesupport.internal.PmdLanguageTypeSupport;
import net.sourceforge.pmd.cli.internal.ExecutionResult;
import net.sourceforge.pmd.internal.LogMessages;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;
import net.sourceforge.pmd.util.treeexport.TreeExportConfiguration;
import net.sourceforge.pmd.util.treeexport.TreeExporter;
import net.sourceforge.pmd.util.treeexport.TreeRendererDescriptor;
import net.sourceforge.pmd.util.treeexport.TreeRenderers;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

@Command(name = "ast-dump", description = "Experimental: dumps the AST of parsing source code")
public class TreeExportCommand extends AbstractPmdSubcommand {

    static {
        final StringBuilder reportPropertiesHelp = new StringBuilder();
        final String lineSeparator = System.lineSeparator();
        
        for (final TreeRendererDescriptor renderer : TreeRenderers.registeredRenderers()) {
            final PropertySource propertyBundle = renderer.newPropertyBundle();
            if (!propertyBundle.getPropertyDescriptors().isEmpty()) {
                reportPropertiesHelp.append(renderer.id() + ":" + lineSeparator);
                for (final PropertyDescriptor<?> property : propertyBundle.getPropertyDescriptors()) {
                    reportPropertiesHelp.append("  ").append(property.name()).append(" - ")
                        .append(property.description()).append(lineSeparator);
                    final Object deflt = property.defaultValue();
                    if (deflt != null && !"".equals(deflt)) {
                        reportPropertiesHelp.append("    Default: ").append(StringUtil.escapeWhitespace(deflt))
                            .append(lineSeparator);
                    }
                }
            }
        }
        
        // System Properties are the easier way to inject dynamically computed values into the help of an option
        System.setProperty("pmd-cli.tree-export.report.properties.help", reportPropertiesHelp.toString());
    }
    
    @Mixin
    private EncodingMixin encoding;
    
    @Option(names = { "--format", "-f" }, defaultValue = "xml",
            description = "The output format.%nValid values: ${COMPLETION-CANDIDATES}",
            completionCandidates = TreeRenderersCandidates.class)
    private String format;

    @Option(names = { "--language", "-l" }, defaultValue = "java",
            description = "The source code language.%nValid values: ${COMPLETION-CANDIDATES}",
            completionCandidates = PmdLanguageTypeSupport.class, converter = PmdLanguageTypeSupport.class)
    private Language language;

    @Option(names = "-P", description = "Key-value pair defining a property for the report format.%n"
            + "Supported values for each report format:%n${sys:pmd-cli.tree-export.report.properties.help}",
            completionCandidates = TreeExportReportPropertiesCandidates.class)
    private Properties properties;

    @Option(names = "--file", description = "The file to parse and dump.")
    private Path file;

    @Option(names = { "--read-stdin", "-i" }, description = "Read source from standard input.")
    private boolean readStdin;

    public TreeExportConfiguration toConfiguration() {
        final TreeExportConfiguration configuration = new TreeExportConfiguration();
        configuration.setDebug(debug);
        configuration.setFile(file);
        configuration.setFormat(format);
        configuration.setLanguage(language);
        configuration.setProperties(properties);
        configuration.setReadStdin(readStdin);
        configuration.setSourceEncoding(encoding.getEncoding().name());
        
        return configuration;
    }
    
    @Override
    protected void validate() throws ParameterException {
        super.validate();
        
        if (file == null && !readStdin) {
            throw new ParameterException(spec.commandLine(), "One of --file or --read-stdin must be used.");
        }
    }
    
    @Override
    protected ExecutionResult execute() {
        final TreeExporter exporter = new TreeExporter(toConfiguration());
        try {
            exporter.export();
            return ExecutionResult.OK;
        } catch (final IOException e) {
            final SimpleMessageReporter reporter = new SimpleMessageReporter(LoggerFactory.getLogger(TreeExportCommand.class));
            reporter.error(e, LogMessages.errorDetectedMessage(1, "ast-dump"));
            
            return ExecutionResult.ERROR;
        }
    }

    /**
     * Provides completion candidates for the report format.
     */
    private static final class TreeRenderersCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            return TreeRenderers.registeredRenderers().stream().map(TreeRendererDescriptor::id).iterator();
        }
    }
    
    /**
     * Provider of candidates for valid report properties.
     * 
     * Check the help for which ones are supported by each report format and possible values.
     */
    private static final class TreeExportReportPropertiesCandidates implements Iterable<String> {

        @Override
        public Iterator<String> iterator() {
            final List<String> propertyNames = new ArrayList<>();
            for (final TreeRendererDescriptor renderer : TreeRenderers.registeredRenderers()) {
                final PropertySource propertyBundle = renderer.newPropertyBundle();
                
                for (final PropertyDescriptor<?> property : propertyBundle.getPropertyDescriptors()) {
                    propertyNames.add(property.name());
                }
            }
            return propertyNames.iterator();
        }
    }
}
