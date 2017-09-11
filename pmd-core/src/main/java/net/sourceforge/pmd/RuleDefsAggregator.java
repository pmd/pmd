/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
 * @author Clément Fournier
 * @since 6.0.0
 */
public class RuleDefsAggregator {

    private static final String OUTPUT_NAMESPACE = "http://pmd.sourceforge.net/ruleset/3.0.0";
    private static final String OUTPUT_XSD_LOCATION = "http://pmd.sourceforge.net/ruleset_3_0_0.xsd";


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


    private void appendRules(File ruledefFile, Document output)
        throws ParserConfigurationException, SAXException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try (InputStream stream = new FileInputStream(ruledefFile)) {


            Document document = builder.parse(stream);
            Element rulesetElement = document.getDocumentElement();
            Element outputDocumentElement = output.getDocumentElement();

            NodeList rules = rulesetElement.getElementsByTagName("rule");

            for (int i = 0; i < rules.getLength(); i++) {

                Node clone = rules.item(i).cloneNode(true);
                output.adoptNode(clone);
                outputDocumentElement.appendChild(clone);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateMasterRuledefFrom(Path ruledefsDir, List<String> exludedFiles, Path outputPath)
        throws IOException, ParserConfigurationException, SAXException, TransformerException {

        Document output = createOutputDocument(ruledefsDir.getFileName().toString());

        for (File ruledefFile : listRuledefs(ruledefsDir, exludedFiles)) {
            appendRules(ruledefFile, output);
        }

        writeDocument(output, outputPath);
    }


    private Document createOutputDocument(String langName) throws ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document output = documentBuilder.newDocument();

        Element rulesetElement = output.createElementNS(OUTPUT_NAMESPACE, "ruleset");
        rulesetElement.setAttribute("name", langName.toUpperCase());

        rulesetElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                                      "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        rulesetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                                      "xsi:schemaLocation",
                                      OUTPUT_NAMESPACE + " " + OUTPUT_XSD_LOCATION);

        Element description = output.createElement("description");
        description.setTextContent("All rules for the " + langName + " language");

        rulesetElement.appendChild(description);
        output.appendChild(rulesetElement);

        return output;
    }


    private void writeDocument(Document doc, Path outputPath) throws IOException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Source source = new DOMSource(doc);
        Result result = new StreamResult(new FileWriter(outputPath.toFile()));
        transformer.transform(source, result);
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected the module's base directory and language");
        }

        String ruledefs = "src/main/resources/ruledefs/" + args[1];

        Path ruledefsDir = FileSystems.getDefault()
                                      .getPath(args[0])
                                      .resolve(ruledefs)
                                      .toAbsolutePath()
                                      .normalize();

        Path outputPath = FileSystems.getDefault()
                                     .getPath(args[0])
                                     .resolve(ruledefs + ".xml")
                                     .toAbsolutePath()
                                     .normalize();

        List<String> excludes = Collections.emptyList();
        if (args.length > 2) {
            excludes = Arrays.asList(args[2].split("exclude:")[1]
                                         .split(","));
        }

        try {
            new RuleDefsAggregator().generateMasterRuledefFrom(ruledefsDir, excludes, outputPath);
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
