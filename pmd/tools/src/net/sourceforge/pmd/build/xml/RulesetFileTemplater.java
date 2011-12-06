/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.filefilter.DirectoryFileFilter;
import net.sourceforge.pmd.build.filefilter.RulesetFilenameFilter;
import net.sourceforge.pmd.build.util.ConfigUtil;
import net.sourceforge.pmd.build.util.FileUtil;
import net.sourceforge.pmd.build.util.XmlUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class RulesetFileTemplater implements XmlFileTemplater {

	private static Logger logger = Logger.getLogger(PmdBuildException.class.toString());

	private String rulesetToDocsXsl = ConfigUtil.getString("pmd.build.config.xsl.rulesetToDocs");
	private String mergeRulesetXsl = ConfigUtil.getString("pmd.build.config.xsl.mergeRuleset");
	private String generateIndexXsl = ConfigUtil.getString("pmd.build.config.xsl.rulesIndex");
	private String createRulesetMenuXsl = ConfigUtil.getString("pmd.build.config.xsl.createRulesetMenu");
	private String addToSiteDescriptorXsl = ConfigUtil.getString("pmd.build.config.xsladdToSiteDescriptor");

	public String getAddToSiteDescriptorXsl() {
		return addToSiteDescriptorXsl;
	}

	public void setAddToSiteDescriptorXsl(String addToSiteDescriptorXsl) {
		this.addToSiteDescriptorXsl = addToSiteDescriptorXsl;
	}

	public String getCreateRulesetMenuXsl() {
		return createRulesetMenuXsl;
	}

	public void setCreateRulesetMenuXsl(String createRulesetMenuXsl) {
		this.createRulesetMenuXsl = createRulesetMenuXsl;
	}

	public String getRulesetToDocsXsl() {
		return rulesetToDocsXsl;
	}

	public void setRulesetToDocsXsl(String rulesetToDocsXsl) {
		this.rulesetToDocsXsl = rulesetToDocsXsl;
	}

	public String getMergeRulesetXsl() {
		return mergeRulesetXsl;
	}

	public void setMergeRulesetXsl(String mergeRulesetXsl) {
		this.mergeRulesetXsl = mergeRulesetXsl;
	}

	public String getGenerateIndexXsl() {
		return generateIndexXsl;
	}

	public void setGenerateIndexXsl(String generateIndexXsl) {
		this.generateIndexXsl = generateIndexXsl;
	}

	private final String rulesDirectory;

	public RulesetFileTemplater(String rulesDirectory) {
		this.rulesDirectory = rulesDirectory;
	}

	@Override
	public Document doTemplate(Document doc, Element root) {
		for ( File dir : FileUtil.filterFilesFrom(FileUtil.existAndIsADirectory(rulesDirectory), new DirectoryFileFilter() )) {
			logger.fine("Adding directory:" + dir.getAbsolutePath());
			doc = addRulesetForEachLanguage(doc, root, dir);
		}
		return doc;
	}

	private Document addRulesetForEachLanguage(Document doc,Element root, File directory) {
		Element language = doc.createElement("language");
		language.setAttribute("name",directory.getName());
		language = addEachRuleset(doc, language, directory);
		if ( language.hasChildNodes() )
			root.appendChild(language);
		return doc;
	}

	private Element addEachRuleset(Document doc,Element language, File directory) {
		for ( File ruleset : FileUtil.filterFilesFrom(directory, new RulesetFilenameFilter() ) ) {
			Element rulesetElement = doc.createElement("ruleset");
			rulesetElement.setAttribute("file",ruleset.getAbsolutePath());
			rulesetElement.setAttribute("filename",ruleset.getName());
			rulesetElement.setAttribute("language",directory.getName());
			language.appendChild(rulesetElement);
		}
		return language;
	}

	@Override
	public void transform(File source, File result, String xsl) {
		transform(source, result, xsl, new HashMap<String, String>(0));
	}

	@Override
	public void transform(DOMSource source, File result, String xsl) {
		this.transform(source, result, xsl,new HashMap<String, String>(0));
	}

	@Override
	public void transform(DOMSource source, File result, String xsl,
			Map<String, String> parameters) {
		try {
			Transformer transformer =  XmlUtil.createTransformer(xsl);
			for ( Entry<String,String> entry: parameters.entrySet() )
				transformer.setParameter(entry.getKey(), entry.getValue());
			transformer.transform(source,new StreamResult(result));
		} catch (TransformerException e) {
			throw new IllegalStateException(e);
		} catch (PmdBuildException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void transform(File source, File result, String xsl,
			Map<String, String> parameters) {
		try {
			this.transform(XmlUtil.createDomSourceFrom(new FileInputStream(source)),result,xsl,parameters);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
