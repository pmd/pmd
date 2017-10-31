/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ruledef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Aggregates several rule definition files into a single one.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class RuleDefsAggregator {

    public static final String OUTPUT_NAMESPACE = "http://pmd.sourceforge.net/ruleset/3.0.0";
    public static final String OUTPUT_XSD_LOCATION = "http://pmd.sourceforge.net/ruleset_3_0_0.xsd";
    private static final String RULEDEFS_PREFIX_LOCATION = "ruledefs/";


    // Lists the ruledefs files in the directory, removing the excludedFiles
    private List<File> listRuledefs(Path ruledefsDir, List<String> exludedFiles) throws IOException {
        List<File> filenames = new ArrayList<>();

        File dir = ruledefsDir.toFile();
        File[] contents = dir.listFiles();

        if (contents == null) {
            throw new IllegalArgumentException("The path " + ruledefsDir.toAbsolutePath()
                                                   + " is not a directory.");
        }

        for (File file : contents) {
            if (file.isFile() && file.getName().endsWith(".xml")
                && !exludedFiles.contains(file.getName())) {
                filenames.add(file);
            }
        }

        return filenames;
    }


    // Append all rule elements found in the file to the output document
    private void appendRules(File ruledefFile, Document output)
        throws ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

        try (InputStream stream = new FileInputStream(ruledefFile)) {


            Document document = builder.parse(stream);
            Element rulesetElement = document.getDocumentElement();
            Element outputDocumentElement = output.getDocumentElement();

            NodeList rules = rulesetElement.getElementsByTagName("rule");

            for (int i = 0; i < rules.getLength(); i++) {

                Node clone = rules.item(i).cloneNode(true);
                output.adoptNode(clone);
                outputDocumentElement.appendChild(clone);
                migrateNamespace(output, clone);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // migrates the namespace of the parsed dom to the OUTPUT_NAMESPACE
    private void migrateNamespace(Document doc, Node n) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            doc.renameNode(n, OUTPUT_NAMESPACE, n.getNodeName());
        }

        Node child = n.getFirstChild();
        while (child != null) {
            migrateNamespace(doc, child);
            child = child.getNextSibling();
        }
    }


    // Initialises the output document
    private Document createOutputDocument(String langName) throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document output = factory.newDocumentBuilder().getDOMImplementation().createDocument(
            OUTPUT_NAMESPACE,
            "ruleset",
            null
        );

        Element rulesetElement = output.getDocumentElement();
        rulesetElement.setAttribute("name", langName.toUpperCase());

        rulesetElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                                      "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rulesetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                                      "xsi:schemaLocation",
                                      OUTPUT_NAMESPACE + " " + OUTPUT_XSD_LOCATION);


        Element description = output.createElementNS(OUTPUT_NAMESPACE, "description");
        description.setTextContent("All rules for the " + langName + " language");

        rulesetElement.appendChild(description);

        return output;
    }


    // writes the document into the file
    private void writeDocument(Document doc, Writer outputWriter) throws IOException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Source source = new DOMSource(doc);
        Result result = new StreamResult(outputWriter);
        transformer.transform(source, result);
    }


    /**
     * Generates the rule index.
     *
     * @param ruledefsDir   Path to the directory where the ruledef files sit
     * @param excludedFiles List of files to exclude
     * @param outputWriter  Writer for the output document
     */
    void generateMasterRuledefFrom(Path ruledefsDir, List<String> excludedFiles, Writer outputWriter)
        throws IOException, ParserConfigurationException, SAXException, TransformerException {

        Document output = createOutputDocument(ruledefsDir.getFileName().toString());

        for (File ruledefFile : listRuledefs(ruledefsDir, excludedFiles)) {
            appendRules(ruledefFile, output);
        }

        writeDocument(output, outputWriter);
    }


    /**
     * Gets the name of the resource path to a language's master ruledef.
     *
     * @param langname Name of the language
     *
     * @return The name of the resource path, suitable for use within getResource(String)
     */
    public static String getGeneratedResourceFileName(String langname) {
        return RULEDEFS_PREFIX_LOCATION + langname + "-index.xml";
    }


    private static List<String> parseExcludeArgument(String s) {
        if (s.matches("exclude:\\w+\\.xml(,\\w+\\.xml)*")) {
            return Arrays.asList(s.split("exclude:")[1].split(","));
        } else {
            throw new IllegalArgumentException("Incorrect exclusion pattern: \"" + s + "\"");
        }
    }

    // @formatter:off
    /**
     * Creates the rule definition index for a language.
     *
     * <p>Parameters:
     * <ul>
     * <li> args[0] (required): path to the base directory of the language module
     * <li> args[1] (required): name of the language, e.g. "java", "ecmascript"
     * <li> args[2] (optional): list of ruledef files to exclude, in the format {@literal "exclude:<filename>.xml(,<filename>.xml)*"}
     * </ul>
     *
     * @param args Arguments
     */
    // @formatter:on
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected the module's base directory and language");
        }

        String langName = args[1];

        Path resourcesDir = FileSystems.getDefault().getPath(args[0]).resolve("src/main/resources/");
        Path ruledefsDir = resourcesDir.resolve(RULEDEFS_PREFIX_LOCATION + langName).toAbsolutePath().normalize();
        Path outputFilePath = resourcesDir.resolve(getGeneratedResourceFileName(langName)).toAbsolutePath().normalize();

        List<String> excludes = (args.length > 2) ? parseExcludeArgument(args[2]) : Collections.<String>emptyList();

        try {
            System.out.println("Aggregating " + args[1] + " ruledefs into " + outputFilePath + " ...");
            long start = System.currentTimeMillis();
            new RuleDefsAggregator().generateMasterRuledefFrom(ruledefsDir, excludes,
                                                               new FileWriter(outputFilePath.toFile()));
            System.out.println("Done in " + (System.currentTimeMillis() - start) + " ms");

        } catch (TransformerException | SAXException | ParserConfigurationException | IOException e) {
            System.err.println("Failed to generate the ruledef index with parameters:");
            System.err.println("\tModule basedir: " + args[0]);
            System.err.println("\tLanguage name: " + args[1]);
            System.err.println("\tExcluded files: " + ((args.length > 2) ? args[2] : "(not provided)"));
            System.err.println("Cause: ");
            e.printStackTrace();
        }
    }
}
