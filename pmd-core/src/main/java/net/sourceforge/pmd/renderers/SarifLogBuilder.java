/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static lombok.AccessLevel.PRIVATE;
import static net.sourceforge.pmd.renderers.SarifLog.ArtifactLocation;
import static net.sourceforge.pmd.renderers.SarifLog.Component;
import static net.sourceforge.pmd.renderers.SarifLog.Location;
import static net.sourceforge.pmd.renderers.SarifLog.Message;
import static net.sourceforge.pmd.renderers.SarifLog.MultiformatMessage;
import static net.sourceforge.pmd.renderers.SarifLog.PhysicalLocation;
import static net.sourceforge.pmd.renderers.SarifLog.PropertyBag;
import static net.sourceforge.pmd.renderers.SarifLog.Region;
import static net.sourceforge.pmd.renderers.SarifLog.ReportingDescriptor;
import static net.sourceforge.pmd.renderers.SarifLog.Result;
import static net.sourceforge.pmd.renderers.SarifLog.Tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.SarifLog.Run;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
class SarifLogBuilder {
    private final Map<ReportingDescriptor, List<Location>> locationsByRule = new HashMap<>();

    public static SarifLogBuilder sarifLogBuilder() {
        return new SarifLogBuilder();
    }

    public SarifLog build() {
        final List<ReportingDescriptor> rules = new ArrayList<>(locationsByRule.keySet());

        final List<Result> results = new ArrayList<>();
        for (int i = 0, size = rules.size(); i < size; i++) {
            ReportingDescriptor rule = rules.get(i);
            List<Location> locations = locationsByRule.get(rule);
            results.add(resultFrom(rule, i, locations));
        }

        final Component driver = getDriverComponent().toBuilder().rules(rules).build();
        final Tool tool = Tool.builder().driver(driver).build();
        final Run run = Run.builder().tool(tool).results(results).build();

        List<SarifLog.Run> runs = Collections.singletonList(run);

        return SarifLog.builder().runs(runs).build();
    }

    public SarifLogBuilder add(RuleViolation violation) {
        final ReportingDescriptor ruleDescriptor = getReportingDescriptor(violation);
        final Location location = getRuleViolationLocation(violation);

        final List<Location> ruleLocation = locationsByRule.containsKey(ruleDescriptor) ? locationsByRule.get(ruleDescriptor) : new ArrayList<Location>();
        ruleLocation.add(location);
        locationsByRule.put(ruleDescriptor, ruleLocation);

        return this;
    }

    private Result resultFrom(ReportingDescriptor rule, Integer ruleIndex, List<Location> locations) {
        final Result result = new Result();
        result.setRuleId(rule.getId());
        result.setRuleIndex(ruleIndex);

        final Message message = new Message();
        message.setText(rule.getShortDescription().getText());
        result.setMessage(message);

        result.setLocations(locations);

        return result;
    }

    private Location getRuleViolationLocation(RuleViolation rv) {
        ArtifactLocation artifactLocation = new ArtifactLocation();
        artifactLocation.setUri(rv.getFilename());

        Region region = new Region();
        region.setStartLine(rv.getBeginLine());
        region.setEndLine(rv.getEndLine());
        region.setStartColumn(rv.getBeginColumn());
        region.setEndColumn(rv.getEndColumn());

        PhysicalLocation physicalLocation = new PhysicalLocation();
        physicalLocation.setArtifactLocation(artifactLocation);
        physicalLocation.setRegion(region);

        Location result = new Location();
        result.setPhysicalLocation(physicalLocation);

        return result;
    }

    private ReportingDescriptor getReportingDescriptor(RuleViolation rv) {
        ReportingDescriptor result = new ReportingDescriptor();

        result.setId(rv.getRule().getName());
        result.setShortDescription(new MultiformatMessage(rv.getDescription()));
        result.setHelpUri(rv.getRule().getExternalInfoUrl());
        result.setProperties(getRuleProperties(rv));

        return result;
    }

    private PropertyBag getRuleProperties(RuleViolation rv) {
        PropertyBag result = new PropertyBag();

        result.setRuleset(rv.getRule().getRuleSetName());
        result.setPriority(rv.getRule().getPriority().getPriority());

        return result;
    }

    private Component getDriverComponent() {
        return Component.builder()
                .name("PMD")
                .version(PMDVersion.VERSION)
                .informationUri("https://pmd.github.io/pmd/")
                .build();
    }
}
