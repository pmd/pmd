/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import net.sourceforge.pmd.annotation.Experimental;
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
    @DynamicParameter(names = {"-P"}, description = "Properties for the renderer.")
    private Map<String, String> properties = new HashMap<>();

    @Parameter(names = { "--help", "-h" }, description = "Display usage.", help = true)
    private boolean help;

    @Parameter(names = { "--file" }, description = "The file to dump")
    private String file;

    @Parameter(names = { "--read-stdin", "-i" }, description = "Read source from standard input")
    private boolean readStdin;


    public static void main(String[] args) throws IOException {
        TreeExportCli cli = new TreeExportCli();
        JCommander jcommander = JCommander.newBuilder()
                                          .addObject(cli)
                                          .build();
        try {
            jcommander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            cli.usage(jcommander);
            System.exit(1);
        }

        if (cli.help) {
            cli.usage(jcommander);
            System.exit(0);
        }


        TreeRendererDescriptor descriptor = TreeRenderers.findById(cli.format);
        if (descriptor == null) {
            throw cli.bail("Unknown format '" + cli.format + "'");
        }

        PropertySource bundle = parseProperties(cli, descriptor);

        cli.run(descriptor.produceRenderer(bundle));
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

        sb.append("Example: ast-dump --format xml --language java MyFile.java")
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
        if (file == null && !readStdin) {
            throw bail("One of --file or --read-stdin must be mentioned");
        } else if (readStdin) {
            System.err.println("Reading from stdin...");
            source = new StringReader(readFromSystemIn());
        } else {
            source = Files.newBufferedReader(new File(file).toPath(), Charset.forName(encoding));
        }

        // disable warnings for deprecated attributes
        Logger.getLogger(Attribute.class.getName()).setLevel(Level.OFF);

        try (Reader reader = source) {
            Node root = parser.parse(file, reader);
            languageHandler.getQualifiedNameResolutionFacade(this.getClass().getClassLoader()).start(root);

            renderer.renderSubtree(root, System.out);
        }
    }

    private String readFromSystemIn() {


        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(new CloseShieldInputStream(System.in))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
        }
        return sb.toString();

    }

    private void printWarning() {
        System.err.println("-------------------------------------------------------------------------------");
        System.err.println("This command line utility is experimental. It might change at any time without");
        System.err.println("prior notice.");
        System.err.println("-------------------------------------------------------------------------------");
    }

    private static <T> void setProperty(PropertyDescriptor<T> descriptor, PropertySource bundle, String value) {
        bundle.setProperty(descriptor, descriptor.valueFrom(value));
    }


    private RuntimeException bail(String message) {
        System.err.println(message);
        System.err.println("Use --help for usage information");
        System.exit(1);
        return new RuntimeException();
    }
}
