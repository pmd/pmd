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
import org.checkerframework.checker.nullness.qual.NonNull;

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

    private final String separator;
    private final String cr;

    private CSVWriter<RuleViolation> csvWriter;

    private static final String DEFAULT_SEPARATOR = ",";

    private static final Map<String, PropertyDescriptor<Boolean>> PROPERTY_DESCRIPTORS_BY_ID = new HashMap<>();

    public static final String NAME = "csv";

    @SuppressWarnings("unchecked")
    private final ColumnDescriptor<RuleViolation>[] allColumns = new ColumnDescriptor[] {
        newColDescriptor("problem", "Problem", (idx, rv, cr) -> Integer.toString(idx)),
        newColDescriptor("package", "Package", (idx, rv, cr) -> rv.getAdditionalInfo().getOrDefault(RuleViolation.PACKAGE_NAME, "")),
        newColDescriptor("file", "File", (idx, rv, cr) -> determineFileName(rv.getFilename())),
        newColDescriptor("priority", "Priority", (idx, rv, cr) -> Integer.toString(rv.getRule().getPriority().getPriority())),
        newColDescriptor("line", "Line", (idx, rv, cr) -> Integer.toString(rv.getBeginLine())),
        newColDescriptor("desc", "Description", (idx, rv, cr) -> StringUtils.replaceChars(rv.getDescription(), '\"', '\'')),
        newColDescriptor("ruleSet", "Rule set", (idx, rv, cr) -> rv.getRule().getRuleSetName()),
        newColDescriptor("rule", "Rule", (idx, rv, cr) -> rv.getRule().getName()),
    };

    private static @NonNull ColumnDescriptor<RuleViolation> newColDescriptor(String id, String title, Accessor<RuleViolation> accessor) {
        return new ColumnDescriptor<>(id, title, accessor);
    }

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
