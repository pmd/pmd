/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDVersion;
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

    // Note: required by https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md
    protected static final String NULL_CHARACTER = "\u0000";
    protected static final List<String> INTERNAL_DEV_PROPERTIES = Arrays.asList("version", "xpath");
    private static final String PMD_PROPERTIES_URL = getPmdPropertiesURL();
    private Rule rule;

    public CodeClimateRenderer() {
        super(NAME, "Code Climate integration.");
    }

    private static String getPmdPropertiesURL() {
        final String PAGE = "/pmd_userdocs_configuring_rules.html#rule-properties";
        String url = "https://pmd.github.io/pmd-" + PMDVersion.VERSION + PAGE;
        if (PMDVersion.isSnapshot() || PMDVersion.isUnknown()) {
            url = "https://pmd.github.io/latest" + PAGE;
        }
        return url;
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
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
        String pathWithoutCcRoot = StringUtils.removeStartIgnoreCase(determineFileName(rv.getFilename()), "/code/");
        return new CodeClimateIssue.Location(pathWithoutCcRoot, rv.getBeginLine(), rv.getEndLine());
    }

    private int getRemediationPoints() {
        return REMEDIATION_POINTS_DEFAULT;
    }

    private String[] getCategories() {
        return CODECLIMATE_DEFAULT_CATEGORIES;
    }

    private <T> String getBody() {
        StringBuilder result = new StringBuilder();
        result.append("## ")
                .append(rule.getName())
                .append("\\n\\n")
                .append("Since: PMD ")
                .append(rule.getSince())
                .append("\\n\\n")
                .append("Priority: ")
                .append(rule.getPriority())
                .append("\\n\\n")
                .append("[Categories](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#categories): ")
                .append(Arrays.toString(getCategories()).replaceAll("[\\[\\]]", ""))
                .append("\\n\\n")
                .append("[Remediation Points](https://github.com/codeclimate/platform/blob/master/spec/analyzers/SPEC.md#remediation-points): ")
                .append(getRemediationPoints())
                .append("\\n\\n")
                .append(cleaned(rule.getDescription()));

        if (!rule.getExamples().isEmpty()) {
            result.append("\\n\\n### Example:\\n\\n");

            for (String snippet : rule.getExamples()) {
                String exampleSnippet = snippet.replaceAll("\\n", "\\\\n");
                exampleSnippet = exampleSnippet.replaceAll("\\t", "\\\\t");
                result.append("```java\\n").append(exampleSnippet).append("\\n```  ");
            }
        }

        if (!rule.getPropertyDescriptors().isEmpty()) {
            result.append("\\n\\n### [PMD properties](").append(PMD_PROPERTIES_URL).append(")\\n\\n");
            result.append("Name | Value | Description\\n");
            result.append("--- | --- | ---\\n");

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
                propertyValue = propertyValue.replaceAll("\\R", "\\\\n");

                result.append(propertyName).append(" | ").append(propertyValue).append(" | ").append(property.description()).append("\\n");
            }
        }
        return cleaned(result.toString());
    }

    private String cleaned(String original) {
        String result = original.trim();
        result = result.replaceAll("\\s+", " ");
        result = result.replaceAll("\\s*[\\r\\n]+\\s*", "");
        result = result.replaceAll("\"", "'");
        return result;
    }
}
