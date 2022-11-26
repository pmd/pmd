/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

@Deprecated
@Experimental
public class TreeExportCli {

    @Parameter(names = { "--format", "-f" }, description = "The output format.")
    private String format = "xml";
    @Parameter(names = { "--language", "-l" }, description = "Specify the language to use.", required = true)
    private @Nullable String language = null;
    @Parameter(names = { "--encoding", "-e" }, description = "Encoding of the source file.")
    private String encoding = StandardCharsets.UTF_8.name();
    @DynamicParameter(names = "-P", description = "Properties for the renderer.")
    private Map<String, String> properties = new HashMap<>();
    @Parameter(names = { "--help", "-h" }, description = "Display usage.", help = true)
    private boolean help;
    @Parameter(names = "--file", description = "The file to dump")
    private String file;
    @Parameter(names = { "--read-stdin", "-i" }, description = "Read source from standard input")
    private boolean readStdin;

    private final Io io;

    TreeExportCli(Io io) {
        this.io = io;
    }

    public static void main(String... args) {
        TreeExportCli cli = new TreeExportCli(Io.system());
        System.exit(cli.runMain(args));
    }

    public int runMain(String... args) {
        try {
            return runMainOrThrow(args);
        } catch (RuntimeException unused) {
            return 1;
        } catch (IOException e) {
            io.stderr.println("Error: " + e);
            e.printStackTrace(io.stderr);
            return 1;
        }
    }

    private int runMainOrThrow(String[] args) throws IOException {

        JCommander jcommander = new JCommander(this);

        try {
            jcommander.parse(args);
        } catch (ParameterException e) {
            io.stderr.println(e.getMessage());
            usage(jcommander);
            return 1;
        }

        if (help) {
            usage(jcommander);
            return 0;
        }

        TreeExportConfiguration configuration = new TreeExportConfiguration();
        configuration.setFile(file == null ? null : Paths.get(file));
        configuration.setFormat(format);
        configuration.setLanguage(LanguageRegistry.findLanguageByTerseName(language));
        configuration.setReadStdin(readStdin);
        configuration.setSourceEncoding(encoding);
        
        Properties props = new Properties();
        props.putAll(this.properties);
        configuration.setProperties(props);

        TreeExporter exporter = new TreeExporter(configuration, io);
        exporter.export();
        
        return 0;
    }

    private void usage(JCommander commander) {
        StringBuilder sb = new StringBuilder();
        commander.setProgramName("ast-dump");
        commander.usage(sb);
        sb.append(System.lineSeparator());

        sb.append("Available languages: ");
        for (Language l : LanguageRegistry.PMD) {
            sb.append(l.getTerseName()).append(' ');
        }
        sb.append(System.lineSeparator());
        sb.append("Available formats: ");
        for (TreeRendererDescriptor t : TreeRenderers.registeredRenderers()) {
            describeRenderer(30, t, sb);
        }
        sb.append(System.lineSeparator())
            .append(System.lineSeparator());

        sb.append("Example: ast-dump --format xml --language java --file MyFile.java")
            .append(System.lineSeparator());

        System.err.print(sb);
    }

    private void describeRenderer(int marginWidth, TreeRendererDescriptor descriptor, StringBuilder sb) {


        sb.append(String.format("%-" + marginWidth + "s%s", descriptor.id(), descriptor.description()))
            .append(System.lineSeparator());

        List<PropertyDescriptor<?>> props = descriptor.newPropertyBundle().getPropertyDescriptors();

        if (!props.isEmpty()) {

            sb.append(String.format("%-" + marginWidth + "s", "+ Properties"))
                .append(System.lineSeparator());

            for (PropertyDescriptor<?> prop : props) {
                sb.append(String.format(
                    "  + %-" + marginWidth + "s%s %s",
                    prop.name(), prop.description(), "(default " + getDefault(prop) + ")"))
                    .append(System.lineSeparator());
            }
        } else {
            sb.append(System.lineSeparator());
        }
    }

    private <T> String getDefault(PropertyDescriptor<T> prop) {
        return StringEscapeUtils.escapeJava(prop.asDelimitedString(prop.defaultValue()));
    }
}
