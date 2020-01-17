/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertySource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

@Experimental
public class TreeExportCli {
    @Parameter(names = { "--format", "-f" }, description = "The output format.", required = true)
    private String format;
    @Parameter(names = { "--language", "-l" }, description = "Specify the language to use.", required = true)
    private String language;
    @Parameter(names = { "--encoding", "-e" }, description = "Encoding of the source file.")
    private String encoding = StandardCharsets.UTF_8.name();

    @Parameter(names = { "--help", "-h" }, description = "Display usage.", help = true)
    private boolean help;

    @Parameter(description = "The file to dump", required = true)
    private String file;

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
        cli.run();
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
            sb.append(t.id()).append(' ');
        }
        sb.append(System.lineSeparator())
            .append(System.lineSeparator());

        sb.append("Example: ast-dump --format xml --language java MyFile.java")
            .append(System.lineSeparator());

        System.err.print(sb);
    }

    private void printWarning() {
        System.err.println("-------------------------------------------------------------------------------");
        System.err.println("This command line utility is experimental. It might change at any time without");
        System.err.println("prior notice.");
        System.err.println("-------------------------------------------------------------------------------");
    }

    private void run() throws IOException {
        printWarning();

        LanguageVersionHandler languageHandler = LanguageRegistry.findLanguageByTerseName(language)
                .getDefaultVersion().getLanguageVersionHandler();
        Parser parser = languageHandler.getParser(languageHandler.getDefaultParserOptions());

        try (Reader reader = Files.newBufferedReader(new File(file).toPath(), Charset.forName(encoding))) {
            Node root = parser.parse(file, reader);
            languageHandler.getQualifiedNameResolutionFacade(this.getClass().getClassLoader()).start(root);

            TreeRendererDescriptor treeRenderer = TreeRenderers.findById(format);
            PropertySource bundle = treeRenderer.newPropertyBundle();
            treeRenderer.produceRenderer(bundle).renderSubtree(root, System.out);
        }
    }
}
