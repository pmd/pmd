/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_BLOCK_HIGHLIGHTING;
import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_CATEGORIES;
import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_REMEDIATION_MULTIPLIER;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Renderer for Code Climate JSON format
 */
public class CodeClimateRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "codeclimate";
    public static final String BODY_PLACEHOLDER = "REPLACE_THIS_WITH_MARKDOWN";
    public static final int REMEDIATION_POINTS_DEFAULT = 50000;
    public static final String[] CODECLIMATE_DEFAULT_CATEGORIES = new String[] {"Style"};

    // Note: required by https://github.com/codeclimate/spec/blob/master/SPEC.md
    protected static final String NULL_CHARACTER = "\u0000";
    protected static final List<String> INTERNAL_DEV_PROPERTIES = Arrays.asList("version", "xpath");
    private final String pmdDeveloperUrl;
    private Rule rule;

    public CodeClimateRenderer() {
        super(NAME, "Code Climate integration.");
        pmdDeveloperUrl = getPmdDeveloperURL();
    }

    private static String getPmdDeveloperURL() {
        String url = "http://pmd.github.io/pmd-" + PMD.VERSION + "/customizing/pmd-developer.html";
        if (PMD.VERSION.contains("SNAPSHOT") || "unknown".equals(PMD.VERSION)) {
            url = "http://pmd.sourceforge.net/snapshot/customizing/pmd-developer.html";
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            rule = rv.getRule();
            String json = gson.toJson(asIssue(rv));
            json = json.replace(BODY_PLACEHOLDER, getBody());
            writer.write(json + NULL_CHARACTER + PMD.EOL);
        }
    }

    /**
     * Generate a CodeClimateIssue suitable for processing into JSON from the
     * given RuleViolation.
     *
     * @param rv RuleViolation to convert.
     *
     * @return The generated issue.
     */
    private CodeClimateIssue asIssue(RuleViolation rv) {
        CodeClimateIssue issue = new CodeClimateIssue();
        issue.check_name = rule.getName();
        issue.description = cleaned(rv.getDescription());
        issue.content = new CodeClimateIssue.Content(BODY_PLACEHOLDER);
        issue.location = getLocation(rv);
        issue.remediation_points = getRemediationPoints();
        issue.categories = getCategories();

        switch (rule.getPriority()) {
        case HIGH:
            issue.severity = "critical";
            break;
        case MEDIUM_HIGH:
        case MEDIUM:
        case MEDIUM_LOW:
            issue.severity = "normal";
            break;
        case LOW:
        default:
            issue.severity = "info";
            break;
        }

        return issue;
    }

    @Override
    public String defaultFileExtension() {
        return "json";
    }

    private CodeClimateIssue.Location getLocation(RuleViolation rv) {
        CodeClimateIssue.Location result;

        String pathWithoutCcRoot = StringUtils.removeStartIgnoreCase(rv.getFilename(), "/code/");

        if (rule.hasDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER)
            && !rule.getProperty(CODECLIMATE_BLOCK_HIGHLIGHTING)) {
            result = new CodeClimateIssue.Location(pathWithoutCcRoot, rv.getBeginLine(), rv.getBeginLine());
        } else {
            result = new CodeClimateIssue.Location(pathWithoutCcRoot, rv.getBeginLine(), rv.getEndLine());
        }

        return result;
    }

    private int getRemediationPoints() {
        int remediationPoints = REMEDIATION_POINTS_DEFAULT;

        if (rule.hasDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER)) {
            remediationPoints *= rule.getProperty(CODECLIMATE_REMEDIATION_MULTIPLIER);
        }

        return remediationPoints;
    }

    private String[] getCategories() {
        String[] result;

        if (rule.hasDescriptor(CODECLIMATE_CATEGORIES)) {
            List<String> categories = rule.getProperty(CODECLIMATE_CATEGORIES);
            result = categories.toArray(new String[0]);
        } else {
            result = CODECLIMATE_DEFAULT_CATEGORIES;
        }

        return result;
    }

    private <T> String getBody() {
        String result = "## " + rule.getName() + "\\n\\n" + "Since: PMD " + rule.getSince() + "\\n\\n" + "Priority: "
            + rule.getPriority() + "\\n\\n"
            + "[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): "
            + Arrays.toString(getCategories()).replaceAll("[\\[\\]]", "") + "\\n\\n"
            + "[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): "
            + getRemediationPoints() + "\\n\\n" + cleaned(rule.getDescription());

        if (!rule.getExamples().isEmpty()) {
            result += "\\n\\n### Example:\\n\\n";

            for (String snippet : rule.getExamples()) {
                String exampleSnippet = snippet.replaceAll("\\n", "\\\\n");
                exampleSnippet = exampleSnippet.replaceAll("\\t", "\\\\t");
                result += "```java\\n" + exampleSnippet + "\\n```  ";
            }
        }

        if (!rule.getPropertyDescriptors().isEmpty()) {
            result += "\\n\\n### [PMD properties](" + pmdDeveloperUrl + ")\\n\\n";
            result += "Name | Value | Description\\n";
            result += "--- | --- | ---\\n";

            for (PropertyDescriptor<?> property : rule.getPropertyDescriptors()) {
                String propertyName = property.name().replaceAll("\\_", "\\\\_");
                if (INTERNAL_DEV_PROPERTIES.contains(propertyName)) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                PropertyDescriptor<T> typed = (PropertyDescriptor<T>) property;
                T value = rule.getProperty(typed);
                String propertyValue = typed.asDelimitedString(value);
                if (propertyValue == null) {
                    propertyValue = "";
                }
                propertyValue = propertyValue.replaceAll("(\n|\r\n|\r)", "\\\\n");

                result += propertyName + " | " + propertyValue + " | " + property.description() + "\\n";
            }
        }
        return cleaned(result);
    }

    private String cleaned(String original) {
        String result = original.trim();
        result = result.replaceAll("\\s+", " ");
        result = result.replaceAll("\\s*[\\r\\n]+\\s*", "");
        result = result.replaceAll("\"", "'");
        return result;
    }
}
