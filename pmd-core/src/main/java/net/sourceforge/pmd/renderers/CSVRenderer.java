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

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.renderers.ColumnDescriptor.Accessor;


/**
 * Renderer the results to a comma-delimited text format. All available columns
 * are present by default. IDEs can enable/disable columns individually
 * (cmd-line control to follow eventually)
 */
public class CSVRenderer extends AbstractIncrementingRenderer {

    private String separator;
    private String cr;

    private CSVWriter<RuleViolation> csvWriter;

    private static final String DEFAULT_SEPARATOR = ",";

    private static final Map<String, PropertyDescriptor<Boolean>> PROPERTY_DESCRIPTORS_BY_ID = new HashMap<>();

    public static final String NAME = "csv";

    @SuppressWarnings("unchecked")
    private final ColumnDescriptor<RuleViolation>[] allColumns = new ColumnDescriptor[] {
        new ColumnDescriptor<>("problem", "Problem", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return Integer.toString(idx);
            }
        }), new ColumnDescriptor<>("package", "Package", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return rv.getPackageName();
            }
        }), new ColumnDescriptor<>("file", "File", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return CSVRenderer.this.determineFileName(rv.getFilename());
            }
        }), new ColumnDescriptor<>("priority", "Priority", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return Integer.toString(rv.getRule().getPriority().getPriority());
            }
        }), new ColumnDescriptor<>("line", "Line", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return Integer.toString(rv.getBeginLine());
            }
        }), new ColumnDescriptor<>("desc", "Description", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return StringUtils.replaceChars(rv.getDescription(), '\"', '\'');
            }
        }), new ColumnDescriptor<>("ruleSet", "Rule set", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return rv.getRule().getRuleSetName();
            }
        }), new ColumnDescriptor<>("rule", "Rule", new Accessor<RuleViolation>() {
            @Override
            public String get(int idx, RuleViolation rv, String cr) {
                return rv.getRule().getName();
            }
        }), };

    public CSVRenderer(ColumnDescriptor<RuleViolation>[] columns, String theSeparator, String theCR) {
        super(NAME, "Comma-separated values tabular format.");

        separator = theSeparator;
        cr = theCR;

        for (ColumnDescriptor<RuleViolation> desc : columns) {
            definePropertyDescriptor(booleanPropertyFor(desc.id, desc.title));
        }
    }

    public CSVRenderer() {
        super(NAME, "Comma-separated values tabular format.");

        separator = DEFAULT_SEPARATOR;
        cr = PMD.EOL;

        for (ColumnDescriptor<RuleViolation> desc : allColumns) {
            definePropertyDescriptor(booleanPropertyFor(desc.id, desc.title));
        }
    }

    private static PropertyDescriptor<Boolean> booleanPropertyFor(String id, String label) {

        PropertyDescriptor<Boolean> prop = PROPERTY_DESCRIPTORS_BY_ID.get(id);
        if (prop != null) {
            return prop;
        }

        prop = PropertyFactory.booleanProperty(id).defaultValue(true).desc("Include " + label + " column").build();
        PROPERTY_DESCRIPTORS_BY_ID.put(id, prop);
        return prop;
    }

    private List<ColumnDescriptor<RuleViolation>> activeColumns() {

        List<ColumnDescriptor<RuleViolation>> actives = new ArrayList<>();

        for (ColumnDescriptor<RuleViolation> desc : allColumns) {
            PropertyDescriptor<Boolean> prop = booleanPropertyFor(desc.id, null);
            if (getProperty(prop)) {
                actives.add(desc);
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

    @Override
    public void start() throws IOException {
        csvWriter().writeTitles(getWriter());
    }

    @Override
    public String defaultFileExtension() {
        return "csv";
    }

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
