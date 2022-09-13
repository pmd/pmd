/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

@Experimental
public class TreeExportCli {

    @Parameter(names = { "--format", "-f" }, description = "The output format.")
    private String format = "xml";
    @Parameter(names = { "--language", "-l" }, description = "Specify the language to use.")
    private String language = LanguageRegistry.getDefaultLanguage().getTerseName();
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
        TreeExportCli cli = new TreeExportCli(Io.SYSTEM);
        System.exit(cli.runMain(args));
    }

    public int runMain(String... args) {
        try {
            return runMainOrThrow(args);
        } catch (AbortedError e) {
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


        TreeRendererDescriptor descriptor = TreeRenderers.findById(this.format);
        if (descriptor == null) {
            throw this.bail("Unknown format '" + this.format + "'");
        }

        PropertySource bundle = parseProperties(this, descriptor);

        run(descriptor.produceRenderer(bundle));
        return 0;
    }

    public static PropertySource parseProperties(TreeExportCli cli, TreeRendererDescriptor descriptor) {
        PropertySource bundle = descriptor.newPropertyBundle();

        for (String key : cli.properties.keySet()) {
            PropertyDescriptor<?> d = bundle.getPropertyDescriptor(key);
            if (d == null) {
                throw cli.bail("Unknown property '" + key + "'");
            }

            setProperty(d, bundle, cli.properties.get(key));
        }
        return bundle;
    }


    private void usage(JCommander commander) {
        StringBuilder sb = new StringBuilder();
        commander.setProgramName("ast-dump");
        commander.usage(sb);
        sb.append(System.lineSeparator());

        sb.append("Available languages: ");
        for (Language l : LanguageRegistry.getLanguages()) {
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

    private void run(TreeRenderer renderer) throws IOException {
        printWarning();

        LanguageVersionHandler languageHandler = LanguageRegistry.findLanguageByTerseName(language)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = languageHandler.getParser(languageHandler.getDefaultParserOptions());

        @SuppressWarnings("PMD.CloseResource")
        Reader source;
        String fileName;
        if (file == null && !readStdin) {
            throw bail("One of --file or --read-stdin must be mentioned");
        } else if (readStdin) {
            io.stderr.println("Reading from stdin...");
            source = readFromSystemIn();
            fileName = "stdin";
        } else {
            source = Files.newBufferedReader(new File(file).toPath(), Charset.forName(encoding));
            fileName = file;
        }

        // disable warnings for deprecated attributes
        Logger.getLogger(Attribute.class.getName()).setLevel(Level.OFF);

        try (Reader reader = source) {
            Node root = AbstractParser.doParse(parser, fileName, reader);
            languageHandler.getQualifiedNameResolutionFacade(this.getClass().getClassLoader()).start(root);

            renderer.renderSubtree(root, io.stdout);
        }
    }

    private Reader readFromSystemIn() {
        return new BufferedReader(new InputStreamReader(io.stdin));
    }

    private void printWarning() {
        io.stderr.println("-------------------------------------------------------------------------------");
        io.stderr.println("This command line utility is experimental. It might change at any time without");
        io.stderr.println("prior notice.");
        io.stderr.println("-------------------------------------------------------------------------------");
    }

    private static <T> void setProperty(PropertyDescriptor<T> descriptor, PropertySource bundle, String value) {
        bundle.setProperty(descriptor, descriptor.valueFrom(value));
    }


    private AbortedError bail(String message) {
        io.stderr.println(message);
        io.stderr.println("Use --help for usage information");
        return new AbortedError();
    }

    private static final class AbortedError extends Error {

    }
}
