/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.renderers.ColumnDescriptor.Accessor;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Renderer the results to a comma-delimited text format. All available columns
 * are present by default. IDEs can enable/disable columns individually (cmd-line
 * control to follow eventually)
 */
public class CSVRenderer extends AbstractIncrementingRenderer {

    private String separator;
    private String cr;

    private CSVWriter<RuleViolation> csvWriter;

    private static final String DefaultSeparator = ",";

    private static final Map<String, BooleanProperty> propertyDescriptorsById = new HashMap<String, BooleanProperty>();

    public static final String NAME = "csv";

    @SuppressWarnings("unchecked")
	private static final ColumnDescriptor<RuleViolation>[] AllColumns = new ColumnDescriptor[] {
    	new ColumnDescriptor<RuleViolation>("problem", 	"Problem", 		new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(idx); }} ),
    	new ColumnDescriptor<RuleViolation>("package",	"Package", 		new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return rv.getPackageName(); }} ),
    	new ColumnDescriptor<RuleViolation>("file",		"File", 		new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return rv.getFilename(); }} ),
    	new ColumnDescriptor<RuleViolation>("priority",	"Priority", 	new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(rv.getRule().getPriority().getPriority()); }} ),
    	new ColumnDescriptor<RuleViolation>("line",		"Line", 		new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(rv.getBeginLine()); }} ),
    	new ColumnDescriptor<RuleViolation>("desc",		"Description", 	new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return StringUtil.replaceString(rv.getDescription(), '\"', "'"); }} ),
    	new ColumnDescriptor<RuleViolation>("ruleSet",	"Rule set", 	new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return rv.getRule().getRuleSetName(); }} ),
    	new ColumnDescriptor<RuleViolation>("rule",		"Rule", 		new Accessor<RuleViolation>() { public String get(int idx, RuleViolation rv, String cr) { return rv.getRule().getName(); }} )
    	};


    private static BooleanProperty booleanPropertyFor(String id, String label) {

    	BooleanProperty prop = propertyDescriptorsById.get(id);
    	if (prop != null) return prop;

    	prop = new BooleanProperty(id, "Include " + label + " column", true, 1.0f);
    	propertyDescriptorsById.put(id, prop);
    	return prop;
    }

    public CSVRenderer(ColumnDescriptor<RuleViolation>[] columns, String theSeparator, String theCR) {
    	super(NAME, "Comma-separated values tabular format.");

    	separator = theSeparator;
    	cr = theCR;

    	for (ColumnDescriptor<RuleViolation> desc : columns) {
    		definePropertyDescriptor(booleanPropertyFor(desc.id, desc.title));
    		}
    }

    private List<ColumnDescriptor<RuleViolation>> activeColumns() {

    	List<ColumnDescriptor<RuleViolation>> actives = new ArrayList<ColumnDescriptor<RuleViolation>>();

     	for (ColumnDescriptor<RuleViolation> desc : AllColumns) {
    		BooleanProperty prop = booleanPropertyFor(desc.id, null);
    		if (getProperty(prop)) {
    			actives.add(desc);
    			} else {
//    				System.out.println("disabled: " + prop);
    			}
    		}
     	return actives;
    }

    private CSVWriter<RuleViolation> csvWriter() {
    	if (csvWriter != null) return csvWriter;

    	csvWriter = new CSVWriter<RuleViolation>(activeColumns(), separator, cr);
    	return csvWriter;
    }

    public CSVRenderer() {
    	this( AllColumns, DefaultSeparator, PMD.EOL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
    	csvWriter().writeTitles(getWriter());
    }

    public String defaultFileExtension() { return "csv"; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
    	csvWriter().writeData(getWriter(), violations);
    }

	 /**
	  * We can't show any violations if we don't have any visible columns.
	  *
	  * @see PropertySource#dysfunctionReason()
	  */
    @Override
	 public String dysfunctionReason() {
		 return activeColumns().isEmpty() ? "No columns selected" : null;
	 }
}
