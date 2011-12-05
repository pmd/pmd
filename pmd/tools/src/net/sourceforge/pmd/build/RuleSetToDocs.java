/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import static net.sourceforge.pmd.build.util.ConfigUtil.getString;
import static net.sourceforge.pmd.build.util.XmlUtil.createXmlBackbone;

import java.io.File;

import net.sourceforge.pmd.build.filefilter.DirectoryFileFilter;
import net.sourceforge.pmd.build.filefilter.RulesetFilenameFilter;
import net.sourceforge.pmd.build.util.FileUtil;
import net.sourceforge.pmd.build.xml.RulesetFileTemplater;

/**
 * A small class to convert files from pmd rulesets fmt to xdoc fmt
 *
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class RuleSetToDocs implements PmdBuildTools {

	private String indexRuleSetFilename = getString("pmd.build.config.index.filename");
	private String mergedRuleSetFilename = getString("pmd.build.config.mergedRuleset.filename");

	private String rulesDirectory;
	private String targetDirectory;

	private RulesetFileTemplater xmlFileTemplater;

	public String getRulesDirectory() {
		return rulesDirectory;
	}

	public void setRulesDirectory(String rulesDirectory) {
		this.rulesDirectory = rulesDirectory;
	}

	public String getIndexRuleSetFilename() {
		return indexRuleSetFilename;
	}

	public void setIndexRuleSetFilename(String indexRuleSetFilename) {
		this.indexRuleSetFilename = indexRuleSetFilename;
	}

	public String getMergedRuleSetFilename() {
		return mergedRuleSetFilename;
	}

	public void setMergedRuleSetFilename(String mergedRuleSetFilename) {
		this.mergedRuleSetFilename = mergedRuleSetFilename;
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}

	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public RulesetFileTemplater getXmlFileTemplater() {
		return xmlFileTemplater;
	}

	public void setXmlFileTemplater(RulesetFileTemplater xmlFileTemplater) {
		this.xmlFileTemplater = xmlFileTemplater;
	}

	/*
	 * <ol>
	 * 		<li>Initialize the xml factory,</li>
	 * 		<li>Check if target exist (or try to create it).</li>
	 * </ol>
	 */
	private void init() throws PmdBuildException {
		FileUtil.createDirIfMissing(targetDirectory);
		xmlFileTemplater = new RulesetFileTemplater(rulesDirectory);
		System.out.println("Merge xsl:" + xmlFileTemplater.getMergeRulesetXsl());
	}

	public void convertRulesets() throws PmdBuildException {
		init();
		File rulesDir = new File(rulesDirectory);
		if ( rulesDir.exists() && rulesDir.isDirectory() )
			recursivelyProcessSubFolder(processAllXDocsFilesFromDir(rulesDir));
		else if ( ! rulesDir.exists() )
			throw new PmdBuildException("The rulesets directory specified '" + rulesDirectory + "' does not exist");
		else if ( ! rulesDir.isDirectory() )
			throw new PmdBuildException("The rulesets directory '" + rulesDirectory + "' provided is not a directory !");
	}

	private void recursivelyProcessSubFolder(File rulesDir) throws PmdBuildException  {
		for ( File folder : FileUtil.filterFilesFrom(rulesDir, new DirectoryFileFilter()) )
			recursivelyProcessSubFolder(processAllXDocsFilesFromDir(folder));
	}

	private File processAllXDocsFilesFromDir(File rulesDir) throws PmdBuildException {
		for ( File ruleset : FileUtil.filterFilesFrom(rulesDir, new RulesetFilenameFilter() ) )
			processXDocFile(ruleset);
		return rulesDir;
	}

	private void processXDocFile(File ruleset) throws PmdBuildException {
		File targetFile = new File(this.targetDirectory + File.separator + ruleset.getName());
		System.out.println("Processing file " + ruleset + " into " + targetFile.getAbsolutePath());
		FileUtil.ensureTargetDirectoryExist(targetFile);
		convertRuleSetFile(ruleset, targetFile);
	}

	private void convertRuleSetFile(File ruleset,File target) throws PmdBuildException {
		xmlFileTemplater.transform(ruleset,target,xmlFileTemplater.getRulesetToDocsXsl());
	}

	public void generateRulesIndex() {
		System.out.println("Merging all rules into " + this.mergedRuleSetFilename);
		File mergedFile = new File(this.targetDirectory + File.separator + ".." + File.separator + mergedRuleSetFilename);
		xmlFileTemplater.transform(createXmlBackbone(xmlFileTemplater), mergedFile, xmlFileTemplater.getMergeRulesetXsl());
		// Fix, removing the xmlns field of each ruleset in the generated xml file.
		FileUtil.replaceAllInFile(mergedFile, "xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"", "");
		System.out.println("Creating index file:" + this.indexRuleSetFilename + ", using merged file:" + mergedFile.toString());
		// Create index from ruleset merge
		xmlFileTemplater.transform(mergedFile,new File(this.targetDirectory + File.separator + indexRuleSetFilename) ,xmlFileTemplater.getGenerateIndexXsl());
	}
}
