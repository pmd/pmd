/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers.internal.sarif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ArtifactLocation;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.AssociatedRule;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Component;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Exception;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Invocation;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Location;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Message;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.MultiformatMessage;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.PhysicalLocation;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.PropertyBag;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Region;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ReportingDescriptor;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Result;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Run;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.Tool;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ToolConfigurationNotification;
import net.sourceforge.pmd.renderers.internal.sarif.SarifLog.ToolExecutionNotification;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

public class SarifLogBuilder {
    private final List<ReportingDescriptor> rules = new ArrayList<>();
    private final List<Result> results = new ArrayList<>();
    private final List<ToolConfigurationNotification> toolConfigurationNotifications = new ArrayList<>();
    private final List<ToolExecutionNotification> toolExecutionNotifications = new ArrayList<>();

    public static SarifLogBuilder sarifLogBuilder() {
        return new SarifLogBuilder();
    }

    public SarifLogBuilder add(RuleViolation violation) {
        final ReportingDescriptor ruleDescriptor = getReportingDescriptor(violation);
        int ruleIndex = rules.indexOf(ruleDescriptor);
        if (ruleIndex == -1) {
            rules.add(ruleDescriptor);
            ruleIndex = rules.size() - 1;
        }

        final Location location = getRuleViolationLocation(violation);
        final Result result = resultFrom(ruleDescriptor, ruleIndex, location);
        results.add(result);

        return this;
    }

    public SarifLogBuilder addRunTimeError(Report.ProcessingError error) {
        ArtifactLocation artifactLocation = ArtifactLocation.builder()
                .uri(error.getFileId().getUriString())
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

    private Result resultFrom(ReportingDescriptor rule, Integer ruleIndex, Location location) {
        final Result result = Result.builder()
                .ruleId(rule.getId())
                .ruleIndex(ruleIndex)
                .build();

        final Message message = Message.builder()
                .text(rule.getShortDescription().getText())
                .build();

        result.setMessage(message);
        result.setLocations(Collections.singletonList(location));

        return result;
    }

    private Location getRuleViolationLocation(RuleViolation rv) {
        ArtifactLocation artifactLocation = ArtifactLocation.builder()
                .uri(rv.getFileId().getUriString())
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
            .fullDescription(new MultiformatMessage(rv.getRule().getDescription()))
            .helpUri(rv.getRule().getExternalInfoUrl())
            .help(new MultiformatMessage(rv.getRule().getDescription()))
            .properties(getRuleProperties(rv))
            .build();
    }

    private PropertyBag getRuleProperties(RuleViolation rv) {
        return PropertyBag.builder()
                .ruleset(rv.getRule().getRuleSetName())
                .priority(rv.getRule().getPriority().getPriority())
                .tags(new HashSet<>(Arrays.asList(rv.getRule().getRuleSetName())))
                .build();
    }

    private Component getDriverComponent() {
        return Component.builder()
                .name("PMD")
                .version(PMDVersion.VERSION)
                .informationUri("https://docs.pmd-code.org/latest/")
                .build();
    }
}
