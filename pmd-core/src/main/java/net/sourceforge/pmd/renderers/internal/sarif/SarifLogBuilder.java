/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers.internal.sarif;

import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ArtifactLocation;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.AssociatedRule;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Component;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Exception;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Invocation;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Location;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Message;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.MultiformatMessage;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.PhysicalLocation;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.PropertyBag;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Region;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ReportingDescriptor;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Result;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Run;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Tool;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ToolConfigurationNotification;
import static net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ToolExecutionNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

public class SarifLogBuilder {
    private final Map<ReportingDescriptor, List<Location>> locationsByRule = new HashMap<>();
    private final List<ToolConfigurationNotification> toolConfigurationNotifications = new ArrayList<>();
    private final List<ToolExecutionNotification> toolExecutionNotifications = new ArrayList<>();

    public static SarifLogBuilder sarifLogBuilder() {
        return new SarifLogBuilder();
    }

    public SarifLogBuilder add(RuleViolation violation) {
        final ReportingDescriptor ruleDescriptor = getReportingDescriptor(violation);
        final Location location = getRuleViolationLocation(violation);

        final List<Location> ruleLocation = locationsByRule.containsKey(ruleDescriptor) ? locationsByRule.get(ruleDescriptor) : new ArrayList<Location>();
        ruleLocation.add(location);
        locationsByRule.put(ruleDescriptor, ruleLocation);

        return this;
    }

    public SarifLogBuilder addRunTimeError(Report.ProcessingError error) {
        ArtifactLocation artifactLocation = ArtifactLocation.builder()
                .uri(error.getFile())
                .build();

        PhysicalLocation physicalLocation = PhysicalLocation.builder()
                .artifactLocation(artifactLocation)
                .build();

        Location location = Location
                .builder()
                .physicalLocation(physicalLocation)
                .build();

        Message message = Message.builder()
                .text(error.getMsg())
                .build();

        Exception exception = Exception.builder()
                .message(error.getDetail())
                .build();

        ToolExecutionNotification toolExecutionNotification = ToolExecutionNotification.builder()
                .locations(Collections.singletonList(location))
                .message(message)
                .exception(exception)
                .build();

        toolExecutionNotifications.add(toolExecutionNotification);

        return this;
    }

    public SarifLogBuilder addConfigurationError(Report.ConfigurationError error) {
        AssociatedRule associatedRule = AssociatedRule.builder()
                .id(error.rule().getName())
                .build();

        Message message = Message.builder().text(error.issue()).build();

        ToolConfigurationNotification toolConfigurationNotification = ToolConfigurationNotification.builder()
                .associatedRule(associatedRule)
                .message(message)
                .build();

        toolConfigurationNotifications.add(toolConfigurationNotification);

        return this;
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
        final Invocation invocation = Invocation.builder()
                .toolExecutionNotifications(toolExecutionNotifications)
                .toolConfigurationNotifications(toolConfigurationNotifications)
                .executionSuccessful(isExecutionSuccessful())
                .build();
        final Run run = Run.builder()
                .tool(tool)
                .results(results)
                .invocations(Collections.singletonList(invocation))
                .build();

        List<Run> runs = Collections.singletonList(run);

        return SarifLog.builder().runs(runs).build();
    }

    private boolean isExecutionSuccessful() {
        return toolExecutionNotifications.isEmpty() && toolConfigurationNotifications.isEmpty();
    }

    private Result resultFrom(ReportingDescriptor rule, Integer ruleIndex, List<Location> locations) {
        final Result result = Result.builder()
                .ruleId(rule.getId())
                .ruleIndex(ruleIndex)
                .build();

        final Message message = Message.builder()
                .text(rule.getShortDescription().getText())
                .build();

        result.setMessage(message);
        result.setLocations(locations);

        return result;
    }

    private Location getRuleViolationLocation(RuleViolation rv) {
        ArtifactLocation artifactLocation = ArtifactLocation.builder()
                .uri(rv.getFilename())
                .build();

        Region region = Region.builder()
            .startLine(rv.getBeginLine())
            .endLine(rv.getEndLine())
            .startColumn(rv.getBeginColumn())
            .endColumn(rv.getEndColumn())
            .build();

        PhysicalLocation physicalLocation = PhysicalLocation.builder()
                .artifactLocation(artifactLocation)
                .region(region)
                .build();

        return Location.builder()
            .physicalLocation(physicalLocation)
            .build();
    }

    private ReportingDescriptor getReportingDescriptor(RuleViolation rv) {
        return ReportingDescriptor.builder()
            .id(rv.getRule().getName())
            .shortDescription(new MultiformatMessage(rv.getDescription()))
            .helpUri(rv.getRule().getExternalInfoUrl())
            .properties(getRuleProperties(rv))
            .build();
    }

    private PropertyBag getRuleProperties(RuleViolation rv) {
        return PropertyBag.builder()
                .ruleset(rv.getRule().getRuleSetName())
                .priority(rv.getRule().getPriority().getPriority())
                .build();
    }

    private Component getDriverComponent() {
        return Component.builder()
                .name("PMD")
                .version(PMDVersion.VERSION)
                .informationUri("https://pmd.github.io/pmd/")
                .build();
    }
}
