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

    private static final String DEFAULT_SEPARATOR = ",";

    private static final Map<String, BooleanProperty> PROPERTY_DESCRIPTORS_BY_ID = new HashMap<>();

    public static final String NAME = "csv";

    @SuppressWarnings("unchecked")
	private static final ColumnDescriptor<RuleViolation>[] ALL_COLUMNS = new ColumnDescriptor[] {
    	new ColumnDescriptor<>("problem", 	"Problem", 		new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(idx); }} ),
    	new ColumnDescriptor<>("package",	"Package", 		new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return rv.getPackageName(); }} ),
    	new ColumnDescriptor<>("file",		"File", 		new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return rv.getFilename(); }} ),
    	new ColumnDescriptor<>("priority",	"Priority", 	new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(rv.getRule().getPriority().getPriority()); }} ),
    	new ColumnDescriptor<>("line",		"Line", 		new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return Integer.toString(rv.getBeginLine()); }} ),
    	new ColumnDescriptor<>("desc",		"Description", 	new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return StringUtil.replaceString(rv.getDescription(), '\"', "'"); }} ),
    	new ColumnDescriptor<>("ruleSet",	"Rule set", 	new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return rv.getRule().getRuleSetName(); }} ),
    	new ColumnDescriptor<>("rule",		"Rule", 		new Accessor<RuleViolation>() { @Override
        public String get(int idx, RuleViolation rv, String cr) { return rv.getRule().getName(); }} )
    	};

    public CSVRenderer(ColumnDescriptor<RuleViolation>[] columns, String theSeparator, String theCR) {
        super(NAME, "Comma-separated values tabular format.");

        separator = theSeparator;
        cr = theCR;

        for (ColumnDescriptor<RuleViolation> desc : columns) {
            definePropertyDescriptor(booleanPropertyFor(desc.id, desc.title));
        }
    }

    public CSVRenderer() {
        this(ALL_COLUMNS, DEFAULT_SEPARATOR, PMD.EOL);
    }

    private static BooleanProperty booleanPropertyFor(String id, String label) {

    	BooleanProperty prop = PROPERTY_DESCRIPTORS_BY_ID.get(id);
    	if (prop != null) {
    	    return prop;
    	}

    	prop = new BooleanProperty(id, "Include " + label + " column", true, 1.0f);
    	PROPERTY_DESCRIPTORS_BY_ID.put(id, prop);
    	return prop;
    }

    private List<ColumnDescriptor<RuleViolation>> activeColumns() {

    	List<ColumnDescriptor<RuleViolation>> actives = new ArrayList<>();

     	for (ColumnDescriptor<RuleViolation> desc : ALL_COLUMNS) {
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
    	if (csvWriter != null) {
    	    return csvWriter;
    	}

    	csvWriter = new CSVWriter<>(activeColumns(), separator, cr);
    	return csvWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
    	csvWriter().writeTitles(getWriter());
    }

    @Override
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
