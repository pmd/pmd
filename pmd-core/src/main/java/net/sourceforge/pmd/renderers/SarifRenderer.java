package net.sourceforge.pmd.renderers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.RuleViolation;

import java.io.IOException;
import java.util.*;

public class SarifRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "sarif";

    private Gson gson;
    private SarifLog sarifLog;
    SarifLog.Component driver;
    private List<SarifLog.ReportingDescriptor> violatedRules;
    private SarifLog.Run run;
    private List<SarifLog.Result> results;

    public SarifRenderer() {
        super(NAME, "Sarif integration.");
    }

    @Override
    public String defaultFileExtension() {
        return "sarif.json";
    }

    @Override
    public void start() throws IOException {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        run = new SarifLog.Run();
        defineRunTool();

        sarifLog = new SarifLog();
        violatedRules = new LinkedList<>();
        results = new LinkedList<>();
    }

    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {

        while (violations.hasNext()) {
            RuleViolation rv = violations.next();

            Integer violatedRuleIndex = getRuleViolationIndex(rv);
            if (violatedRuleIndex != -1) {
                SarifLog.Result existingResult = getResultByRuleIndex(violatedRuleIndex);

                List<SarifLog.Location> locations = existingResult.getLocations();
                locations.add(getRuleViolationLocation(rv));
                existingResult.setLocations(locations);
            } else {
                SarifLog.ReportingDescriptor rule = createReportingDescriptor(rv);
                violatedRules.add(rule);
                violatedRuleIndex = violatedRules.indexOf(rule);

                SarifLog.Result result = new SarifLog.Result();
                result.setRuleId(rv.getRule().getName());
                result.setRuleIndex(violatedRuleIndex);

                SarifLog.Message message = new SarifLog.Message();
                message.setText(rv.getDescription());
                result.setMessage(message);

                List<SarifLog.Location> locations = new LinkedList<>();
                locations.add(getRuleViolationLocation(rv));
                result.setLocations(locations);

                results.add(result);
            }
        }
    }

    @Override
    public void end() throws IOException {
        if (errors.isEmpty() && configErrors.isEmpty()) {
            driver.setRules(violatedRules);
            run.setResults(results);

            List<SarifLog.Run> runs = new LinkedList<>();
            runs.add(run);
            sarifLog.setRuns(runs);

            String json = gson.toJson(sarifLog);

            writer.write(json);
        }
    }

    private void defineRunTool() {
        driver = new SarifLog.Component();
        driver.setName("PMD"); // to improve
        driver.setVersion(PMDVersion.VERSION);
        driver.setInformationUri("https://github.com/pmd"); // to improve

        SarifLog.Tool tool = new SarifLog.Tool();
        tool.setDriver(driver);

        run.setTool(tool);
    }

    private Integer getRuleViolationIndex(RuleViolation rv) {
        Integer result = -1;
        for (SarifLog.ReportingDescriptor rule : violatedRules) {
            if (rule.getId() == rv.getRule().getName()) {
                result = violatedRules.indexOf(rule);
            }
        }
        return result;
    }

    private SarifLog.ReportingDescriptor createReportingDescriptor(RuleViolation rv) {
        SarifLog.ReportingDescriptor result = new SarifLog.ReportingDescriptor();
        result.setId(rv.getRule().getName());
        result.setName(rv.getRule().getName());
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

        SarifLog.Location location = new SarifLog.Location();
        location.setPhysicalLocation(physicalLocation);

        return location;
    }

    private SarifLog.PropertyBag getRuleProperties(RuleViolation rv) {
        SarifLog.PropertyBag propertyBag = new SarifLog.PropertyBag();

        propertyBag.setRuleset(rv.getRule().getRuleSetName());
        propertyBag.setPriority(rv.getRule().getPriority().getPriority());

        return propertyBag;
    }


}
