/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A small class to convert files from pmd rulesets fmt to xdoc fmt
 *	// FUTURE: This object is fat !
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class RuleSetToDocs implements PmdBuildTools {

    private static final String RULESET_TO_DOC = "tool/xslt/rule-format.xsl";
    private static final String MERGE_RULESET = "tool/xslt/merge-rulesets.xsl";
    private static final String GENERATE_INDEX = "tool/xslt/rules-index.xsl";
    private static final String POM4JAVA4 = "tool/xslt/generate-pom-jdk4.xsl";

    private static final String INDEX_RULESET_FILE = "index.xml";
    private static final String MERGED_RULESET_FILE = "mergedruleset.xml";

    private String rulesDirectory;
    private String targetDirectory;

    Transformer transformer;
    private File target;

    /**
     * Default (empty) constructor:
     */
    public RuleSetToDocs() {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#getRulesDirectory()
     */
    public String getRulesDirectory() {
	return rulesDirectory;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#setRulesDirectory(java.lang.String)
     */
    public void setRulesDirectory(String rulesDirectory) {
	this.rulesDirectory = rulesDirectory;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#convertRulesets()
     */
    public void convertRulesets() throws PmdBuildException {
	init();
	File rulesDir = new File(rulesDirectory);
	if ( rulesDir.exists() && rulesDir.isDirectory() ) {
	    File[] rulesets = rulesDir.listFiles(new RulesetFilenameFilter());
	    for (int fileIterator = 0; fileIterator < rulesets.length; fileIterator++ )
	    {
		File ruleset = rulesets[fileIterator];
		System.out.println("Processing file " + ruleset);
		try {
		    convertRuleSet(ruleset);
		} catch (ParserConfigurationException e) {
		    throw new PmdBuildException(e);
		} catch (SAXException e) {
		    throw new PmdBuildException(e);
		} catch (IOException e) {
		    throw new PmdBuildException(e);
		} catch (TransformerException e) {
		    throw new PmdBuildException(e);
		}
	    }
	}
	else if ( ! rulesDir.exists() ) {
	    throw new PmdBuildException("The rulesets directory specified '" + rulesDirectory + "' does not exist");
	}
	else if ( ! rulesDir.isDirectory() ) {
	    throw new PmdBuildException("The rulesets directory '" + rulesDirectory + "' provided is not a directory !");
	}
    }

    /*
     * <ol>
     * 		<li>Initialize the xml factory,</li>
     * 		<li>Check if target exist (or try to create it).</li>
     * </ol>
     */
    private void init() throws PmdBuildException {
	// Create transformer
	transformer = this.createTransformer(RULESET_TO_DOC);
	target = new File(targetDirectory);
	if ( (! target.exists() && ! target.mkdir()) ) {// no directory, creating it
	    throw new PmdBuildException("Target directory '" + targetDirectory + "' does not exist and can't be created");
	}
	else if ( target.exists() && target.isFile() ) {
	    throw new PmdBuildException("Target directory '" + targetDirectory + "' already exist and is a file.");
	}
    }

    /*
     *
     */
    private void convertRuleSet(File ruleset) throws ParserConfigurationException, SAXException, IOException, TransformerException {
	// Loading Ruleset file
	DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document document =  parser.parse(ruleset);
	DOMSource xml =  new DOMSource(document);
	// Generating the result
	StreamResult fileResult= new StreamResult(new File(this.targetDirectory + File.separator + ruleset.getName()));
	// Transforming and Writing the ruleset, xdoc fmt, on file
	this.transformer.transform(xml,fileResult);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#generateRulesIndex()
     */
    public void generateRulesIndex() throws PmdBuildException, TransformerException {
	// Merge ruleset
	System.out.println("Merging all rules into " + RuleSetToDocs.MERGED_RULESET_FILE);
	File mergedFile = new File(this.targetDirectory + File.separator + ".." + File.separator + MERGED_RULESET_FILE);
	this.transformer = createTransformer(MERGE_RULESET);
	StreamResult fileResult= new StreamResult(mergedFile);
	DOMSource xml = createXmlBackbone();
	this.transformer.transform(xml,fileResult);
	// Fix, removing the xmlns field of each ruleset in the generated xml file.
	correctXmlMergeFile(mergedFile);
	System.out.println("Creating index file:" + RuleSetToDocs.INDEX_RULESET_FILE);
	this.transformer = createTransformer(GENERATE_INDEX);
	// Create index from ruleset merge
	StreamSource src = new StreamSource(mergedFile);
	fileResult = new StreamResult(new File(this.targetDirectory + File.separator + INDEX_RULESET_FILE));
	this.transformer.transform(src,fileResult);

    }

    private DOMSource createXmlBackbone() throws PmdBuildException {
	Document doc = null;
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder parser = factory.newDocumentBuilder();
	    doc = parser.newDocument();
	} catch (ParserConfigurationException e) {
	    throw new PmdBuildException(e);
	}
	Element root = doc.createElement("root");
	doc = addingEachRuleset(doc,root);
	doc.appendChild(root);
	return new DOMSource(doc);
    }

    private Document addingEachRuleset(Document doc,Element root) {
	File rulesDir = new File(rulesDirectory);
	if ( rulesDir.exists() && rulesDir.isDirectory() ) {
	    File[] rulesets = rulesDir.listFiles(new RulesetFilenameFilter());
	    for (int fileIterator = 0; fileIterator < rulesets.length; fileIterator++ ) {
		File ruleset = rulesets[fileIterator];
		//create child element
		Element rulesetElement = doc.createElement("ruleset");
		//Add the atribute to the child
		rulesetElement.setAttribute("file",ruleset.getAbsolutePath());
		root.appendChild(rulesetElement);
	    }
	}
	return doc;
    }

    private Transformer createTransformer(String xsl) throws PmdBuildException {
	try {
	    TransformerFactory factory = TransformerFactory.newInstance();
	    StreamSource src = new StreamSource(xsl);
	    return factory.newTransformer(src);
	} catch (TransformerConfigurationException e) {
	    throw new PmdBuildException(e);
	}
    }


    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#getTargetDirectory()
     */
    public String getTargetDirectory() {
	return targetDirectory;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#setTargetDirectory(java.lang.String)
     */
    public void setTargetDirectory(String targetDirectory) {
	this.targetDirectory = targetDirectory;
    }

    public static void deleteFile(File file) {
	if ( ! file.isDirectory() ) {
	    file.delete();
	}
	else {
	    File[] files = file.listFiles();
	    for (int nbFile = 0; nbFile < files.length; nbFile++ )
		RuleSetToDocs.deleteFile(files[nbFile]);
	    file.delete();
	}
    }

    private void correctXmlMergeFile(File file) {

	File tmp = new File(file + ".tmp");
	try {
	    String line;
	    FileWriter fw = new FileWriter(tmp);
	    FileReader fr = new FileReader(file);
	    BufferedWriter bw = new BufferedWriter(fw);
	    BufferedReader br = new BufferedReader(fr);
	    while (br.ready()) {
		line = br.readLine();
		line = line.replaceAll("xmlns=\"http://pmd.sf.net/ruleset/1.0.0\"", "");
		bw.write(line);
	    }
	    fr.close();
	    bw.flush();
	    fw.close();
	    // Copy , and suppress tmp file
	    copy(tmp, file);
	    tmp.delete();
	}
	// Catches any error conditions
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void copy(File src, File dst) throws IOException
    {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                    out.write(buf, 0, len);
            in.close();
            out.close();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.build.PmdBuildTools#createPomForJava4(java.lang.String, java.lang.String)
     */
    public void createPomForJava4(String pom,String pom4java4) throws PmdBuildException {
	try {
	    Transformer transformer = this.createTransformer(POM4JAVA4);
	    StreamResult result = new StreamResult(new File(pom4java4));
	    // Loading pom file
	    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document =  parser.parse(pom);
	    transformer.transform(new DOMSource(document),result);
	} catch (PmdBuildException e) {
	    throw new PmdBuildException(e);
	} catch (SAXException e) {
	    throw new PmdBuildException(e);
	} catch (IOException e) {
	    throw new PmdBuildException(e);
	} catch (TransformerException e) {
	    throw new PmdBuildException(e);
	} catch (ParserConfigurationException e) {
	    throw new PmdBuildException(e);
	}
    }

}
