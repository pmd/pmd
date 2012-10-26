/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.report.ReportHTMLPrintVisitor;
import net.sourceforge.pmd.lang.dfa.report.ReportTree;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/**
 * Renderer to another HTML format.
 */
public class YAHTMLRenderer extends AbstractAccumulatingRenderer {

    public static final String NAME = "yahtml";

    public static final StringProperty OUTPUT_DIR = new StringProperty("outputDir", "Output directory.", null, 0);

    public YAHTMLRenderer() {
	// YA = Yet Another?
	super(NAME, "Yet Another HTML format.");
	definePropertyDescriptor(OUTPUT_DIR);
    }

    public String defaultFileExtension() { return "html"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void end() throws IOException {
	String outputDir = getProperty(OUTPUT_DIR);
	ReportTree tree = report.getViolationTree();
	tree.getRootNode().accept(new ReportHTMLPrintVisitor(outputDir == null ? ".." : outputDir));
	writer.write("<h3 align=\"center\">The HTML files are located "
		+ (outputDir == null ? "above the project directory" : "in '" + outputDir + '\'') + ".</h3>" + PMD.EOL);
    }
}
