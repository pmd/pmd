/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.RuleViolation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SarifRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "sarif";

    private SarifLog sarifLog = new SarifLog();
    private SarifLog.Run run;
    private SarifLog.Component driver;
    private List<SarifLog.ReportingDescriptor> ruleDescriptors = new LinkedList<>();
    private List<SarifLog.Result> results = new LinkedList<>();

    public SarifRenderer() {
        super(NAME, "Sarif integration.");
    }

    @Override
    public String defaultFileExtension() {
        return "sarif.json";
    }

    @Override
    public void start() throws IOException {
        driver = getDriverComponent();
        SarifLog.Tool tool = new SarifLog.Tool().setDriver(driver);
        run = new SarifLog.Run().setTool(tool);
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {

        while (violations.hasNext()) {
            SarifLog.Result result;
            List<SarifLog.Location> locations = new LinkedList<>();

            RuleViolation rv = violations.next();

            Integer ruleIndex = getRuleViolationIndex(rv);
            if (ruleIndex != -1) {
                result = getResultByRuleIndex(ruleIndex);
                locations = result.getLocations();
            } else {
                result = resultFrom(rv);
                results.add(result);
            }

            locations.add(getRuleViolationLocation(rv));
            result.setLocations(locations);
        }
    }

    @Override
    public void end() throws IOException {
        if (errors.isEmpty() && configErrors.isEmpty()) {
            driver.setRules(ruleDescriptors);
            run.setResults(results);

            List<SarifLog.Run> runs = new LinkedList<>();
            runs.add(run);
            sarifLog.setRuns(runs);

            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(sarifLog);
            writer.write(json);
        }
    }

    private SarifLog.Result resultFrom(RuleViolation violation) {
        SarifLog.ReportingDescriptor ruleDescriptor = getReportingDescriptor(violation);
        ruleDescriptors.add(ruleDescriptor);
        Integer ruleIndex = ruleDescriptors.indexOf(ruleDescriptor);

        SarifLog.Result result = new SarifLog.Result();
        result.setRuleId(violation.getRule().getName());
        result.setRuleIndex(ruleIndex);

        SarifLog.Message message = new SarifLog.Message();
        message.setText(violation.getDescription());
        result.setMessage(message);

        return result;
    }

    private SarifLog.Component getDriverComponent() {
        SarifLog.Component result = new SarifLog.Component();

        result.setName("PMD");
        result.setVersion(PMDVersion.VERSION);
        result.setInformationUri("https://pmd.github.io/pmd/");

        return result;
    }

    private Integer getRuleViolationIndex(RuleViolation rv) {
        Integer result = -1;
        for (SarifLog.ReportingDescriptor rule : ruleDescriptors) {
            if (rule.getId() == rv.getRule().getName()) {
                result = ruleDescriptors.indexOf(rule);
            }
        }
        return result;
    }

    private SarifLog.ReportingDescriptor getReportingDescriptor(RuleViolation rv) {
        SarifLog.ReportingDescriptor result = new SarifLog.ReportingDescriptor();

        result.setId(rv.getRule().getName());
        result.setShortDescription(new SarifLog.MultiformatMessage(rv.getDescription()));
        result.setHelpUri(rv.getRule().getExternalInfoUrl());
        result.setProperties(getRuleProperties(rv));

        return result;
    }

    private SarifLog.Result getResultByRuleIndex(Integer ruleIndex) {
        SarifLog.Result result = null;

        for (SarifLog.Result res : results) {
            if (res.getRuleIndex() == ruleIndex) {
                result = res;
            }
        }
        return result;
    }

    private SarifLog.Location getRuleViolationLocation(RuleViolation rv) {
        SarifLog.ArtifactLocation artifactLocation = new SarifLog.ArtifactLocation();
        artifactLocation.setUri(rv.getFilename());

        SarifLog.Region region = new SarifLog.Region();
        region.setStartLine(rv.getBeginLine());
        region.setEndLine(rv.getEndLine());
        region.setStartColumn(rv.getBeginColumn());
        region.setEndColumn(rv.getEndColumn());

        SarifLog.PhysicalLocation physicalLocation = new SarifLog.PhysicalLocation();
        physicalLocation.setArtifactLocation(artifactLocation);
        physicalLocation.setRegion(region);

        SarifLog.Location result = new SarifLog.Location();
        result.setPhysicalLocation(physicalLocation);

        return result;
    }

    private SarifLog.PropertyBag getRuleProperties(RuleViolation rv) {
        SarifLog.PropertyBag result = new SarifLog.PropertyBag();

        result.setRuleset(rv.getRule().getRuleSetName());
        result.setPriority(rv.getRule().getPriority().getPriority());

        return result;
    }
}
